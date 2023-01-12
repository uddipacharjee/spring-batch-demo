package com.tutorial.springbatchdemo.batch.tasklet;

import com.tutorial.springbatchdemo.model.TransactionLog;
import com.tutorial.springbatchdemo.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Slf4j
@Component
@RequiredArgsConstructor
public class RandomGeneratorTasklet implements Tasklet, StepExecutionListener {

    private final TransactionRepository transactionRepository;
    @Override
    public void beforeStep(StepExecution stepExecution) {
        log.info("Random Generator tasklet starts");
    }


    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        Random random = new Random();
        String[] names = {"USER-A","USER-B","USER-c"};
        int[] ops = {1,2,3};
        List<TransactionLog> txnLogList = new ArrayList<>();
        for(int i=0; i< 100000; i++){
            TransactionLog txnLog = TransactionLog.builder()
                    .userName(names[random.nextInt(3)])
                    .operation(ops[random.nextInt(3)])
                    .date(new Date())
                    .amount((double)random.nextInt(1000)+15000L)
                    .status("00")
                    .build();
            txnLogList.add(txnLog);
        }
        transactionRepository.saveAll(txnLogList);
        return RepeatStatus.FINISHED;
    }


    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        log.info("Random Generator tasklet ends");
        return ExitStatus.COMPLETED;
    }
}
