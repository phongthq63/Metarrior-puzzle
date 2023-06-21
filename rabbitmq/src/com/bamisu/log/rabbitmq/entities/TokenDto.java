package com.bamisu.log.rabbitmq.entities;

public class TokenDto {
    private String txhash;
    private String wallet;
    private String moneyType;
    private double amount;

    public String getTxhash() {
        return txhash;
    }

    public void setTxhash(String txhash) {
        this.txhash = txhash;
    }

    public String getWallet() {
        return wallet;
    }

    public void setWallet(String wallet) {
        this.wallet = wallet;
    }

    public String getMoneyType() {
        return moneyType;
    }

    public void setMoneyType(String moneyType) {
        this.moneyType = moneyType;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "TokenDto{" +
                "txhash='" + txhash + '\'' +
                ", wallet='" + wallet + '\'' +
                ", moneyType='" + moneyType + '\'' +
                ", amount=" + amount +
                '}';
    }
}
