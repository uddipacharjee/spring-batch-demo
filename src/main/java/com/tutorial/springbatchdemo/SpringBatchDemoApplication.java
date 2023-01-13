package com.tutorial.springbatchdemo;

import com.tutorial.springbatchdemo.model.enums.JobType;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;

@SpringBootApplication()
public class SpringBatchDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBatchDemoApplication.class, args);
    }

    @Bean
    CommandLineRunner run(
            @Autowired JobLauncher jobLauncher,
            @Qualifier("randTransactionGenJob") Job randTransactionGenJob,
            @Qualifier("randTransactionProcessJob") Job randTransactionProcessJob,
            @Qualifier("transactionLogHandlerJob") Job transactionLogHandlerJob
    ) {
        return args -> {
            JobParameters jobParameters = new JobParametersBuilder().addDate("date", new Date())
                    .addLong("time", System.currentTimeMillis()).toJobParameters();

            JobType type = JobType.getByStrValue(args[0]);
            Job launchableJob = transactionLogHandlerJob;

            switch (type) {
                case TR_GEN:
                    launchableJob = randTransactionGenJob;
                    break;
                case TR_PROCESS:
                    launchableJob = randTransactionProcessJob;
                    break;
                case TR_GEN_AND_PROCESS:
                    launchableJob = transactionLogHandlerJob;
                    break;
                default:
                    break;
            }
            JobExecution execution = jobLauncher.run(launchableJob, jobParameters);
            System.out.println("STATUS :: " + execution.getStatus());
        };
    }
}
