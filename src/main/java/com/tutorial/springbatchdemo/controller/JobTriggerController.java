package com.tutorial.springbatchdemo.controller;

import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/load")
public class JobTriggerController {
    @Autowired
    private JobLauncher jobLauncher;


//    @Autowired
//    private @Qualifier("transactionLogHandlerJob") Job transactionLogHandlerJob;
//
//    @GetMapping
//    public void load() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
//        JobParameters jobParameters = new JobParametersBuilder().addDate("date", new Date())
//                .addLong("time", System.currentTimeMillis()).toJobParameters();
//
//
//        JobExecution execution = jobLauncher.run(transactionLogHandlerJob, jobParameters);
//        System.out.println("STATUS :: " + execution.getStatus());
//    }

        @Autowired
    private @Qualifier("transactionLogHandlerJobWithDecider") Job transactionLogHandlerJob;

    @GetMapping
    public void load() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        JobParameters jobParameters = new JobParametersBuilder().addDate("date", new Date())
                .addLong("time", System.currentTimeMillis()).toJobParameters();


        JobExecution execution = jobLauncher.run(transactionLogHandlerJob, jobParameters);
        System.out.println("STATUS :: " + execution.getStatus());
    }
}
