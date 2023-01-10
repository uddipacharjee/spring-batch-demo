package com.tutorial.springbatchdemo.repository;

import com.tutorial.springbatchdemo.model.TransactionLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<TransactionLog,Long> {
}
