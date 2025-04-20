package com.hdu.hduvboard.service.impl;

import com.hdu.hduvboard.service.logProcessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Slf4j
@Service
public class logProcessServiceImpl implements logProcessService {
    @Override
    public void logProcess(Process process) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                log.debug(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
