package com.hdu.vboard.service;

import cn.hutool.json.JSONObject;
import com.hdu.hdufpga.entity.Result;
import com.hdu.hdufpga.entity.vo.UserVO;
import com.hdu.vboard.entity.bo.SimulationWorkerBO;

import java.util.List;

public interface VirtualBoardService {
  Boolean createWorkbench(String workspaceName, List<String> verilogPath, String bindPath) throws Exception;

  Boolean checkWorkbench(String workspaceName) throws Exception;

  SimulationWorkerBO runWorkbench(String workspaceName) throws Exception;

  Boolean sendSignal(String workspaceName, String signal) throws Exception;

  JSONObject getSignalFromVirtualBoard(String workspaceName) throws Exception;

  Boolean stopWorkbench(String workspaceName) throws Exception;

  Boolean clearWorkbench(String workspaceName) throws Exception;

  Result generateToken(UserVO userVO);
}
