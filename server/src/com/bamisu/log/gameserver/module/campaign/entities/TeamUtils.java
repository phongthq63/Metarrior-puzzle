package com.bamisu.log.gameserver.module.campaign.entities;

import com.bamisu.gamelib.skill.SkillConfigManager;
import com.bamisu.gamelib.skill.config.entities.WinCondition;
import com.bamisu.gamelib.skill.passive.Statbuff;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.entities.ColorHero;
import com.bamisu.log.gameserver.module.campaign.config.entities.MonsterOnTeam;
import com.bamisu.log.gameserver.module.characters.CharactersConfigManager;
import com.bamisu.log.gameserver.module.characters.element.Element;
import com.bamisu.log.gameserver.module.characters.entities.ICharacterVO;
import com.bamisu.log.gameserver.module.characters.kingdom.Kingdom;
import com.bamisu.log.gameserver.module.mission.config.entities.MissionVO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Create by Popeye on 10:28 AM, 5/6/2020
 */
public class TeamUtils {
    public static List<MonsterOnTeam> genEnemyMissionOutpost(MissionVO missionVO) {
        List<MonsterOnTeam> enemyTeam = new ArrayList<>();
//        String json;
//        if (Utils.rate(50)) {
//            json = "[\n" +
//                    "            {\"id\" : \"M1001\", \"level\": 1, \"star\": 1, \"kingdom\": \"1\", \"element\": \"E401\"},\n" +
//                    "            {\"id\" : \"MBS1000\", \"level\": 1, \"star\": 1, \"kingdom\": \"1\", \"element\": \"E401\"},\n" +
//                    "            {\"id\" : \"T1020\", \"level\": 1, \"star\": 1, \"kingdom\": \"1\", \"element\": \"E401\"},\n" +
//                    "            {\"id\" : \"MBS1002\", \"level\": 1, \"star\": 1, \"kingdom\": \"2\", \"element\": \"E401\"},\n" +
//                    "            {\"id\" : \"M1006\", \"level\": 1, \"star\": 1, \"kingdom\": \"3\", \"element\": \"E401\"}\n" +
//                    "          ]";
//        } else {
//            json = "[\n" +
//                    "            {\"id\" : \"M1002\", \"level\": 1, \"star\": 1, \"kingdom\": \"1\", \"element\": \"E401\"},\n" +
//                    "            {\"id\" : \"MBS1000\", \"level\": 1, \"star\": 1, \"kingdom\": \"1\", \"element\": \"E401\"},\n" +
//                    "            {\"id\" : \"T1020\", \"level\": 1, \"star\": 1, \"kingdom\": \"1\", \"element\": \"E401\"},\n" +
//                    "            {\"id\" : \"MBS1002\", \"level\": 1, \"star\": 1, \"kingdom\": \"2\", \"element\": \"E401\"},\n" +
//                    "            {\"id\" : \"M1005\", \"level\": 1, \"star\": 1, \"kingdom\": \"3\", \"element\": \"E401\"}\n" +
//                    "          ]";
//        }
//        enemyTeam = Utils.fromJson(json, enemyTeam.getClass());

//        for (int i = 0; i < 5; i++) {
//            MonsterOnTeam monsterOnTeam = new MonsterOnTeam();
//            if (Utils.rate(50)) {
//                HeroVO heroVO = CharactersConfigManager.getInstance().getRandomHeroConfig(missionVO.monsterStar, null, null, null);
//                monsterOnTeam.id = heroVO.id;
//                monsterOnTeam.star = missionVO.monsterStar;
//                monsterOnTeam.level = missionVO.monsterLevel;
//                monsterOnTeam.kingdom = heroVO.kingdom;
//                monsterOnTeam.element = heroVO.element;
//            } else {
//                HeroVO heroVO = CharactersConfigManager.getInstance().getRandomHeroConfig(missionVO.monsterStar, null, null, null);
//                monsterOnTeam.id = heroVO.id;
//                monsterOnTeam.star = missionVO.monsterStar;
//                monsterOnTeam.level = missionVO.monsterLevel;
//                monsterOnTeam.kingdom = heroVO.kingdom;
//                monsterOnTeam.element = heroVO.element;
//            }
//
//            enemyTeam.add(monsterOnTeam);
//        }
        return enemyTeam;
    }

    public static List<MonsterOnTeam> genEnemyHunt(int level, int lethal, int monsterStar) {
        List<MonsterOnTeam> enemyTeam = new ArrayList<>();

        MonsterOnTeam monsterOnTeam = null;
        ICharacterVO characterVO = null;
        for (int i = 0; i < 5; i++) {
            if (i >= 2) {
                enemyTeam.add(null);
                continue;
            }

            if (characterVO != null && Utils.rate(50)) {
                //TODO: ko gen lại
            } else {
                do {
                    characterVO = CharactersConfigManager.getInstance().getRandom3StarHeroConfig(ColorHero.RED.getStar());
                } while (characterVO.getID().equalsIgnoreCase("T1031"));
            }

//            test
//            if( i == 0){
//                characterVO = CharactersConfigManager.getInstance().getHeroConfig("T1039");
//            }
//            if( i == 1){
//                characterVO = CharactersConfigManager.getInstance().getHeroConfig("T1037");
//            }

            monsterOnTeam = new MonsterOnTeam();
            monsterOnTeam.kingdom = Kingdom.DARK.getId();
            monsterOnTeam.element = genElement("random");
            monsterOnTeam.id = characterVO.getID();
            monsterOnTeam.star = monsterStar;
            monsterOnTeam.level = level;
//            if (level >= 1 && level <= 30) monsterOnTeam.lethal = 200;
//            else if (level >= 31 && level <= 60) monsterOnTeam.lethal = 200;
//            else monsterOnTeam.lethal = 200 + (level / 20) * 10;
//
//
//            monsterOnTeam.lethal = monsterOnTeam.lethal > 300 ? 300 : monsterOnTeam.lethal;
            monsterOnTeam.lethal = lethal;

//            if (monsterOnTeam.id.equalsIgnoreCase("T1033")) {
//                monsterOnTeam.lethal = monsterOnTeam.lethal * 40 / 100;
//            }
//
//            if (monsterOnTeam.id.equalsIgnoreCase("T1026")) {
//                monsterOnTeam.lethal = monsterOnTeam.lethal * 55 / 100;
//            }
            enemyTeam.add(monsterOnTeam);
        }
        return enemyTeam;
    }

    /**
     * random hoặc chỉ định 1 EElement ID
     *
     * @param options
     * @return
     */
    public static String genElement(String options) {
        Element element = Element.fromID(options);
        if (element != null) return element.getId();
        options = options.toLowerCase();
        switch (options) {
            case "random":
                return Element.v1.get(Utils.randomInRange(0, Element.v1.size() - 1)).getId();
        }

        return null;
    }

    public static int getTeamBonus(List<Kingdom> kingdoms) {
        Map<String, Integer> count = new HashMap<>();
        for (Kingdom kingdom : Kingdom.values()) {
            count.put(kingdom.getId(), 0);
        }

        for (Kingdom kingdom : kingdoms) {
            count.put(kingdom.getId(), count.get(kingdom.getId()) + 1);
        }

        Kingdom max = null;
        Kingdom second = null;
        int maxCount = 0;
        for (String key : count.keySet()) {
            if (Kingdom.fromID(key) != Kingdom.GUARDIAN && Kingdom.fromID(key) != Kingdom.DARK) {
                if (count.get(key) > maxCount) {
                    maxCount = count.get(key);
                    second = max;
                    max = Kingdom.fromID(key);
                } else {
                    if (second == null && count.get(key) > 0) second = Kingdom.fromID(key);
                }
            }
        }

        if (max == null) {
            if (kingdoms.size() < 3) {
                return -1;
            }

            if (kingdoms.size() == 3) return 0;
            if (kingdoms.size() == 4) return 2;
            if (kingdoms.size() == 5) return 3;
            return kingdoms.size() - 3;
        } else {
            count.put(max.getId(), count.get(max.getId()) + count.get(Kingdom.GUARDIAN.getId()) + count.get(Kingdom.DARK.getId()));
            if (count.get(max.getId()) < 3) {
                return -1;
            }
            if (count.get(max.getId()) == 3) {
                if (second == null) return 0;
                if (count.get(second.getId()) < 2) return 0;
                return 1;
            }

            if (count.get(max.getId()) == 4) return 2;
            if (count.get(max.getId()) == 5) return 3;
        }

        return -1;
    }

    /**
     * Gen ra dieu kien hoan thanh nhiem vu
     *
     * @return
     */
    public static List<String> genMissionCodition(short min, short max) {
        List<String> condition = new ArrayList<>();

        //Lay ra so luong dieu kien cua nhiem vu
        int count = Utils.randomInRange(min, max);

        //Deep colne lai list dieu kien config --- tien cho viec xu ly
        List<WinCondition> conditionCf = SkillConfigManager.getInstance().getWinCodition().parallelStream().collect(Collectors.toList());
        int rdCondition;
        for (int i = 0; i < count; i++) {
            //random chua qua xu ly --- thay doi sau
            rdCondition = Utils.randomInRange(0, condition.size() - 1);
            //Xu ly chon dieu kien + loai bo dieu kien khoi mang
            // ---> 2 dk mission khoong trung nhau
            condition.add(conditionCf.get(rdCondition).id);
            conditionCf.remove(rdCondition);
        }

        return condition;
    }

    public static List<Statbuff> getPosBonus(int pos) {
        List<Statbuff> statbuffs = new ArrayList<>();
        switch (pos) {
            case 0:
                statbuffs.add(new Statbuff("ATK", "(20*1)", null, null, true));
                statbuffs.add(new Statbuff("DPEN", "(10*1)", null, null, true));
                return statbuffs;
            case 1:
                statbuffs.add(new Statbuff("ATK", "(20*1)", null, null, true));
                statbuffs.add(new Statbuff("DPEN", "(10*1)", null, null, true));
                return statbuffs;
            case 2:
                statbuffs.add(new Statbuff("ATK", "(20*1)", null, null, true));
                statbuffs.add(new Statbuff("DPEN", "(10*1)", null, null, true));
                return statbuffs;
            case 3:
                statbuffs.add(new Statbuff("HP", "(20*1)", null, null, true));
                statbuffs.add(new Statbuff("DEF", "(20*1)", null, null, true));
                return statbuffs;
            case 4:
                statbuffs.add(new Statbuff("HP", "(20*1)", null, null, true));
                statbuffs.add(new Statbuff("DEF", "(20*1)", null, null, true));
                return statbuffs;
            default:
                return statbuffs;
        }
    }

    public static void main(String[] args) {
        //System.out.println(getTeamBonus(Arrays.asList(Kingdom.DRUID, Kingdom.DRUID, Kingdom.BANISHED, Kingdom.BANISHED, Kingdom.DWARF)));
    }

    public static List<Statbuff> getMapBonus(Element actorElement, Element elementMap) {
        List<Statbuff> statbuffs = new ArrayList<>();
        switch (elementMap) {
            case FIRE:
                if (actorElement == Element.FIRE) {
                    statbuffs.add(new Statbuff("HP", "(10*1)", null, null, false));
                    statbuffs.add(new Statbuff("ATK", "(10*1)", null, null, false));
                    statbuffs.add(new Statbuff("DEF", "(10*1)", null, null, false));
                }

                if (actorElement == Element.FOREST) {
                    statbuffs.add(new Statbuff("HP", "(10*1)", null, null, true));
                    statbuffs.add(new Statbuff("ATK", "(10*1)", null, null, true));
                    statbuffs.add(new Statbuff("DEF", "(10*1)", null, null, true));
                }
                break;
            case ICE:
                if (actorElement == Element.ICE) {
                    statbuffs.add(new Statbuff("HP", "(10*1)", null, null, false));
                    statbuffs.add(new Statbuff("ATK", "(10*1)", null, null, false));
                    statbuffs.add(new Statbuff("DEF", "(10*1)", null, null, false));
                }

                if (actorElement == Element.FIRE) {
                    statbuffs.add(new Statbuff("HP", "(10*1)", null, null, true));
                    statbuffs.add(new Statbuff("ATK", "(10*1)", null, null, true));
                    statbuffs.add(new Statbuff("DEF", "(10*1)", null, null, true));
                }
                break;
            case LIGHTNING:
                if (actorElement == Element.LIGHTNING) {
                    statbuffs.add(new Statbuff("HP", "(10*1)", null, null, false));
                    statbuffs.add(new Statbuff("ATK", "(10*1)", null, null, false));
                    statbuffs.add(new Statbuff("DEF", "(10*1)", null, null, false));
                }

                if (actorElement == Element.ICE) {
                    statbuffs.add(new Statbuff("HP", "(10*1)", null, null, true));
                    statbuffs.add(new Statbuff("ATK", "(10*1)", null, null, true));
                    statbuffs.add(new Statbuff("DEF", "(10*1)", null, null, true));
                }
                break;
            case GROUND:
                if (actorElement == Element.GROUND) {
                    statbuffs.add(new Statbuff("HP", "(10*1)", null, null, false));
                    statbuffs.add(new Statbuff("ATK", "(10*1)", null, null, false));
                    statbuffs.add(new Statbuff("DEF", "(10*1)", null, null, false));
                }

                if (actorElement == Element.LIGHTNING) {
                    statbuffs.add(new Statbuff("HP", "(10*1)", null, null, true));
                    statbuffs.add(new Statbuff("ATK", "(10*1)", null, null, true));
                    statbuffs.add(new Statbuff("DEF", "(10*1)", null, null, true));
                }
                break;
            case FOREST:
                if (actorElement == Element.FOREST) {
                    statbuffs.add(new Statbuff("HP", "(10*1)", null, null, false));
                    statbuffs.add(new Statbuff("ATK", "(10*1)", null, null, false));
                    statbuffs.add(new Statbuff("DEF", "(10*1)", null, null, false));
                }

                if (actorElement == Element.GROUND) {
                    statbuffs.add(new Statbuff("HP", "(10*1)", null, null, true));
                    statbuffs.add(new Statbuff("ATK", "(10*1)", null, null, true));
                    statbuffs.add(new Statbuff("DEF", "(10*1)", null, null, true));
                }
                break;
        }

        return statbuffs;
    }
}
