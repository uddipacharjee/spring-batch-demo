package com.tutorial.springbatchdemo.batch.step;

import com.tutorial.springbatchdemo.batch.tasklet.RandomGeneratorTasklet;
import com.tutorial.springbatchdemo.model.AccountInfo;
import com.tutorial.springbatchdemo.model.TransactionLog;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import static com.tutorial.springbatchdemo.util.BeanNames.Processor.TRANSACTION_LOG_PROCESSOR;
import static com.tutorial.springbatchdemo.util.BeanNames.Reader.TRANSACTION_LOG_CURSOR_READER;
import static com.tutorial.springbatchdemo.util.BeanNames.Step.RANDOM_GENERATOR_STEP;
import static com.tutorial.springbatchdemo.util.BeanNames.Step.TRANSACTION_LOG_STEP;
import static com.tutorial.springbatchdemo.util.BeanNames.Writer.COMPOSITE_ITEM_WRITER;

@Configuration
@RequiredArgsConstructor
public class TransactionLogManagerStep {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    @Bean(TRANSACTION_LOG_STEP)
    public Step runStep(@Qualifier(TRANSACTION_LOG_CURSOR_READER) JdbcCursorItemReader<TransactionLog> cursorItemReader,
                     @Qualifier(TRANSACTION_LOG_PROCESSOR) ItemProcessor<TransactionLog, AccountInfo> itemProcessor,
                     //@Qualifier("accountInfoJDBCWriter") ItemWriter<AccountInfo> itemWriter,
                     //@Qualifier("txnStatusUpdateJDBCWriter") JdbcBatchItemWriter<AccountInfo> itemUpdater
                     @Qualifier(COMPOSITE_ITEM_WRITER) CompositeItemWriter<AccountInfo> compositeItemWriter

    ) {
        return new StepBuilder("Txn-Log",jobRepository)
                .<TransactionLog, AccountInfo>chunk(1000,platformTransactionManager)
                .reader(cursorItemReader)
                .processor(itemProcessor)
                .writer(compositeItemWriter)
                .build();
    }

    @Bean(RANDOM_GENERATOR_STEP)
    public Step step(RandomGeneratorTasklet generatorTasklet) {
        return new StepBuilder("Random-Gen",jobRepository)
                .tasklet(generatorTasklet,platformTransactionManager)
                .build();
    }
}
