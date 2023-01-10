package com.tutorial.springbatchdemo.batch;

import com.tutorial.springbatchdemo.model.AccountInfo;
import com.tutorial.springbatchdemo.model.TransactionLog;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StepDepositConfig {
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Bean("transactionLogStep")
    public Step step(JdbcCursorItemReader<TransactionLog> cursorItemReader,
                     ItemProcessor<TransactionLog, AccountInfo> itemProcessor,
                     ItemWriter<AccountInfo> itemWriter) {
        return stepBuilderFactory.get("Txn-Log")
                .<TransactionLog, AccountInfo>chunk(5)
                .reader(cursorItemReader)
                .processor(itemProcessor)
                .writer(itemWriter)
                .build();
    }
}
