package com.hdu.hdufpga.entity.bo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.BufferedReader;
import java.io.BufferedWriter;

@Data
@AllArgsConstructor
public class SimulationWorkerBO {
    public String workspaceName;
    public Process simulationProcess;
    public BufferedWriter simInput;
    public BufferedReader simOutput;
    public volatile boolean running;

//    public SimulationWorkerBO(String workspaceName, Process simulationProcess, BufferedWriter simInput, BufferedReader simOutput, boolean running) {
//        this.workspaceName = workspaceName;
//        this.simulationProcess = simulationProcess;
//        this.simInput = simInput;
//        this.simOutput = simOutput;
//        this.running = running;
//    }
}
