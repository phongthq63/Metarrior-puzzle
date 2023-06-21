package com.bamisu.log.rabbitmq.entities;

public class MarketDto {
    private String txhash;
    private String tokenId;
    private String hash;
    private double price;
    private String from;
    private String to;

    public String getTxhash() {
        return txhash;
    }

    public void setTxhash(String txhash) {
        this.txhash = txhash;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    @Override
    public String toString() {
        return "MarketDto{" +
                "txhash='" + txhash + '\'' +
                ", tokenId='" + tokenId + '\'' +
                ", hash='" + hash + '\'' +
                ", price=" + price +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                '}';
    }
}
