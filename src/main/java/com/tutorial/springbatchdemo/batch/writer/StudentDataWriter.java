package com.tutorial.springbatchdemo.batch.writer;

import com.tutorial.springbatchdemo.model.Student;
import com.tutorial.springbatchdemo.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class StudentDataWriter {
    private final StudentRepository repository;

    @Bean("studentRepositoryWriter")
    public RepositoryItemWriter<Student> writer() {
        RepositoryItemWriter<Student> writer = new RepositoryItemWriter<>();
        writer.setRepository(repository);
        writer.setMethodName("save");
        return writer;
    }
}
