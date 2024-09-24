package com.tutorial.springbatchdemo.batch.job;

import com.tutorial.springbatchdemo.batch.listener.JobCompletionNotificationListener;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
}
