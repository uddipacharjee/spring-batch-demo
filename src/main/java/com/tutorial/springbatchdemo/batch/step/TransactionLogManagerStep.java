package com.tutorial.springbatchdemo.batch.step;

import com.tutorial.springbatchdemo.batch.tasklet.RandomGeneratorTasklet;
import com.tutorial.springbatchdemo.model.AccountInfo;
import com.tutorial.springbatchdemo.model.TransactionLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Slf4j
@Configuration
public class TransactionLogManagerStep {
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Bean("transactionLogStep")
    public Step step(@Qualifier("transactionLogCursorItemReader") JdbcCursorItemReader<TransactionLog> cursorItemReader,
                     @Qualifier("transactionLogProcessor") ItemProcessor<TransactionLog, AccountInfo> itemProcessor,
                     @Qualifier("accountInfoJDBCWriter") ItemWriter<AccountInfo> itemWriter) {
        return stepBuilderFactory.get("Txn-Log")
                .<TransactionLog, AccountInfo>chunk(10000)
                .reader(cursorItemReader)
                .processor(itemProcessor)
                .writer(itemWriter)
                .build();
    }

    @Bean("randomGeneratorStep")
    public Step step(RandomGeneratorTasklet generatorTasklet) {
        return stepBuilderFactory.get("Random-Gen")
                .tasklet(generatorTasklet)
                .build();
    }

    @Bean("initStep")
    public Step step() {
        return stepBuilderFactory.get("Init-Step")
                .tasklet((contribution, chunkContext) -> {
                    log.info("initial step");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }
}
