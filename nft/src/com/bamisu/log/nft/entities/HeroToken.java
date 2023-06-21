package com.bamisu.log.nft.entities;

/**
 * Created by Quach Thanh Phong
 * On 3/5/2022 - 8:41 PM
 */
public class HeroToken {
    public String hashHero;
    public String tokenId;

    public static HeroToken create(String hashHero, String tokenId) {
        HeroToken heroToken = new HeroToken();
        heroToken.hashHero = hashHero;
        heroToken.tokenId = tokenId;

        return heroToken;
    }
}
