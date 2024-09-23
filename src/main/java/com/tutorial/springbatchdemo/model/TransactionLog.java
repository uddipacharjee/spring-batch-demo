package com.tutorial.springbatchdemo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "txn_log")
public class TransactionLog {
    @Id
    @Column(name = "txn_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long txnId;

    @Column(name = "date")
    private Date date;

    @Column(name = "operation")
    private Integer operation;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "status")
    private String status;
}
