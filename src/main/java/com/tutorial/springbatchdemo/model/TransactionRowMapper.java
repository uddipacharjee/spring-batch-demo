package com.tutorial.springbatchdemo.model;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TransactionRowMapper implements RowMapper<TransactionLog> {
    public static final String ID_COLUMN = "txn_id";
    public static final String OPERATION_COLUMN = "operation";
    public static final String USER_NAME_COLUMN = "user_name";
    public static final String DATE_COLUMN = "date";
    public static final String AMOUNT_COLUMN = "amount";
    public static final String STATUS_COLUMN = "amount";


    @Override
    public TransactionLog mapRow(ResultSet rs, int rowNum) throws SQLException {
        return TransactionLog.builder()
                .txnId(rs.getLong(ID_COLUMN))
                .operation(rs.getInt(OPERATION_COLUMN))
                .userName(rs.getString(USER_NAME_COLUMN))
                .date(rs.getDate(DATE_COLUMN))
                .amount(rs.getDouble(AMOUNT_COLUMN))
                .status(rs.getString(STATUS_COLUMN))
                .build();
    }
}
