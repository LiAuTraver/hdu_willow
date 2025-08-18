package com.hdu.vboard.util;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.BufferedWriter;

@Slf4j
public class VirtualBoardUtil {
  public static void sendSignalToVirtualBoard(BufferedWriter simInput, String signal_json) throws Exception {
    if (simInput == null) {
      throw new Exception("simInput is null");
    }
    simInput.write(signal_json + "\n");
    simInput.flush();
  }

  public static JSONObject getSignalFromVirtualBoard(BufferedReader simOutput) throws Exception {
    String line;
    if (simOutput == null) {
      throw new Exception("simOutput is null");
    }
    if ((line = simOutput.readLine()) != null) {
      JSONObject signalData = JSONUtil.parseObj(line);
      JSONObject outputJson = new JSONObject();
      outputJson.set("data", signalData);
      return outputJson;
    } else {
      return null;
    }
  }
}
