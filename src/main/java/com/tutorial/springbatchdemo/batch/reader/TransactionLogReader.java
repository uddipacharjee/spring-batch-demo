package com.tutorial.springbatchdemo.batch.reader;

import com.tutorial.springbatchdemo.model.TransactionLog;
import com.tutorial.springbatchdemo.model.TransactionRowMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class TransactionLogReader {
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
}
