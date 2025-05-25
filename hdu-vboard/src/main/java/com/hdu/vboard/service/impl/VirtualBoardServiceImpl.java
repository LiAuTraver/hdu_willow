package com.hdu.vboard.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONObject;
import com.hdu.vboard.entity.bo.SimulationWorkerBO;
import com.hdu.vboard.exception.CreateWorkbenchException;
import com.hdu.vboard.exception.MakeWorkbenchException;
import com.hdu.vboard.service.VirtualBoardService;
import com.hdu.vboard.service.logProcessService;
import com.hdu.vboard.util.VbSysFileUtil;
import com.hdu.vboard.util.VirtualBoardUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.*;
import java.util.HashMap;

@Slf4j
@Service
public class VirtualBoardServiceImpl implements VirtualBoardService {
  final HashMap<String, SimulationWorkerBO> simulationWorkers = new HashMap<>();

  @Resource
  logProcessService logProcessService;

  @Override
  public Boolean createWorkbench(String workspaceName, String verilogFullPath, String bindFullPath) throws Exception {
    if (!FileUtil.exist(verilogFullPath)) {
      throw new CreateWorkbenchException("verilog file does not exist");
    }
    if (!FileUtil.exist(bindFullPath)) {
      throw new CreateWorkbenchException("bind file does not exist");
    }

    // 脚本路径
    String scriptFullPath = VbSysFileUtil.getRootBasePath() + "src/main/python/script/create_workbench.py";


    ProcessBuilder builder = new ProcessBuilder(
        "python3", scriptFullPath,
        "--workspace-name", workspaceName,
        "--verilog-file", verilogFullPath,
        "--bind-json", bindFullPath);
    builder.directory(new File(VbSysFileUtil.getFullWorkbenchPath("")));
    builder.redirectErrorStream(true);
    log.debug("workbench path:{}", VbSysFileUtil.getFullWorkbenchPath(""));

    Process createProcess = builder.start();
    logProcessService.logProcess(createProcess);
    int exitCode = createProcess.waitFor();
    if (exitCode != 0) {
      FileUtil.del(VbSysFileUtil.getFullWorkbenchPath(workspaceName));
      throw new CreateWorkbenchException("Error creating simulation workspace, clear \" " + workspaceName);
    }
    log.debug("Success creating workbench");
    return true;
  }

  @Override
  public Boolean checkWorkbench(String workspaceName) throws Exception {
    ProcessBuilder runBuilder = new ProcessBuilder("make");
    if (!FileUtil.exist(VbSysFileUtil.getFullWorkbenchPath(workspaceName))) {
      throw new CreateWorkbenchException("workbench does not exist");
    }

    runBuilder.directory(new File(VbSysFileUtil.getFullWorkbenchPath(workspaceName)));
    runBuilder.redirectErrorStream(true);

    Process process = runBuilder.start();
    logProcessService.logProcess(process);
    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

    int exitCode = process.waitFor();
    if (exitCode != 0) {
      String errMsg = "Error making workbench: " + reader;
      throw new MakeWorkbenchException(errMsg);
    }
    return true;
  }

  @Override
  public SimulationWorkerBO runWorkbench(String workspaceName) throws Exception {
    ProcessBuilder runBuilder = new ProcessBuilder("make", "run");
    String workbenchPath = VbSysFileUtil.getFullWorkbenchPath(workspaceName);
    if (!FileUtil.exist(workbenchPath)) {
      throw new CreateWorkbenchException("workbench does not exist");
    }

    runBuilder.directory(new File(workbenchPath));
    runBuilder.redirectErrorStream(true);

    Process simProcess = runBuilder.start();

    BufferedWriter simInput = new BufferedWriter(new OutputStreamWriter(simProcess.getOutputStream()));
    BufferedReader simOutput = new BufferedReader(new InputStreamReader(simProcess.getInputStream()));

    SimulationWorkerBO simulationWorkerBO = new SimulationWorkerBO(workspaceName, simProcess, simInput, simOutput,
        true);
    log.info("Simulation process started for workspace: {}", workspaceName);
    simulationWorkers.put(workspaceName, simulationWorkerBO);
    return simulationWorkerBO;
  }

  @Override
  public Boolean sendSignal(String workspaceName, String signal) throws Exception {
    SimulationWorkerBO simulationWorkerBO = simulationWorkers.get(workspaceName);
    if (simulationWorkerBO == null) {
      throw new MakeWorkbenchException("simulation workbench does not exist");
    }
    VirtualBoardUtil.sendSignalToVirtualBoard(simulationWorkerBO.simInput, signal);
    return true;
  }

  @Override
  public JSONObject getSignalFromVirtualBoard(String workspaceName) throws Exception {
    SimulationWorkerBO simulationWorkerBO = simulationWorkers.get(workspaceName);
    if (simulationWorkerBO == null) {
      throw new MakeWorkbenchException("simulation workbench does not exist");
    }
    return VirtualBoardUtil.getSignalFromVirtualBoard(simulationWorkerBO.simOutput);
  }

  @Override
  public Boolean stopWorkbench(String workspaceName) throws Exception {
    SimulationWorkerBO simulationWorkerBO = simulationWorkers.remove(workspaceName);
    if (simulationWorkerBO == null) {
      throw new MakeWorkbenchException("simulation workbench does not exist");
    }
    simulationWorkerBO.running = false;
    simulationWorkerBO.simInput.write((char) -1);
    if (simulationWorkerBO.simulationProcess.isAlive()) {
      simulationWorkerBO.simulationProcess.destroy();
      log.debug("workbench:{} stopped!", workspaceName);
    }
    VbSysFileUtil.deleteDirectory(new File(VbSysFileUtil.getFullWorkbenchPath(workspaceName)));
    log.debug("workbench:{} cleared!", workspaceName);
    return true;
  }

  @Override
  public Boolean clearWorkbench(String workspaceName) throws Exception {
    String workbenchFullPath = VbSysFileUtil.getFullWorkbenchPath(workspaceName);
    if (!FileUtil.exist(workbenchFullPath)) {
      throw new CreateWorkbenchException("workbench does not exist");
    }
    VbSysFileUtil.deleteDirectory(new File(workbenchFullPath));
    return true;
  }
}
