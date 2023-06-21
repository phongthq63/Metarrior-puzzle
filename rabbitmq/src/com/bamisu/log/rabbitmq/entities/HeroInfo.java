package com.bamisu.log.rabbitmq.entities;

public class HeroInfo {
    private String heroHash;
    private String tokenId;

    public String getHeroHash() {
        return heroHash;
    }

    public void setHeroHash(String hash) {
        this.heroHash = hash;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }
}
