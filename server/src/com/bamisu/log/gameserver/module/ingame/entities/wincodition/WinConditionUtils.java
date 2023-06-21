package com.bamisu.log.gameserver.module.ingame.entities.wincodition;

import com.bamisu.log.gameserver.module.ingame.entities.fighting.FightingManager;
import com.bamisu.log.gameserver.module.ingame.entities.player.BasePlayer;
import com.bamisu.log.gameserver.module.ingame.entities.player.TeamSlot;
import com.bamisu.gamelib.skill.SkillConfigManager;
import com.bamisu.gamelib.skill.config.entities.WinCondition;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Create by Popeye on 11:31 AM, 5/11/2020
 */
public class WinConditionUtils {
    public static List<Boolean> check(List<String> winConditions, FightingManager fightingManager) {
        List<Boolean> result = new ArrayList<>();
        for (String conditionID : winConditions) {
            WinCondition winCondition = SkillConfigManager.getInstance().getWinCodition(conditionID);
            if (winCondition == null) {
                result.add(false);
                continue;
            }

            /**
             * {"id": "WCO001", "desc": "Đánh bại tất cả các enemy", "tag": 1},
             *     {"id": "WCO002", "desc": "Không hero nào bên ally chết khi hết trận đấu", "tag": 2},
             *     {"id": "WCO003", "desc": "Tất cả Hero còn tối thiểu 20% máu", "tag": 2},
             *     {"id": "WCO004", "desc": "Tất cả Hero còn tối thiểu 40% máu", "tag": 2},
             *     {"id": "WCO005", "desc": "Tất cả Hero còn tối thiểu 60% máu", "tag": 2},
             *     {"id": "WCO006", "desc": "Tất cả Hero còn tối thiểu 80% máu", "tag": 2},
             *     {"id": "WCO007", "desc": "Tất cả Hero còn tối thiểu 90% máu", "tag": 2}
             *     {"id": "WCO008", "desc": "Thắng trong 8 lượt đánh", "tag": 4},
             */
            switch (winCondition.id) {
                case "WCO001":
                    result.add(checkWCO001(fightingManager));
                    break;
                case "WCO002":
                    result.add(checkWCO002(fightingManager));
                    break;
                case "WCO003":
                    result.add(checkWCO003(fightingManager));
                    break;
                case "WCO004":
                    result.add(checkWCO004(fightingManager));
                    break;
                case "WCO005":
                    result.add(checkWCO005(fightingManager));
                    break;
                case "WCO006":
                    result.add(checkWCO006(fightingManager));
                    break;
                case "WCO007":
                    result.add(checkWCO007(fightingManager));
                    break;
                case "WCO008":
                    result.add(checkWCO008(fightingManager));
                    break;
            }
        }
        return result;
    }

    private static Boolean checkWCO008(FightingManager fightingManager) {
        return (checkWCO001(fightingManager)) && fightingManager.turnCount <= 10;
    }

    private static Boolean checkWCO007(FightingManager fightingManager) {
        BasePlayer player = fightingManager.getPlayer(0);
        for (TeamSlot teamSlot : player.team) {
            if(teamSlot.haveCharacter()) {
                if (teamSlot.getCharacter().getCurrentHPPercent() < 90) {
                    return false;
                }
            }
        }

        return true;
    }

    private static Boolean checkWCO006(FightingManager fightingManager) {
        BasePlayer player = fightingManager.getPlayer(0);
        for (TeamSlot teamSlot : player.team) {
            if(teamSlot.haveCharacter()) {
                if (teamSlot.getCharacter().getCurrentHPPercent() < 80) {
                    return false;
                }
            }
        }

        return true;
    }

    private static Boolean checkWCO005(FightingManager fightingManager) {
        BasePlayer player = fightingManager.getPlayer(0);
        for (TeamSlot teamSlot : player.team) {
            if(teamSlot.haveCharacter()) {
                if (teamSlot.getCharacter().getCurrentHPPercent() < 60) {
                    return false;
                }
            }
        }

        return true;
    }

    private static Boolean checkWCO004(FightingManager fightingManager) {
        BasePlayer player = fightingManager.getPlayer(0);
        for (TeamSlot teamSlot : player.team) {
            if(teamSlot.haveCharacter()) {
                if (teamSlot.getCharacter().getCurrentHPPercent() < 40) {
                    return false;
                }
            }
        }

        return true;
    }

    private static Boolean checkWCO003(FightingManager fightingManager) {
        BasePlayer player = fightingManager.getPlayer(0);
        for (TeamSlot teamSlot : player.team) {
            if(teamSlot.haveCharacter()) {
                if (teamSlot.getCharacter().getCurrentHPPercent() < 20) {
                    return false;
                }
            }
        }
        return true;
    }

    private static Boolean checkWCO002(FightingManager fightingManager) {
        BasePlayer player = fightingManager.getPlayer(0);
        for (TeamSlot teamSlot : player.team) {
            if(teamSlot.haveCharacter()) {
                if (!teamSlot.getCharacter().isLive()) {
                    return false;
                }
            }
        }

        return true;
    }

    private static Boolean checkWCO001(FightingManager fightingManager) {
        BasePlayer player = fightingManager.getPlayer(1);
        for (TeamSlot teamSlot : player.team) {
            if(teamSlot.haveCharacter()){
                if (teamSlot.getCharacter().isLive()) {
                    return false;
                }
            }
        }

        return true;
    }
}
