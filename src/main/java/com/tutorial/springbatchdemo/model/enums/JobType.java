package com.tutorial.springbatchdemo.model.enums;

import java.util.HashMap;

public enum JobType {
    TR_GEN(100,"TR_GEN"),
    TR_PROCESS(200,"TR_PROCESS"),
    TR_GEN_AND_PROCESS(300,"TR_GEN_AND_PROCESS");
    private final Integer value;
    private final String strVal;

    private static final HashMap<Integer, JobType> mapInt = new HashMap<>();
    private static final HashMap<String, JobType> mapStr = new HashMap<>();
    static {
        for (JobType op:values()
             ) {
            mapInt.put(op.value, op);
            mapStr.put(op.strVal, op);
        }
    }
    JobType(Integer val, String strVal){
        this.value = val;
        this.strVal = strVal;
    }

    public static JobType getByValue(Integer value){
        return JobType.mapInt.get(value);
    }
    public static JobType getByStrValue(String value){
        return JobType.mapStr.get(value);
    }

    public Integer getValue(){
        return value;
    }
}
