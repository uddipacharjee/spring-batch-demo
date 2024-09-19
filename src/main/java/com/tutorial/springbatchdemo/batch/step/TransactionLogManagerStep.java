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

@Configuration
@RequiredArgsConstructor
public class TransactionLogManagerStep {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    @Bean("transactionLogStep")
    public Step runStep(@Qualifier("transactionLogCursorItemReader") JdbcCursorItemReader<TransactionLog> cursorItemReader,
                     @Qualifier("transactionLogProcessor") ItemProcessor<TransactionLog, AccountInfo> itemProcessor,
                     //@Qualifier("accountInfoJDBCWriter") ItemWriter<AccountInfo> itemWriter,
                     //@Qualifier("txnStatusUpdateJDBCWriter") JdbcBatchItemWriter<AccountInfo> itemUpdater
                     @Qualifier("compositeItemWriter") CompositeItemWriter<AccountInfo> compositeItemWriter

    ) {
        return new StepBuilder("Txn-Log",jobRepository)
                .<TransactionLog, AccountInfo>chunk(1000,platformTransactionManager)
                .reader(cursorItemReader)
                .processor(itemProcessor)
                .writer(compositeItemWriter)
                .build();
    }

    @Bean("randomGeneratorStep")
    public Step step(RandomGeneratorTasklet generatorTasklet) {
        return new StepBuilder("Random-Gen",jobRepository)
                .tasklet(generatorTasklet,platformTransactionManager)
                .build();
    }
}
