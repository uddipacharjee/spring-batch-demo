package com.tutorial.springbatchdemo.batch.job;

import com.tutorial.springbatchdemo.batch.reader.FixedLengthFileReaderFactory;
import com.tutorial.springbatchdemo.batch.processor.StudentProcessor;
import com.tutorial.springbatchdemo.listener.CustomStepExecutionListener;
import com.tutorial.springbatchdemo.listener.JobCompletionNotificationListener;
import com.tutorial.springbatchdemo.model.Student;
import com.tutorial.springbatchdemo.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class StudentJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    private final StudentProcessor processor;
    private final JobCompletionNotificationListener jobCompletionNotificationListener;


    @Bean("studentFixedLengthJob")
    public Job runJob2(@Qualifier("studentFixedLengthStep") Step step) {
        return new JobBuilder("importStudentsFixedLength", jobRepository)
                .start(step)
                .listener(jobCompletionNotificationListener)
                .build();

    }


    @Bean("studentJob")
    public Job runJob(@Qualifier("csvStudentFileStep") Step step) {
        return new JobBuilder("importStudentsCSV", jobRepository)
                .start(step)
                .build();

    }





}
