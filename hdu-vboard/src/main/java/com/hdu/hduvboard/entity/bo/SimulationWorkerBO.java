package com.hdu.hduvboard.entity.bo;

import com.hdu.hduvboard.exception.SimulationException;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

@Data
@AllArgsConstructor
public class SimulationWorkerBO {
    public String workspaceName;
    public Process simulationProcess;
    public BufferedWriter simInput;
    public BufferedReader simOutput;
    public volatile boolean running;

    public String getOutput() throws IOException {
        if (simulationProcess != null) {
            String line;
            if ((line = simOutput.readLine()) != null) {
                return line;
            }
        }
        throw new SimulationException("Simulation process is null");
    }
}
