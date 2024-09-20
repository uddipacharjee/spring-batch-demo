package com.tutorial.springbatchdemo.batch.processor;
import com.tutorial.springbatchdemo.model.Student;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StudentProcessor implements ItemProcessor<Student,Student> {

    @Override
    public Student process(Student student) {
        log.info("processing student, Thread name {}", Thread.currentThread().getName());
        return student;
    }
}