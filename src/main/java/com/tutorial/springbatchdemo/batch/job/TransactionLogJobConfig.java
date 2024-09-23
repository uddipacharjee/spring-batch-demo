package com.tutorial.springbatchdemo.batch.job;

import com.tutorial.springbatchdemo.listener.JobCompletionNotificationListener;
import com.tutorial.springbatchdemo.model.Student;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.FixedLengthTokenizer;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import static com.tutorial.springbatchdemo.util.BeanNames.Job.*;

@Configuration
//@EnableBatchProcessing
@RequiredArgsConstructor
public class TransactionLogJobConfig {

    private final JobRepository jobRepository;

    private final JobCompletionNotificationListener jobCompletionListener;



    @Bean(TRANSACTION_LOG_HANDLER)
    public Job transactionLogHandlerJob(
            @Qualifier("randomGeneratorStep") Step randomGeneratorStep,
            @Qualifier("transactionLogStep") Step transactionLogStep) {

        return new JobBuilder("transactionLogHandlerJob",jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(jobCompletionListener)
                .start(randomGeneratorStep)
                .next(transactionLogStep)
                .build();
    }

    @Bean(RANDOM_TRANSACTION_GENERATOR)
    public Job randTransactionGenJob(
            @Qualifier("randomGeneratorStep") Step randomGeneratorStep) {

        return new JobBuilder("genTransactionRandom",jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(jobCompletionListener)
                .start(randomGeneratorStep)
                .build();
    }

    @Bean(RANDOM_TRANSACTION_PROCESS)
    public Job randTransactionProcessJob(
            @Qualifier("transactionLogStep") Step transactionLogStep) {

        return new JobBuilder("processTransaction",jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(jobCompletionListener)
                .start(transactionLogStep)
                .build();
    }


    @Bean
    public FlatFileItemReader<Student> fixedLengthReader() {
        FlatFileItemReader<Student> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(new FileSystemResource("src/main/resources/students-fixed-length.txt"));
        itemReader.setName("fixedLengthReader");
        //itemReader.setLinesToSkip(1);
        itemReader.setLineMapper(fixedLengthLineMapper());
        return itemReader;
    }

    private LineMapper<Student> fixedLengthLineMapper() {
        DefaultLineMapper<Student> lineMapper = new DefaultLineMapper<>();

        // FixedLengthTokenizer to define field lengths
        FixedLengthTokenizer tokenizer = new FixedLengthTokenizer();
        tokenizer.setNames("uid", "firstName", "lastName", "age");

        // Set column lengths for each field (example values)
        tokenizer.setColumns(
                new Range(1, 5),    // uid (5 characters)
                new Range(6, 25),   // firstName (20 characters)
                new Range(26, 45),  // lastName (20 characters)
                new Range(46, 48)   // age (3 characters)
        );

        // Optionally, specify the padding character (default is a space)
        tokenizer.setStrict(false); // Allow shorter lines

        BeanWrapperFieldSetMapper<Student> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Student.class);

        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        return lineMapper;
    }





}
