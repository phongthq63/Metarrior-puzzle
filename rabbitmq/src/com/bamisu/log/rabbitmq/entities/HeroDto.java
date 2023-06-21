package com.bamisu.log.rabbitmq.entities;

import java.util.List;

public class HeroDto {
    private String txhash;
    private String wallet;
    private List<HeroInfo> heroes;

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

    public List<HeroInfo> getHeroes() {
        return heroes;
    }

    public void setHeroes(List<HeroInfo> heroes) {
        this.heroes = heroes;
    }
}
