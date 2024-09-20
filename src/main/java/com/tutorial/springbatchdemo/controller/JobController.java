package com.tutorial.springbatchdemo.controller;

import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/jobs")
public class JobController {

    @Autowired
    private JobRegistry jobRegistry;
    @Autowired
    private @Qualifier("asyncJobLauncher") JobLauncher jobLauncher;

    @GetMapping("/list")
    public List<String> listAllJobs() {
        return new ArrayList<>(jobRegistry.getJobNames());
    }

    @Async
    @GetMapping("/{jobName}")
    public CompletableFuture<String> load(@PathVariable String jobName) throws JobInstanceAlreadyCompleteException,
            JobExecutionAlreadyRunningException,
            JobParametersInvalidException,
            JobRestartException {
        try {
            Job job = jobRegistry.getJob(jobName);

            JobParameters jobParameters = new JobParametersBuilder()
                    .addDate("date", new Date())
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

            JobExecution execution = jobLauncher.run(job, jobParameters);
            System.out.println("STATUS :: " + execution.getStatus());
            return CompletableFuture.completedFuture("Job " + jobName + " started. Status: " + execution.getStatus());
        } catch (NoSuchJobException e) {
            throw new IllegalArgumentException("Job not found: " + jobName);
        }
    }


}