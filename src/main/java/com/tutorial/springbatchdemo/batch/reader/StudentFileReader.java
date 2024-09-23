package com.tutorial.springbatchdemo.batch.reader;

import com.tutorial.springbatchdemo.model.Student;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import static com.tutorial.springbatchdemo.util.BeanNames.Reader.STUDENT_CSV_READER;
import static com.tutorial.springbatchdemo.util.BeanNames.Reader.STUDENT_FIXED_LENGTH_READER;

@Configuration
@RequiredArgsConstructor
public class StudentFileReader {

    private final FixedLengthFileReaderFactory readerFactory;

    @Value("${file.fixed-length}")
    private String fixedLengthFileInput;

    @Value("${file.csv-student}")
    private String csvFileInput;


    @Bean(STUDENT_FIXED_LENGTH_READER )
    public FlatFileItemReader<Student> studentFixedLengthReader() {
        return readerFactory.createFixedLengthReader(
                fixedLengthFileInput,
                Student.class,
                new String[]{"uid", "firstName", "lastName", "age"},
                new Range[]{
                        new Range(1, 5),    // uid (5 characters)
                        new Range(6, 25),   // firstName (20 characters)
                        new Range(26, 45),  // lastName (20 characters)
                        new Range(46, 48)   // age (3 characters)
                }
        );
    }

    @Bean(STUDENT_CSV_READER)
    public FlatFileItemReader<Student> reader() {
        FlatFileItemReader<Student> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(new FileSystemResource("src/main/resources/"+csvFileInput));
        itemReader.setName("csvReader");
        itemReader.setStrict(false);
        itemReader.setLinesToSkip(1);
        itemReader.setLineMapper(lineMapper());
        return itemReader;
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
}
