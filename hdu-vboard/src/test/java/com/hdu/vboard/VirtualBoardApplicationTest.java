package com.hdu.vboard;

import cn.hutool.json.JSONObject;
import lombok.SneakyThrows;
import lombok.var;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
class SimulationTest {
  private Process process;
  private BufferedReader simOutput;
  private BufferedWriter simInput;
  private ExecutorService executorService;

  @SneakyThrows
  @BeforeEach
  void setup() {
    var builder = new ProcessBuilder("make", "run");
    builder.redirectErrorStream(true);
    builder.directory(Paths.get("test").toFile());
    process = builder.start();
    simInput = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
    simOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));
    executorService = Executors.newSingleThreadExecutor();
  }

  @SneakyThrows
  @AfterEach
  void teardown() {
    if (simInput != null) {
      simInput.close();
    }
    if (simOutput != null) {
      simOutput.close();
    }
    executorService.shutdown();
  }

  @SneakyThrows
  @Test
  void testSimulator() {
    executorService.submit(this::doReadLine);
    var jsonInputs = "{ `SW7`:0,`SW6`:1,`SW5`:0,`SW4`:1,`SW3`:0,`SW2`:1,`SW1`:1,`SW0`:0 }".replace('`', '"');
    for (int i = 0; i < 10; i++) {
      if (i == 3) {
        simInput.write(jsonInputs + "\n"); // 写入 JSON 格式输入
        simInput.flush(); // 确保立即发送输入到仿真程序
      }

      if (i == 9) {
        System.out.println("Simulating EOF at 10th second.");
        simInput.close(); // 模拟 EOF，关闭输入流
        break;
      }

      Thread.sleep(1000); // 等待 1 秒，观察仿真结果
    }
    System.out.println("Out of break!");
    // 等待仿真程序完成执行
    process.waitFor();
  }

  @SneakyThrows
  private void doReadLine()  {
    var line = "";
    while ((line = simOutput.readLine()) != null) {
      try {
        // 解析仿真输出为 JSON 对象
        JSONObject outputJson = new JSONObject(line);
        System.out.println("Simulator Output: " + outputJson.toJSONString(4)); // 格式化输出
      } catch (Exception e) {
        System.err.println("Failed to parse simulator output as JSON: " + line);
      }
    }
  }
}