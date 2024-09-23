package com.tutorial.springbatchdemo.util;

public class BeanNames {
    public static class Job {
        public static final String STUDENT_FIXED_LENGTH = "studentFixedLengthJob";
        public static final String STUDENT_CSV = "studentCSVJob";
        // Add other job-related constants here
    }

    public static class Step {
        public static final String STUDENT_FIXED_LENGTH_STEP = "studentFixedLengthStep";
        public static final String STUDENT_CSV_STUDENT_STEP = "csvStudentFileStep";
        // Add other step-related constants here
    }

    public static class Writer {
        public static final String STUDENT_REPOSITORY_WRITER = "studentRepositoryWriter";
        // Add other writer-related constants here
    }

    public static class Reader {
        public static final String STUDENT_FIXED_LENGTH_READER = "studentFixedLengthReader";
        public static final String STUDENT_CSV_READER = "csvReader";
        // Add other reader-related constants here
    }

    public static class Processor {
        // Add processor-related constants here if needed
    }
}
