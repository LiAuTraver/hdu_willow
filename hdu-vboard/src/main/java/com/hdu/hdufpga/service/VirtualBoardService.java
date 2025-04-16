package com.hdu.hdufpga.service;

import com.alibaba.fastjson.JSONObject;
import com.hdu.hdufpga.entity.bo.SimulationWorkerBO;

public interface VirtualBoardService {
    Boolean createWorkbench(String workspaceName, String verilogPath, String bindPath) throws Exception;

    Boolean checkWorkbench(String workspaceName) throws Exception;

    SimulationWorkerBO runWorkbench(String workspaceName) throws Exception;

    Boolean sendSignal(String workspaceName, String signal) throws Exception;

    JSONObject getSignalFromVirtualBoard(String workspaceName) throws Exception;

    Boolean stopWorkbench(String workspaceName) throws Exception;

    Boolean clearWorkbench(String workspaceName) throws Exception;
}
