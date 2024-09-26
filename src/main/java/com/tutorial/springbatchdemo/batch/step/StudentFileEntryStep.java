package com.tutorial.springbatchdemo.batch.step;

import com.tutorial.springbatchdemo.batch.processor.StudentProcessor;
import com.tutorial.springbatchdemo.batch.listener.CustomStepExecutionListener;
import com.tutorial.springbatchdemo.model.Student;
import com.tutorial.springbatchdemo.util.BeanNames;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import static com.tutorial.springbatchdemo.util.BeanNames.Reader.STUDENT_CSV_READER;
import static com.tutorial.springbatchdemo.util.BeanNames.Reader.STUDENT_FIXED_LENGTH_READER;
import static com.tutorial.springbatchdemo.util.BeanNames.Writer.STUDENT_REPOSITORY_WRITER;

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

    @Bean(BeanNames.Step.STUDENT_FIXED_LENGTH_STEP)
    public Step step2(@Qualifier(STUDENT_FIXED_LENGTH_READER) SynchronizedItemStreamReader<Student> reader,
                      @Qualifier(STUDENT_REPOSITORY_WRITER) RepositoryItemWriter<Student> writer) {
        return new StepBuilder("importFixedLengthTxt", jobRepository)
                .<Student, Student>chunk(100, platformTransactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .faultTolerant()
                .retryLimit(3) // Retry failed operations up to 3 times
                .retry(Exception.class) // Retry on exceptions
                //.skipLimit(5) // Skip up to 5 items before failing the step
                //.skip(Exception.class) //
                .listener(stepExecutionListener)
                .taskExecutor(stepTaskExecutor)
                .build();
    }

    @Bean(BeanNames.Step.STUDENT_CSV_STUDENT_STEP)
    public Step step1(@Qualifier(STUDENT_CSV_READER) FlatFileItemReader<Student> reader,
                      @Qualifier(STUDENT_REPOSITORY_WRITER) RepositoryItemWriter<Student> writer) {
        return new StepBuilder("csvImport", jobRepository)
                .<Student, Student>chunk(1000, platformTransactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .taskExecutor(stepTaskExecutor)
                .build();
    }
}
