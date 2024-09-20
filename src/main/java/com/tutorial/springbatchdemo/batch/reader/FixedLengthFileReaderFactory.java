package com.tutorial.springbatchdemo.batch.reader;

import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.FixedLengthTokenizer;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

@Component
public class FixedLengthFileReaderFactory {
    /**
     * A reusable method for creating a FlatFileItemReader for any entity class.
     *
     * @param <T>         The type of the entity to be mapped.
     * @param filePath    The path to the fixed-length file.
     * @param targetClass The class type of the entity.
     * @param fieldNames  Array of field names to map.
     * @param ranges      Array of Range objects specifying the character lengths for each field.
     * @return A FlatFileItemReader for the given entity class.
     */
    public <T> FlatFileItemReader<T> createFixedLengthReader(
            String filePath,
            Class<T> targetClass,
            String[] fieldNames,
            Range[] ranges
    ) {
        FlatFileItemReader<T> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(new FileSystemResource(filePath));
        itemReader.setName(targetClass.getSimpleName() + "FixedLengthReader");
        itemReader.setLineMapper(createFixedLengthLineMapper(targetClass, fieldNames, ranges));
        return itemReader;
    }

    /**
     * Creates a LineMapper for fixed-length file processing.
     *
     * @param <T>         The type of the entity to be mapped.
     * @param targetClass The class type of the entity.
     * @param fieldNames  Array of field names to map.
     * @param ranges      Array of Range objects specifying the character lengths for each field.
     * @return A LineMapper for the specified entity.
     */
    private <T> LineMapper<T> createFixedLengthLineMapper(
            Class<T> targetClass,
            String[] fieldNames,
            Range[] ranges
    ) {
        DefaultLineMapper<T> lineMapper = new DefaultLineMapper<>();

        // FixedLengthTokenizer to define field lengths
        FixedLengthTokenizer tokenizer = new FixedLengthTokenizer();
        tokenizer.setNames(fieldNames);
        tokenizer.setColumns(ranges);
        tokenizer.setStrict(false); // Allow shorter lines

        // Map the fields to the target class using BeanWrapperFieldSetMapper
        BeanWrapperFieldSetMapper<T> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(targetClass);

        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        return lineMapper;
    }
}
