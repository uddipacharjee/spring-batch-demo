package com.tutorial.springbatchdemo.util;

public class BeanNames {
    public static class Job {
        public static final String STUDENT_FIXED_LENGTH = "studentFixedLengthJob";
        public static final String STUDENT_CSV = "studentCSVJob";
        public static final String TRANSACTION_LOG_HANDLER = "transactionLogHandlerJob";
        public static final String RANDOM_TRANSACTION_GENERATOR = "randTransactionGenJob";
        public static final String RANDOM_TRANSACTION_PROCESS = "randTransactionProcessJob";
        // Add other job-related constants here
    }

    public static class Step {
        public static final String STUDENT_FIXED_LENGTH_STEP = "studentFixedLengthStep";
        public static final String STUDENT_CSV_STUDENT_STEP = "csvStudentFileStep";
        public static final String TRANSACTION_LOG_STEP = "transactionLogStep";
        public static final String RANDOM_GENERATOR_STEP = "randomGeneratorStep";
        // Add other step-related constants here
    }

    public static class Writer {
        public static final String STUDENT_REPOSITORY_WRITER = "studentRepositoryWriter";
        public static final String COMPOSITE_ITEM_WRITER = "compositeItemWriter";
        public static final String ACCOUNT_INFO_JDBC_WRITER =  "accountInfoJDBCWriter";
        public static final String TXN_UPDATE_JDBC_WRITER = "txnStatusUpdateJDBCWriter";
        // Add other writer-related constants here
    }

    public static class Reader {
        public static final String STUDENT_FIXED_LENGTH_READER = "studentFixedLengthReader";
        public static final String STUDENT_CSV_READER = "csvReader";
        public static final String TRANSACTION_LOG_CURSOR_READER = "transactionLogCursorItemReader";
        // Add other reader-related constants here
    }

    public static class Processor {
        // Add processor-related constants here if needed
        public static final String TRANSACTION_LOG_PROCESSOR = "transactionLogProcessor";
    }
}
