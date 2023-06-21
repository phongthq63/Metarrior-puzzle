package com.bamisu.log.gameserver.datamodel.nft.entities;

import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.gamelib.entities.TokenResourcePackage;
import com.bamisu.gamelib.item.entities.IResourcePackage;
import com.bamisu.log.gameserver.datamodel.hero.entities.HeroModel;

import java.util.List;

/**
 * Created by Quach Thanh Phong
 * On 3/6/2022 - 2:39 AM
 */
public class HeroMintModel {
    public List<HeroModel> listHeroMint;
    public int sum;
    public List<TokenResourcePackage> resourceCreate;
    public String extraData;

    public static HeroMintModel create(List<HeroModel> listHeroMint, List<TokenResourcePackage> resourceCreate, String extraData) {
        HeroMintModel heroMintModel = new HeroMintModel();
        heroMintModel.listHeroMint = listHeroMint;
        heroMintModel.resourceCreate = resourceCreate;
        heroMintModel.sum = listHeroMint.size();
        heroMintModel.extraData = extraData;

        return heroMintModel;
    }
}
