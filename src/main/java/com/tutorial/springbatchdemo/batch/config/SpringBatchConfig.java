package com.tutorial.springbatchdemo.batch.config;

import com.tutorial.springbatchdemo.batch.decider.TransactionLogExecutionDecider;
import com.tutorial.springbatchdemo.listener.JobCompletionNotificationListener;
import com.tutorial.springbatchdemo.model.AccountInfo;
import com.tutorial.springbatchdemo.model.TransactionLog;
import com.tutorial.springbatchdemo.model.TransactionRowMapper;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class SpringBatchConfig {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;


    @Autowired
    private DataSource dataSource;
    @Bean("transactionLogCursorItemReader")
    public JdbcCursorItemReader<TransactionLog> transactionLogCursorItemReader(){
        JdbcCursorItemReader<TransactionLog> reader = new JdbcCursorItemReader<>();
        reader.setSql("SELECT txn_id, date, operation, user_name,amount FROM txn_log");
        reader.setDataSource(dataSource);
        reader.setFetchSize(100);
        reader.setRowMapper(new TransactionRowMapper());

        return reader;
    }

    @Bean("jobCompletionListener")
    public JobExecutionListener jobCompletionListener() {
        return new JobCompletionNotificationListener();
    }

    @Bean("accountInfoJDBCWriter")
    public JdbcBatchItemWriter<AccountInfo> accountInfoJDBCWriter(DataSource dataSource) {
        String sql = "INSERT INTO account_info (operation,from_account,to_account,amount,date) " +
                "VALUES (:operation, :fromAccount,:toAccount,:amount,:date)";
        return new JdbcBatchItemWriterBuilder<AccountInfo>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<AccountInfo>())
                .sql(sql)
                .dataSource(dataSource)
                .build();
    }


    @Bean("transactionLogHandlerJob")
    public Job transactionLogHandlerJob(
                   @Qualifier("randomGeneratorStep") Step randomGeneratorStep,
                   @Qualifier("transactionLogStep") Step transactionLogStep){

        return jobBuilderFactory.get("Transaction-001")
                .incrementer(new RunIdIncrementer())
                .listener(jobCompletionListener())
                .start(randomGeneratorStep)
                .next(transactionLogStep)
                .build();
    }

    @Bean("transactionLogHandlerJobWithDecider")
    public Job transactionLogHandlerJobWithDecider(
            TransactionLogExecutionDecider transactionLogExecutionDecider,
            @Qualifier("initStep") Step initStep,
            @Qualifier("randomGeneratorStep") Step randomGeneratorStep,
            @Qualifier("transactionLogStep") Step transactionLogStep){

        return jobBuilderFactory.get("Transaction-002")
                .incrementer(new RunIdIncrementer())
                .listener(jobCompletionListener())
                .flow(initStep)
                .next(transactionLogExecutionDecider).on("YES").to(randomGeneratorStep)
                .from(transactionLogExecutionDecider).on("*").to(transactionLogStep)
                .next(transactionLogStep)
                .end()
                .build();
    }

}
