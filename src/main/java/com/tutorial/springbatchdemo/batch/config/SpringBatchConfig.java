package com.tutorial.springbatchdemo.batch.config;

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
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Arrays;

@Configuration
@EnableBatchProcessing
public class SpringBatchConfig {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private DataSource dataSource;

    @Bean("transactionLogCursorItemReader")
    public JdbcCursorItemReader<TransactionLog> transactionLogCursorItemReader() {
        JdbcCursorItemReader<TransactionLog> reader = new JdbcCursorItemReader<>();
        reader.setSql("SELECT txn_id, date, operation, user_name,amount, status FROM txn_log" +
                " where status = '00'");
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
        String sql = "INSERT INTO account_info (operation,txn_id,from_account,to_account,amount,date) " +
                "VALUES (:operation,:transactionId, :fromAccount,:toAccount,:amount,:date)";
        return new JdbcBatchItemWriterBuilder<AccountInfo>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<AccountInfo>())
                .sql(sql)
                .dataSource(dataSource)
                .build();
    }

    @Bean("txnStatusUpdateJDBCWriter")
    public JdbcBatchItemWriter<AccountInfo> txnStatusUpdateJDBCWriter(DataSource dataSource) {
        String sql = "UPDATE txn_log set status = '10' where txn_id=:transactionId;";
        return new JdbcBatchItemWriterBuilder<AccountInfo>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<AccountInfo>())
                .sql(sql)
                .dataSource(dataSource)
                .build();
    }

    @Bean("compositeItemWriter")
    public CompositeItemWriter<AccountInfo> compositeItemWriter(DataSource dataSource) {
        CompositeItemWriter writer = new CompositeItemWriter();
        writer.setDelegates(Arrays.asList(accountInfoJDBCWriter(dataSource), txnStatusUpdateJDBCWriter(dataSource)));
        return writer;
    }


    @Bean("transactionLogHandlerJob")
    public Job transactionLogHandlerJob(
            @Qualifier("randomGeneratorStep") Step randomGeneratorStep,
            @Qualifier("transactionLogStep") Step transactionLogStep) {

        return jobBuilderFactory.get("Transaction")
                .incrementer(new RunIdIncrementer())
                .listener(jobCompletionListener())
                .start(randomGeneratorStep)
                .next(transactionLogStep)
                .build();
    }

    @Bean("randTransactionGenJob")
    public Job randTransactionGenJob(
            @Qualifier("randomGeneratorStep") Step randomGeneratorStep) {

        return jobBuilderFactory.get("genTransactionRandom")
                .incrementer(new RunIdIncrementer())
                .listener(jobCompletionListener())
                .start(randomGeneratorStep)
                .build();
    }

    @Bean("randTransactionProcessJob")
    public Job randTransactionProcessJob(
            @Qualifier("transactionLogStep") Step transactionLogStep) {

        return jobBuilderFactory.get("processTransaction")
                .incrementer(new RunIdIncrementer())
                .listener(jobCompletionListener())
                .start(transactionLogStep)
                .build();
    }
}
