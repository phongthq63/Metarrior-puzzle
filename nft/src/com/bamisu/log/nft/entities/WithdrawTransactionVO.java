package com.bamisu.log.nft.entities;

import java.sql.Timestamp;

public class WithdrawTransactionVO {
    private String transactionId;
    private String txhash;

    private String token;
    private double amount;
    private boolean isSuccess;
    private Timestamp created;
    private Timestamp updated;

    public WithdrawTransactionVO() {

    }

    public WithdrawTransactionVO(String name, String transactionId, double amount) {
        this.token = name;
        this.transactionId = transactionId;
        this.amount = amount;
        this.created = new Timestamp(System.currentTimeMillis());
        this.isSuccess = false;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTxhash() {
        return txhash;
    }

    public void setTxhash(String txhash) {
        this.txhash = txhash;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public Timestamp getUpdated() {
        return updated;
    }

    public void setUpdated(Timestamp updated) {
        this.updated = updated;
    }

    public void updateUpdated() {
        this.updated = new Timestamp(System.currentTimeMillis());
    }
}
