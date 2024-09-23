package com.tutorial.springbatchdemo.batch.job;

import com.tutorial.springbatchdemo.listener.JobCompletionNotificationListener;
import com.tutorial.springbatchdemo.util.BeanNames;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class StudentJobConfig {

    private final JobRepository jobRepository;

    private final JobCompletionNotificationListener jobCompletionNotificationListener;


    @Bean(BeanNames.Job.STUDENT_FIXED_LENGTH)
    public Job runJob2(@Qualifier(BeanNames.Step.STUDENT_FIXED_LENGTH_STEP) Step step) {
        return new JobBuilder("importStudentsFixedLength", jobRepository)
                .start(step)
                .listener(jobCompletionNotificationListener)
                .build();

    }


    @Bean(BeanNames.Job.STUDENT_CSV)
    public Job runJob(@Qualifier(BeanNames.Step.STUDENT_CSV_STUDENT_STEP) Step step) {
        return new JobBuilder("importStudentsCSV", jobRepository)
                .start(step)
                .build();
    }
}
