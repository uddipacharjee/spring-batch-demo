package com.tutorial.springbatchdemo.batch.step;

import com.tutorial.springbatchdemo.batch.processor.StudentProcessor;
import com.tutorial.springbatchdemo.listener.CustomStepExecutionListener;
import com.tutorial.springbatchdemo.model.Student;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class StudentFileEntryStep {

    private final CustomStepExecutionListener stepExecutionListener;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final StudentProcessor processor;
    @Autowired
    @Qualifier("StepTaskExecutor")
    private TaskExecutor stepTaskExecutor;

    @Bean("studentFixedLengthStep")
    public Step step2(@Qualifier("studentFixedLengthReader") FlatFileItemReader<Student> reader,
                      @Qualifier("studentRepositoryWriter") RepositoryItemWriter<Student> writer) {
        return new StepBuilder("importFixedLengthTxt", jobRepository)
                .<Student, Student>chunk(1000, platformTransactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .listener(stepExecutionListener)
                .taskExecutor(stepTaskExecutor)
                .build();
    }

    @Bean("csvStudentFileStep")
    public Step step1(@Qualifier("csvReader") FlatFileItemReader<Student> reader,
                      @Qualifier("studentRepositoryWriter") RepositoryItemWriter<Student> writer) {
        return new StepBuilder("csvImport", jobRepository)
                .<Student, Student>chunk(1000, platformTransactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .taskExecutor(stepTaskExecutor)
                .build();
    }
}
