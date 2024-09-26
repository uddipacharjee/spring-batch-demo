package com.tutorial.springbatchdemo.batch.async;

import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AsyncConfig {
    @Bean("jobLauncherTaskExecutor")
    public TaskExecutor jobLauncherTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(10);  // Adjust pool size according to your needs
        taskExecutor.setMaxPoolSize(20);
        taskExecutor.setQueueCapacity(50);
        taskExecutor.setThreadNamePrefix("Async-Job-");
        taskExecutor.initialize();
        return taskExecutor;
    }


    @Bean("StepTaskExecutor")
    public TaskExecutor stepTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);  // Minimum number of threads to keep in the pool
        executor.setMaxPoolSize(50);   // Maximum number of threads to create
        executor.setQueueCapacity(100);  // Queue size for tasks waiting for an available thread
        executor.setThreadNamePrefix("StepTaskExecutor-");
        executor.initialize();  // Required to start the pool
        return executor;
    }


    @Bean("asyncJobLauncher")
    public JobLauncher asyncJobLauncher(@Qualifier("jp")JobRepository jobRepository, @Qualifier("jobLauncherTaskExecutor") TaskExecutor taskExecutor) throws Exception {
        TaskExecutorJobLauncher jobLauncher = new TaskExecutorJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        jobLauncher.setTaskExecutor(taskExecutor); // Set async TaskExecutor
        jobLauncher.afterPropertiesSet();
        return jobLauncher;
    }


}
