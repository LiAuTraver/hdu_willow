package com.hdu.vboard.executor;

import lombok.extern.slf4j.Slf4j;
import cn.hutool.json.JSONObject;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.hdu.vboard.executor.WorkspaceCleaner.deleteDirectory;

@Slf4j
public class SimulationWorker {
  public final static ConcurrentHashMap<String, SimulationWorker> Workers = new ConcurrentHashMap<>();

  private final String workspaceName;
  private final ExecutorService executorService;
  private Process simulationProcess;
  private BufferedWriter simInput;
  private BufferedReader simOutput;
  private final WebSocketSession session;
  private volatile boolean running = true;

  public enum SimulationResponse {
    Ok, FailedCreateWorkbench, FailedMakeWorkbench, ErrorWhileSimulation
  }

  public SimulationWorker(String workspaceName, WebSocketSession session) {
    this.workspaceName = workspaceName;
    this.session = session;
    this.executorService = Executors.newSingleThreadExecutor();
  }

  public SimulationResponse StartSimulation(String verilogPath, String bindPath) throws Exception {
    // Step 1: 调用 Python 脚本创建工作区
    ProcessBuilder builder = new ProcessBuilder(
        "python3", "./script/create_workbench.py",
        "--workspace-name", workspaceName,
        "--verilog-file", verilogPath,
        "--bind-json", bindPath
    );
    builder.redirectErrorStream(true);
    Process createProcess = builder.start();
    logProcessOutput(createProcess);
    int exitCode = createProcess.waitFor();
    Path workspaceDir = Paths.get(workspaceName);
    if (exitCode != 0) {
      log.warn("Error creating simulation workspace, clear \" {}", workspaceName);
      clearWorkSpace(workspaceDir);
      return SimulationResponse.FailedCreateWorkbench;
    }

    // Step 2: 构建仿真环境
    builder = new ProcessBuilder("make");
    builder.directory(workspaceDir.toFile());
    builder.redirectErrorStream(true);
    Process makeProcess = builder.start();
    logProcessOutput(makeProcess);
    exitCode = makeProcess.waitFor();
    if (exitCode != 0) {
      log.warn("Error making workspace, clear \" {}", workspaceName);
      clearWorkSpace(workspaceDir);
      return SimulationResponse.FailedMakeWorkbench;
    }

    // Step 3: 启动仿真环境
    builder = new ProcessBuilder("make", "run");
    builder.directory(workspaceDir.toFile());
    builder.redirectErrorStream(true);
    simulationProcess = builder.start();
    simInput = new BufferedWriter(new OutputStreamWriter(simulationProcess.getOutputStream()));
    simOutput = new BufferedReader(new InputStreamReader(simulationProcess.getInputStream()));

    // Step 4: 开启线程读取输出
    executorService.submit(this::readSimulationOutput);

    return SimulationResponse.Ok;
  }

  public void SendSignal(String signalData) throws IOException {
    log.debug("received signal!");
    log.debug(signalData);

    if (simInput != null) {
      simInput.write(signalData + "\n");
      simInput.flush();
    }
  }

  private void stopSimulation() {
    running = false;
    executorService.shutdownNow();
    if (simulationProcess != null) {
      simulationProcess.destroy();
    }
    try {
      clearWorkSpace(Paths.get(workspaceName));
    } catch (IOException e) {
      log.error(e.getMessage());
    }
  }

  private void readSimulationOutput() {
    try {
      String line;
      while (running && (line = simOutput.readLine()) != null) {
        try {
          JSONObject signalJson = new JSONObject(line);
          if (session != null && session.isOpen()) {
            JSONObject outputJson = new JSONObject()
                .put("type", "signal")
                .put("data", signalJson);

            log.debug(outputJson.toString());
            session.sendMessage(new TextMessage(signalJson.toJSONString(4)));
          }
        } catch (Exception e) {
          log.warn("Failed to parse output: {}", line);
        }
      }
    } catch (IOException e) {
      log.error("Error reading simulation output: {}", e.getMessage());
    }
  }

  private void logProcessOutput(Process process) throws IOException {
    try(BufferedReader reader = Files.newBufferedReader(Paths.get(process.getInputStream().toString()))){
      String line;
      while ((line = reader.readLine()) != null) {
        log.debug(line);
      }
    }
  }

  private void clearWorkSpace(Path directory) throws IOException {
    deleteDirectory(directory);
  }

  public static boolean stopSimulationWorker(String sessionId) {
    SimulationWorker worker = Workers.remove(sessionId);
    if (worker == null) {

      return false;
    }

    worker.stopSimulation();
    return true;
  }
}
