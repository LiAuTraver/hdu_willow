package com.hdu.hdufpga.service;

import hdu.svccmn.exception.EmptyHistoryStepsException;

public interface CircuitBoardHistoryOperationService {
    void clearSteps(String token);

    Boolean loadOperationHistory(String token) throws EmptyHistoryStepsException;

    void saveOperationStep(String token, String step);
}
