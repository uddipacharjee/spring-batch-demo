package com.tutorial.springbatchdemo.batch.config;

import com.tutorial.springbatchdemo.batch.processor.StudentProcessor;
import com.tutorial.springbatchdemo.listener.CustomStepExecutionListener;
import com.tutorial.springbatchdemo.listener.JobCompletionNotificationListener;
import com.tutorial.springbatchdemo.model.AccountInfo;
import com.tutorial.springbatchdemo.model.Student;
import com.tutorial.springbatchdemo.model.TransactionLog;
import com.tutorial.springbatchdemo.model.TransactionRowMapper;
import com.tutorial.springbatchdemo.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FixedLengthTokenizer;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Arrays;

@Configuration
//@EnableBatchProcessing
@RequiredArgsConstructor
public class SpringBatchConfig {
    //@Autowired
    //private JobBuilderFactory jobBuilderFactory;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final StudentRepository repository;


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

    @Bean("stepCompletionListener")
    public StepExecutionListener stepCompletionListener() {
        return new CustomStepExecutionListener();
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

        return new JobBuilder("Transaction",jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(jobCompletionListener())
                .start(randomGeneratorStep)
                .next(transactionLogStep)
                .build();
    }

    @Bean("randTransactionGenJob")
    public Job randTransactionGenJob(
            @Qualifier("randomGeneratorStep") Step randomGeneratorStep) {

        return new JobBuilder("genTransactionRandom",jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(jobCompletionListener())
                .start(randomGeneratorStep)
                .build();
    }

    @Bean("randTransactionProcessJob")
    public Job randTransactionProcessJob(
            @Qualifier("transactionLogStep") Step transactionLogStep) {

//        return jobBuilderFactory.get("processTransaction")
//                .incrementer(new RunIdIncrementer())
//                .listener(jobCompletionListener())
//                .start(transactionLogStep)
//                .build();
        return new JobBuilder("processTransaction",jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(jobCompletionListener())
                .start(transactionLogStep)
                .build();
    }

    @Bean
    public FlatFileItemReader<Student> reader() {
        FlatFileItemReader<Student> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(new FileSystemResource("src/main/resources/students.csv"));
        itemReader.setName("csvReader");
        itemReader.setLinesToSkip(1);
        itemReader.setLineMapper(lineMapper());
        return itemReader;
    }

    @Bean
    public StudentProcessor processor() {
        return new StudentProcessor();
    }


    @Bean
    public RepositoryItemWriter<Student> writer() {
        RepositoryItemWriter<Student> writer = new RepositoryItemWriter<>();
        writer.setRepository(repository);
        writer.setMethodName("save");
        return writer;
    }

    @Bean
    public Step step1() {
        return new StepBuilder("csvImport", jobRepository)
                .<Student, Student>chunk(1000, platformTransactionManager)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean("studentJob")
    public Job runJob() {
        return new JobBuilder("importStudents", jobRepository)
                .start(step1())
                .build();

    }

    @Bean
    public TaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor asyncTaskExecutor = new SimpleAsyncTaskExecutor();
        asyncTaskExecutor.setConcurrencyLimit(10);
        return asyncTaskExecutor;
    }

    private LineMapper<Student> lineMapper() {
        DefaultLineMapper<Student> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("uid", "firstName", "lastName", "age");

        BeanWrapperFieldSetMapper<Student> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Student.class);

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }

    @Bean
    public Step step2() {
        return new StepBuilder("importFixedLengthTxt", jobRepository)
                .<Student, Student>chunk(1000, platformTransactionManager)
                .reader(fixedLengthReader())
                .processor(processor())
                .writer(writer())
                .listener(stepCompletionListener())
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean("studentFixedLengthJob")
    public Job runJob2() {
        return new JobBuilder("importStudentsFixedLength", jobRepository)
                .start(step2())
                .listener(jobCompletionListener())
                .build();

    }

    @Bean
    public FlatFileItemReader<Student> fixedLengthReader() {
        FlatFileItemReader<Student> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(new FileSystemResource("src/main/resources/students-fixed-length.txt"));
        itemReader.setName("fixedLengthReader");
        //itemReader.setLinesToSkip(1);
        itemReader.setLineMapper(fixedLengthLineMapper());
        return itemReader;
    }

    private LineMapper<Student> fixedLengthLineMapper() {
        DefaultLineMapper<Student> lineMapper = new DefaultLineMapper<>();

        // FixedLengthTokenizer to define field lengths
        FixedLengthTokenizer tokenizer = new FixedLengthTokenizer();
        tokenizer.setNames("uid", "firstName", "lastName", "age");

        // Set column lengths for each field (example values)
        tokenizer.setColumns(
                new Range(1, 5),    // uid (5 characters)
                new Range(6, 25),   // firstName (20 characters)
                new Range(26, 45),  // lastName (20 characters)
                new Range(46, 48)   // age (3 characters)
        );

        // Optionally, specify the padding character (default is a space)
        tokenizer.setStrict(false); // Allow shorter lines
        //tokenizer.setPaddingCharacter(' '); // Define the padding character

        BeanWrapperFieldSetMapper<Student> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Student.class);

        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        return lineMapper;
    }


    @Bean("jobLauncherTaskExecutor")
    public TaskExecutor jobLauncherTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(10);  // Adjust pool size according to your needs
        taskExecutor.setMaxPoolSize(20);
        taskExecutor.setQueueCapacity(50);
        taskExecutor.setThreadNamePrefix("Async-Job-");
        taskExecutor.initialize();
        return taskExecutor;
    }

    @Bean("asyncJobLauncher")
    public JobLauncher asyncJobLauncher(JobRepository jobRepository,@Qualifier("jobLauncherTaskExecutor") TaskExecutor taskExecutor) {
        TaskExecutorJobLauncher jobLauncher = new TaskExecutorJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        jobLauncher.setTaskExecutor(taskExecutor); // Set async TaskExecutor
        return jobLauncher;
    }


}
