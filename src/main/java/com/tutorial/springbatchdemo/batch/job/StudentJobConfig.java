package com.tutorial.springbatchdemo.batch.job;

import com.tutorial.springbatchdemo.batch.listener.JobCompletionNotificationListener;
import com.tutorial.springbatchdemo.util.BeanNames;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.support.DatabaseType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class StudentJobConfig {

    //private final JobRepository jobRepository;

    private final JobCompletionNotificationListener jobCompletionNotificationListener;
    private final DataSource dataSource;
    private final PlatformTransactionManager transactionManager;


    @Bean("jp")
    public JobRepository jobRepository() throws Exception {
        JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
        factory.setDataSource(dataSource);
        factory.setTransactionManager(transactionManager);
        factory.setIsolationLevelForCreate("ISOLATION_SERIALIZABLE");
        factory.setDatabaseType(DatabaseType.POSTGRES.name());  // Set this to your DB type
        factory.afterPropertiesSet();  // Ensures the factory is properly initialized
        return factory.getObject();
    }



    @Bean(BeanNames.Job.STUDENT_FIXED_LENGTH)
    public Job runJob2(@Qualifier(BeanNames.Step.STUDENT_FIXED_LENGTH_STEP) Step step,@Qualifier("jp")JobRepository jobRepository) throws Exception {
        return new JobBuilder("importStudentsFixedLength", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(step)
                .listener(jobCompletionNotificationListener)
                .build();

    }


    @Bean(BeanNames.Job.STUDENT_CSV)
    public Job runJob(@Qualifier(BeanNames.Step.STUDENT_CSV_STUDENT_STEP) Step step,JobRepository jobRepository) throws Exception {
        return new JobBuilder("importStudentsCSV", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(step)
                .build();
    }
}
