package com.bamisu.log.gameserver.datamodel.nft.entities;

import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.gamelib.item.entities.EquipDataVO;
import com.bamisu.log.gameserver.datamodel.hero.entities.HeroModel;

import java.util.List;

/**
 * Created by Quach Thanh Phong
 * On 3/23/2022 - 8:25 PM
 */
public class HeroUpstarBurn {
    public HeroModel heroModel;
    public List<HeroModel> listFission;
    public List<ResourcePackage> listRes;
    public long timer;
    public boolean isRequestBurn; // Kiểm tra xem listFission đã bị burn hay chưa

    public static HeroUpstarBurn create(HeroModel heroModel, List<HeroModel> listFission) {
        HeroUpstarBurn heroUpstarBurn = new HeroUpstarBurn();
        heroUpstarBurn.heroModel = heroModel;
        heroUpstarBurn.listFission = listFission;
        heroUpstarBurn.isRequestBurn = false;

        return heroUpstarBurn;
    }
}
