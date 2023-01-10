package com.tutorial.springbatchdemo.config;

import com.tutorial.springbatchdemo.model.TransactionLog;
import com.tutorial.springbatchdemo.model.TransactionRowMapper;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Bean
    public JdbcCursorItemReader<TransactionLog> cursorItemReader(){
        JdbcCursorItemReader<TransactionLog> reader = new JdbcCursorItemReader<>();
        reader.setSql("SELECT txn_id, date, operation, user_name,amount FROM txn_log");
        reader.setDataSource(dataSource);
        reader.setFetchSize(100);
        reader.setRowMapper(new TransactionRowMapper());

        return reader;
    }

    @Bean
    public ItemWriter<TransactionLog> customerItemWriter(){
        return items -> {
            for(TransactionLog c : items) {
                System.out.println(c.toString());
            }
        };
    }




    @Bean
    public Job job(Step step){

        return jobBuilderFactory.get("Transaction")
                .incrementer(new RunIdIncrementer())
                .start(step)
                .build();
    }




}
