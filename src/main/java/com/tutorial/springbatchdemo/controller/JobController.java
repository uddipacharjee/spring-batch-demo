package com.tutorial.springbatchdemo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.*;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/jobs")
@Slf4j
public class JobController {

    @Autowired
    private JobRegistry jobRegistry;
    @Autowired
    private @Qualifier("asyncJobLauncher") JobLauncher jobLauncher;
    @Autowired
    private  JobExplorer jobExplorer;
    @Autowired
    private  JobOperator jobOperator;

    @GetMapping("/list")
    public List<String> listAllJobs() {
        return new ArrayList<>(jobRegistry.getJobNames());
    }

    @Async
    @GetMapping("/async/{jobName}")
    public CompletableFuture<String> loadAsync(@PathVariable String jobName) throws JobInstanceAlreadyCompleteException,
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

    @GetMapping("/{jobName}")
    public void load(@PathVariable String jobName) throws JobInstanceAlreadyCompleteException,
            JobExecutionAlreadyRunningException,
            JobParametersInvalidException,
            JobRestartException {
        try {
            Job job = jobRegistry.getJob(jobName);

            JobParameters jobParameters = new JobParametersBuilder()
                    //.addDate("date", new Date())
                    .addLong("time", System.currentTimeMillis())
                    .addString("filePath","src/main/resources/students-fixed-length.txt")
                    .toJobParameters();

            JobExecution execution = jobLauncher.run(job, jobParameters);
            System.out.println("STATUS :: " + execution.getStatus());

        } catch (NoSuchJobException e) {
            throw new IllegalArgumentException("Job not found: " + jobName);
        }
    }
    @GetMapping("/restart/{jobName}")
    public void restart(@PathVariable String jobName) throws NoSuchJobException {

            Job job = jobRegistry.getJob(jobName);

            JobInstance lastJobInstance = this.jobExplorer.getLastJobInstance(job.getName());
            JobExecution lastExecution = this.jobExplorer.getLastJobExecution(lastJobInstance);
            try {
                assert lastExecution != null;
                long executionId = this.jobOperator.restart(lastExecution.getId());
                JobExecution newExecution = this.jobExplorer.getLastJobExecution(lastJobInstance);
                assert newExecution != null;
                //return Optional.of(newExecution);
            } catch (JobInstanceAlreadyCompleteException e) {
                log.error("Job: {} is already complete with execution if: {}", job.getName(), lastExecution.getId());
            } catch (NoSuchJobExecutionException e) {
                log.error("Job execution {} for {} job does not exist", lastExecution.getId(), job.getName());
            } catch (NoSuchJobException e) {
                log.error("Job: {} does not exist", job.getName());
            } catch (JobRestartException e) {
                log.error("Job: {} could not be restarted", job.getName());
            } catch (JobParametersInvalidException e) {
                log.error("Invalid parameters: {} provided for job: {}", lastExecution.getJobParameters(), job.getName());
            }
           // return Optional.empty();

            //JobExecution execution = jobLauncher.run(job, jobParameters);
            //System.out.println("STATUS :: " + execution.getStatus());
    }

    @PostMapping("/stop-job/{jobName}")
    public void stopJob(@PathVariable String jobName) {
        // Retrieve the last job instance
        JobInstance lastInstance = this.jobExplorer.getLastJobInstance(jobName);
        if (lastInstance == null) {
            log.warn("No job instance found for job: {}", jobName);
            return;
        }

        // Retrieve the last job execution
        JobExecution lastExecution = this.jobExplorer.getLastJobExecution(lastInstance);
        if (lastExecution == null || lastExecution.isStopping() || lastExecution.getStatus() == BatchStatus.STOPPED) {
            log.warn("Job execution is already stopped or stopping for job: {}", jobName);
            return;
        }

        // Stop the job execution
        try {
            log.info("Attempting to stop job execution with ID: {}", lastExecution.getId());
            this.jobOperator.stop(lastExecution.getId());
            log.info("Job {} is being stopped.", jobName);
        } catch (NoSuchJobExecutionException | JobExecutionNotRunningException e) {
            log.error("Error stopping job: {}", jobName, e);
        }
    }


}