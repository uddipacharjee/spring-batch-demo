package com.tutorial.springbatchdemo.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;

@Slf4j
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("Operation[JOB.START] DATE_TIME[{}]", jobExecution.getStartTime());
        log.info("Operation[STEP.START] Name[{}]", jobExecution.getJobConfigurationName());
        super.beforeJob(jobExecution);
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        log.info("Operation[JOB.END] DATE_TIME[{}]", jobExecution.getEndTime());

        super.afterJob(jobExecution);
    }
}
