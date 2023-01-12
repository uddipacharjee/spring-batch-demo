package com.tutorial.springbatchdemo.batch.decider;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.stereotype.Component;
@Slf4j
@Component
public class TransactionLogExecutionDecider implements JobExecutionDecider {
    @Override
    public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
        String rgEnabled = jobExecution.getJobParameters().getString("rgEnabled");
        log.info("decider rgEnabled: {}",rgEnabled);
        if(rgEnabled !=null && rgEnabled.equalsIgnoreCase("YES")){
            return new FlowExecutionStatus("YES");
        }
        return new FlowExecutionStatus("NO");
    }
}
