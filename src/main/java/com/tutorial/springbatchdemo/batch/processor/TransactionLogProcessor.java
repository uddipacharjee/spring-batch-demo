package com.tutorial.springbatchdemo.batch.processor;

import com.tutorial.springbatchdemo.model.AccountInfo;
import com.tutorial.springbatchdemo.model.TransactionLog;
import com.tutorial.springbatchdemo.model.enums.OperationEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
@Slf4j
@Component("transactionLogProcessor")
public class TransactionLogProcessor implements ItemProcessor<TransactionLog, AccountInfo> {

    private static final Map<Integer, String> OPERATION = new HashMap<>();

    public TransactionLogProcessor(){

    }

    @Override
    public AccountInfo process(TransactionLog item) throws Exception {

        String fromAcc = "";
        String toAcc = "";

        if(Objects.equals(item.getOperation(), OperationEnum.RECHARGE.getValue())){
            fromAcc = "RETAILER";
            toAcc = "SALE_DEPOSIT";
        } else if (Objects.equals(item.getOperation(), OperationEnum.CLAIM.getValue())) {
            fromAcc = "SALE_DEPOSIT";
            toAcc = "RETAILER";
        } else if (Objects.equals(item.getOperation(), OperationEnum.PURCHASE.getValue())) {
            fromAcc = "CUSTOMER";
            toAcc = "SALE_DEPOSIT";
        }

        AccountInfo accountInfo = AccountInfo.builder()
                .operation(item.getOperation())
                .fromAccount(fromAcc)
                .toAccount(toAcc)
                .amount(item.getAmount())
                .date(item.getDate())
                .build();
        return accountInfo;
    }
}
