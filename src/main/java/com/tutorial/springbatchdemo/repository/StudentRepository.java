package com.tutorial.springbatchdemo.repository;

import com.tutorial.springbatchdemo.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student,Integer> {
}
