package com.tutorial.springbatchdemo.batch.processor;
import com.tutorial.springbatchdemo.model.Student;
import org.springframework.batch.item.ItemProcessor;

public class StudentProcessor implements ItemProcessor<Student,Student> {

    @Override
    public Student process(Student student) {
        return student;
    }
}