package com.tutorial.springbatchdemo.batch;

import com.tutorial.springbatchdemo.model.AccountInfo;
import com.tutorial.springbatchdemo.repository.AccountInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class DBWriter implements ItemWriter<AccountInfo> {
    @Autowired
    private AccountInfoRepository accountInfoRepository;
    @Override
    public void write(List<? extends AccountInfo> items) throws Exception {
        log.info("writing to db");
        accountInfoRepository.saveAll(items);
    }
}
