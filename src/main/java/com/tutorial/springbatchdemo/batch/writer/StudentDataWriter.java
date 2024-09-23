package com.tutorial.springbatchdemo.batch.writer;

import com.tutorial.springbatchdemo.model.Student;
import com.tutorial.springbatchdemo.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.tutorial.springbatchdemo.util.BeanNames.Writer.STUDENT_REPOSITORY_WRITER;

@Configuration
@RequiredArgsConstructor
public class StudentDataWriter {
    private final StudentRepository repository;

    @Bean(STUDENT_REPOSITORY_WRITER)
    public RepositoryItemWriter<Student> writer() {
        RepositoryItemWriter<Student> writer = new RepositoryItemWriter<>();
        writer.setRepository(repository);
        writer.setMethodName("save");
        return writer;
    }
}
