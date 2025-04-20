package com.hdu.hduvboard.util;

import cn.hutool.json.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

@Slf4j
public class VirtualBoardUtil {
    public static void sendSignalToVirtualBoard(BufferedWriter simInput, String signal_json) throws Exception {
        if (simInput == null) {
            throw new Exception("simInput is null");
        }
        simInput.write(signal_json+"\n");
        simInput.flush();
        log.debug("send signal to virtual board:"+signal_json);
    }

    public static JSONObject getSignalFromVirtualBoard(BufferedReader simOutput) throws Exception {
        String line;
        if (simOutput == null) {
            throw new Exception("simOutput is null");
        }
        log.debug("Now to get signal from virtual board");
        if ((line = simOutput.readLine()) != null) {
            JSONObject outputJson = new JSONObject();
            outputJson.put("data", line);
            return outputJson;
        } else {
            return null;
        }
    }
}
