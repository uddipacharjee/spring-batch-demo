package com.tutorial.springbatchdemo.batch.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomStepExecutionListener implements StepExecutionListener {
    @Override
    public void beforeStep(StepExecution stepExecution) {

        log.info("Before step: {}", stepExecution.getStepName());
        log.info("Initial ExecutionContext: {}", stepExecution.getExecutionContext());
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {

        log.info("After step: {}", stepExecution.getStepName());
        log.info("ExecutionContext after step: {}", stepExecution.getExecutionContext());
        log.info("Read count: {}, Write count: {}, Skip count: {}",
                stepExecution.getReadCount(),
                stepExecution.getWriteCount(),
                stepExecution.getSkipCount());
        return stepExecution.getExitStatus();
    }
}
