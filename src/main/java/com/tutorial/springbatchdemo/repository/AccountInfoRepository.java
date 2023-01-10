package com.tutorial.springbatchdemo.repository;

import com.tutorial.springbatchdemo.model.AccountInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountInfoRepository extends JpaRepository<AccountInfo,Long> {
}
