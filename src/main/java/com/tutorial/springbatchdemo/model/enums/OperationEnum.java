package com.tutorial.springbatchdemo.model.enums;

import java.util.HashMap;

public enum OperationEnum {
    RECHARGE(1),CLAIM(2);
    private final Integer value;

    private static final HashMap<Integer, OperationEnum> map = new HashMap<>();
    static {
        for (OperationEnum op:values()
             ) {
            map.put(op.value, op);
        }
    }
    OperationEnum(Integer val){
        this.value = val;
    }

    public static OperationEnum getByValue(Integer value){
        return OperationEnum.map.get(value);
    }

    public Integer getValue(){
        return value;
    }
}
