package com.tutorial.springbatchdemo;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.converter.DefaultJobParametersConverter;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
@SpringBootApplication()
public class SpringBatchDemoApplication {

    public static void main(String[] args) throws Exception {
        if (args.length != 0 && args.length % 2 == 1) {
            // First parameter is jobname
            String jobName = args[0];
            Map<String, String> params = new HashMap<String, String>();
            for (int i=1; i < args.length; i+=2) {
                String key = args[i];;
                String value = args[i+1];;
                params.put(key.replace("-", ""), value);
            }
            // TODO need to consider double run prevention
            params.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));

            // Start
            ConfigurableApplicationContext context = SpringApplication.run(SpringBatchDemoApplication.class, args);
            JobLauncher jobLauncher = context.getBean("jobLauncher", JobLauncher.class);
            Properties property = new Properties();
            for (Map.Entry<String, String> set : params.entrySet()) {
                property.put(set.getKey(), set.getValue());
            }
            JobParameters jobParameters = new DefaultJobParametersConverter().getJobParameters(property);
            Job job = (Job)context.getBean(jobName, Job.class);
            JobExecution jobExecution = jobLauncher.run(job, jobParameters);
            //SpringApplication.exit(context);
        }
        else {
            // Error
            throw new Exception("Need at least one argument jobName and parameter should be --key value");
        }
    }

//    @Bean
//    CommandLineRunner run(
//            @Autowired JobLauncher jobLauncher,
//            @Qualifier("transactionLogHandlerJobWithDecider") Job job
//    ) {
//        return args -> {
//            JobParameters jobParameters = new JobParametersBuilder().addDate("date", new Date())
//                    .addLong("time", System.currentTimeMillis()).toJobParameters();
//
//
//            JobExecution execution = jobLauncher.run(job, jobParameters);
//            System.out.println("STATUS :: " + execution.getStatus());
//        };
//    }
}
