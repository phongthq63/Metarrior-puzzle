package com.bamisu.log.gameserver.datamodel.hunt.entities;

import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.log.gameserver.module.campaign.config.BattleBackgroundConfigManager;
import com.bamisu.log.gameserver.module.campaign.config.entities.MonsterOnTeam;
import com.bamisu.log.gameserver.module.campaign.entities.TeamUtils;
import com.bamisu.log.gameserver.module.hunt.HuntManager;
import com.bamisu.log.gameserver.module.hunt.config.entities.HuntPowerConfig;
import com.bamisu.log.gameserver.module.hunt.config.entities.RewardPowerVO;
import com.bamisu.log.gameserver.module.hunt.config.entities.SlotRewardVO;
import net.andreinc.mockneat.MockNeat;

import java.util.ArrayList;
import java.util.List;

public class HuntInfo {

    private int avgLevel;
    public String mapBonus;
    public String bg;
    public List<MonsterInfo> listEnemy;
    public List<String> condition;
    public List<ResourcePackage> reward;
    private HuntPowerConfig huntPowerConfig;


    public HuntInfo() {
    }

    public HuntInfo(int avgLevel) {
        this.avgLevel = avgLevel;
    }

    public static HuntInfo create(int avgLevel, boolean isNewDay) {
        HuntInfo huntInfo = new HuntInfo(avgLevel);

        HuntPowerConfig huntPowerConfig = HuntManager.getInstance().getHuntConfig().readPowerConfig(huntInfo.avgLevel);
        huntInfo.changeHuntPowerConfig(huntPowerConfig);

        huntInfo.mapBonus = TeamUtils.genElement(huntPowerConfig.terrain);
        huntInfo.bg = BattleBackgroundConfigManager.getInstance().getBGFromTerrain(huntPowerConfig.terrain);
        huntInfo.reward = huntInfo.genHuntReward(huntInfo.avgLevel, isNewDay);
        huntInfo.condition = new ArrayList<>(huntPowerConfig.conditions);
        huntInfo.listEnemy = huntInfo.genHuntEnemy(avgLevel, huntPowerConfig.lethan.get(0), huntPowerConfig.monsterStar);

        return huntInfo;
    }

    public HuntPowerConfig readHuntPowerConfig() {
        return huntPowerConfig;
    }

    public void changeHuntPowerConfig(HuntPowerConfig huntPowerConfig) {
        this.huntPowerConfig = huntPowerConfig;
    }

    private List<MonsterInfo> genHuntEnemy(int level, int lethal, int monsterStar) {
        List<MonsterInfo> monster = new ArrayList<>();
        for (MonsterOnTeam enemy : TeamUtils.genEnemyHunt(level, lethal, monsterStar)) {
            if (enemy == null) {
                monster.add(null);
            } else {
                monster.add(MonsterInfo.create(enemy));
            }
        }

        return monster;
    }

    /**
     * Gen phan thuong
     */
    private List<ResourcePackage> genHuntReward(int avgLevel, boolean isNewDay) {
        RewardPowerVO rewardCf = HuntManager.getInstance().getRewardHuntConfig(avgLevel);
        if (rewardCf == null) return new ArrayList<>();

        List<ResourcePackage> reward = new ArrayList<>();
        MockNeat mockNeat = MockNeat.threadLocal();
        String luckyItem = mockNeat.probabilites(String.class)
                .add(0.5, "true")
                .add(0.5, "false")
                .val();
        String slotRandom = mockNeat.probabilites(String.class)
                .add(0.5, "SPI1050")
                .add(0.2, "FRA")
                .add(0.3, "MON1024")
                .val();
        String itemFRA = mockNeat.probabilites(String.class)
                .add(0.25, "FRAT1039")
                .add(0.25, "FRAT1038")
                .add(0.25, "FRAT1001")
                .add(0.25, "FRAT1037")
                .val();

        for (SlotRewardVO slot : rewardCf.slot) {

            if (slot.reward.id.equals("MON1001") || slot.reward.id.equals("SOG") || slot.reward.id.equals("MON1002") || slot.reward.id.equals("SPI1049")) {
                reward.add(new ResourcePackage(slot.reward.id, slot.reward.amount));
            }
            if (luckyItem.equals("true")) {
                if (slot.reward.id.equals("SPI1050") && slot.reward.id.equals(slotRandom)) {
                    reward.add(new ResourcePackage(slot.reward.id, slot.reward.amount));
                }
                if (slot.reward.id.equals("MON1024") && slot.reward.id.equals(slotRandom)) {
                    reward.add(new ResourcePackage(slot.reward.id, slot.reward.amount));
                }
                if (slotRandom.equals("FRA") && slot.reward.id.equals(itemFRA)) {
                    reward.add(new ResourcePackage(slot.reward.id, slot.reward.amount));
                }
            }

//            if(slot.reward.id.equals("SPI1050") && !isNewDay){
//                if(Utils.rate(50)){
//                    reward.add(new ResourcePackage(slot.reward.id, slot.reward.amount));
//                }
//            }else {
//                if(Utils.rate(slot.reward.rate)){
//                    reward.add(new ResourcePackage(slot.reward.id, slot.reward.amount));
//                }
//            }
        }

        return reward;
    }

    public void updateCurrentHpEnemy(List<Float> listCurrentHp) {
        for (int i = 0; i < listEnemy.size(); i++) {
            if (listEnemy.get(i) == null) continue;

            if (i > listCurrentHp.size() - 1) break;
            if (listCurrentHp.get(i) <= 0) {
                listEnemy.set(i, null);
            } else {
                listEnemy.get(i).currentHp = listCurrentHp.get(i);
            }
        }
    }
}
