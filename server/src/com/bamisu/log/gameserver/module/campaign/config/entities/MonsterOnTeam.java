package com.bamisu.log.gameserver.module.campaign.config.entities;

import com.bamisu.log.gameserver.datamodel.hero.entities.HeroModel;
import com.bamisu.log.gameserver.module.characters.creep.entities.CreepInstanceVO;
import com.bamisu.log.gameserver.module.characters.mboss.entities.MbossInstanceVO;
import com.bamisu.log.gameserver.module.hero.HeroManager;
import com.bamisu.log.gameserver.module.ingame.entities.character.ECharacterType;

/**
 * Create by Popeye on 3:14 PM, 2/5/2020
 */
public class MonsterOnTeam {
    public String id = "";
    public int star = 0;
    public int level = 0;
    public String kingdom = "";
    public String element = "";
    public int lethal = 0;

    public int getType(){
        return ECharacterType.IdToType(id);
    }

    public int getPower(){
        if(id.isEmpty()) return 0;

        switch (ECharacterType.fromType(getType())){
            case Hero:
                return HeroManager.getInstance().getPower(
                        HeroManager.getInstance().getStatsNormalHeroModel(HeroModel.createWithoutUser(id, star, level)).cloneWithLethal(lethal));
            case Creep:
                return HeroManager.getInstance().getPower(
                        HeroManager.CreepManager.getInstance().getStatsCreep(
                                CreepInstanceVO.createCreepInstanceVO(id, level, star, kingdom, element)).cloneWithLethal(lethal));
            case MiniBoss:
                return HeroManager.getInstance().getPower(
                        HeroManager.MBossManager.getInstance().getStatsMBoss(
                                MbossInstanceVO.createMBossInstanceVO(id, level, star, kingdom, element)).cloneWithLethal(lethal));
//            case Boss:
//                return HeroManager.getInstance().getPower(
//                        HeroManager.CreepManager.getInstance().getStatsCreep(
//                                CreepInstanceVO.createCreepInstanceVO(id, level, star, kingdom, element)));
        }

        return  0;
    }
}
