package com.tutorial.springbatchdemo.batch.writer;

import com.tutorial.springbatchdemo.model.AccountInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
public class TransactionLogWriter {

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

}
