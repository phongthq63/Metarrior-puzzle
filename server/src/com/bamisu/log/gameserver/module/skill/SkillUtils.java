package com.bamisu.log.gameserver.module.skill;

import com.bamisu.log.gameserver.module.characters.element.Element;
import com.bamisu.log.gameserver.module.ingame.entities.character.Character;
import com.bamisu.log.gameserver.module.ingame.entities.character.ECharacterType;
import com.bamisu.log.gameserver.module.ingame.entities.effect.EEffect;
import com.bamisu.log.gameserver.module.ingame.entities.effect.Effect;
import com.bamisu.log.gameserver.module.ingame.entities.player.BasePlayer;
import com.bamisu.log.gameserver.module.ingame.entities.player.TeamSlot;
import com.bamisu.log.gameserver.module.ingame.entities.skill.Damage;
import com.bamisu.log.gameserver.module.ingame.entities.skill.DamagePackge;
import com.bamisu.log.gameserver.module.skill.template.entities.EHealsTarget;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.gamelib.utils.business.Debug;

import java.util.*;

/**
 * Create by Popeye on 4:52 PM, 2/28/2020
 */
public class SkillUtils {
    public static List<TeamSlot> findTarget(Character character, String strTarget) {
        ETargetType targetType = ETargetType.fromStrValue(strTarget);

        //xác định player target
        BasePlayer playerActor = character.getMaster();
        BasePlayer targetPlayer = playerActor.getEnemyPlayer();

        //xác định target slot
        List<TeamSlot> liveTeamSlot = targetPlayer.getLiveTeamSlots();

        List<TeamSlot> backCharacter = new ArrayList<>();
        List<TeamSlot> frontCharacter = new ArrayList<>();
        for (TeamSlot teamSlot : liveTeamSlot) {
            if (Arrays.asList(0, 1, 2).contains(teamSlot.getPos())) {
                backCharacter.add(teamSlot);
            }

            if (Arrays.asList(3, 4).contains(teamSlot.getPos())) {
                frontCharacter.add(teamSlot);
            }
        }
        int targetNumber;
        String[] arrTarget;
        List<TeamSlot> targetLine = null;
        List<TeamSlot> backupTargetLine = null;
        switch (targetType) {
            case RANDOM:
                arrTarget = strTarget.split(",");
                if (arrTarget.length == 1) {
                    targetNumber = Integer.parseInt(arrTarget[0]);
                } else {
                    targetNumber = Utils.randomInRange(Integer.parseInt(arrTarget[0]), Integer.parseInt(arrTarget[1]));
                }

                //số lượng tướng còn ít hơn sớ lượng mục tiêu
                if (liveTeamSlot.size() <= targetNumber) {
                    return liveTeamSlot;
                }

                Collections.shuffle(liveTeamSlot);
                return liveTeamSlot.subList(0, targetNumber);
            case BACK:
                targetLine = backCharacter;
                backupTargetLine = frontCharacter;
                break;
            case FRONT:
                targetLine = frontCharacter;
                backupTargetLine = backCharacter;
                break;
            case similar_position:
                //vị trí tương ứng có còn sống ko
                int pos = character.getTeamSlot().getPos();
                TeamSlot similarPosition = targetPlayer.getTeam().get(pos);
                if (similarPosition.haveCharacter() && similarPosition.getCharacter().isLive()) {
                    ArrayList<TeamSlot> target = new ArrayList<>();
                    target.add(similarPosition);
                    return target;
                } else {
                    //nếu ko còn sống thì xử lý tương tư như front line
                    targetLine = !frontCharacter.isEmpty() ? frontCharacter : backCharacter;
                    Collections.shuffle(targetLine);

                    ArrayList<TeamSlot> target = new ArrayList<>();
                    target.add(targetLine.get(0));
                    return target;
                }
            case LowestHP:
                TeamSlot lowestHPSlot = liveTeamSlot.get(0);
                for (TeamSlot teamSlot : liveTeamSlot) {
                    if (teamSlot.getCharacter().getCurrentHPPercent() < lowestHPSlot.getCharacter().getCurrentHPPercent()) {
                        lowestHPSlot = teamSlot;
                    }
                }

                ArrayList<TeamSlot> target = new ArrayList<>();
                target.add(lowestHPSlot);
                return target;
            default:
                return null;
        }

        //là front or back
        if (strTarget.split("_").length == 1) {
            return targetLine;
        } else {
            arrTarget = strTarget.split("_")[1].split(",");
            if (arrTarget.length == 1) {
                targetNumber = Integer.parseInt(arrTarget[0]);
            } else {
                targetNumber = Utils.randomInRange(Integer.parseInt(arrTarget[0]), Integer.parseInt(arrTarget[1]));
            }


            if (targetLine.size() <= targetNumber) {
                List<TeamSlot> finalTarget = new ArrayList<>();
                //hàng ko đủ target
                int delta = targetNumber - targetLine.size();
                finalTarget.addAll(targetLine);

                //lấy thêm từ hàng khác
                if(delta > 0){
                    if (backupTargetLine.size() <= delta) {
                        //hàng ko đủ target
                        finalTarget.addAll(backupTargetLine);
                    }else {
                        //hàng đó thừa target
                        Collections.shuffle(backupTargetLine);
                        finalTarget.addAll(backupTargetLine.subList(0, delta));
                    }
                }

                return finalTarget;
            }else {
                //hàng đó thừa target
                Collections.shuffle(targetLine);
                return targetLine.subList(0, targetNumber);
            }
        }
    }

    /**
     * tính D1 (chưa gồm khắc hệ)
     *
     * @param actor
     * @param dameRate
     * @return
     */
    public static DamagePackge calculateD1(DamageType type, Character actor, Character target, String dameRate, boolean isCrit) {
        int D1;
        int startD1 = Math.toIntExact(Math.round(Double.valueOf(String.valueOf(Utils.calculationFormula(SkillUtils.fillDataToFormula(dameRate, actor, target))))));
        //check bạo
        if (isCrit) {
            D1 = Math.floorDiv(startD1 * Utils.randomInRange(180, 200), 100);   //crit
            D1 += Math.round(startD1 * actor.getCRITBONUS() / 100);                 //bonus
        } else {
            D1 = startD1;
        }
        return new DamagePackge().push(new Damage(D1, type));
    }

    /**
     * Dame sau khi khắc hệ
     *
     * @param damagePackge
     * @param actor
     * @param target
     * @return
     */
    public static DamagePackge opposition(DamagePackge damagePackge, Character actor, Character target) {
        DamagePackge resultPackge = damagePackge.cloneNew();
        if (actor.getType() == ECharacterType.Sage || actor.getType() == ECharacterType.Celestial) {
            return resultPackge;
        }

        switch (actor.getElement()) {
            case FIRE:
                if (target.getElement() == Element.FOREST) {
                    resultPackge.applyRate(1.5);
                }
                if (target.getElement() == Element.ICE || target.getElement() == Element.LIGHT || target.getElement() == Element.DARK) {
                    resultPackge.applyRate(0.5);
                }
                break;
            case ICE:
                if (target.getElement() == Element.FIRE) {
                    resultPackge.applyRate(1.5);
                }
                if (target.getElement() == Element.LIGHTNING || target.getElement() == Element.LIGHT || target.getElement() == Element.DARK) {
                    resultPackge.applyRate(0.5);
                }
                break;
            case FOREST:
                if (target.getElement() == Element.GROUND) {
                    resultPackge.applyRate(1.5);
                }
                if (target.getElement() == Element.FIRE || target.getElement() == Element.LIGHT || target.getElement() == Element.DARK) {
                    resultPackge.applyRate(0.5);
                }
                break;
            case GROUND:
                if (target.getElement() == Element.LIGHTNING) {
                    resultPackge.applyRate(1.5);
                }
                if (target.getElement() == Element.FOREST || target.getElement() == Element.LIGHT || target.getElement() == Element.DARK) {
                    resultPackge.applyRate(0.5);
                }
                break;
            case LIGHTNING:
                if (target.getElement() == Element.ICE) {
                    resultPackge.applyRate(1.5);
                }
                if (target.getElement() == Element.GROUND || target.getElement() == Element.LIGHT || target.getElement() == Element.DARK) {
                    resultPackge.applyRate(0.5);
                }
                break;
            case LIGHT:
                resultPackge.applyRate(1.5);
                break;
            case DARK:
                resultPackge.applyRate(1.5);
                break;
        }

        return resultPackge;
    }

    /**
     * @param d1
     * @param victim
     * @return
     */
    public static int calculateD2(DamagePackge d1, Character sourceActor, Character victim) {
        int D2 = 0;
        double n = 30000;
        double A;
        double rate;
        //qua giáp và xuyên giáp
        for (Damage damage : d1.getDamages()) {
            switch (damage.getType()) {
                case PHYSICAL:
                    float APEN = sourceActor == null ? 0 : sourceActor.getAPEN();
                    A = Math.abs(1.0 * victim.getARM() * (1 - APEN * 1.0 / 100));
                    rate = n / (n + A);
                    D2 = (int) (damage.getValue() * rate);
                    break;
                case MAGIC:
                    float MPEN = sourceActor == null ? 0 : sourceActor.getMPEN();
                    A = Math.abs(1.0 * victim.getMR() * (1 - MPEN * 1.0 / 100));
                    rate = n / (n + A);
                    D2 = (int) (damage.getValue() * rate);
                    break;
                case STANDARD:
                    D2 += damage.getValue();
                    break;
            }
        }

        if (D2 < 0) {
            D2 = 0;
        }

//        Debug.debug("DAME " + D2);
        return D2;
    }

    /**
     * trả về true nếu miss
     * @param actor
     * @return
     */
    public static boolean calculateMiss(Character actor) {
        if (actor.getType() == ECharacterType.Sage || actor.getType() == ECharacterType.Celestial) {
            return false;
        }
        float rate = 3.5f;// - actor.getDEX() * 0.0225f;

        //Blind tăng tỉ lệ miss
        if(actor.haveEffect(EEffect.Blind)){
            rate += 50;
        }

        return Utils.rate(rate);
    }

    public static boolean calculateDodge(Character actor) {
        float rate = (actor.getELU() * 0.001f + actor.getAGI() * 0.001f);
        rate = rate > 50 ? 50 : rate;
//        //System.out.println("DODGE: " + actor.getCharacterVO().readID() + ",ELU " + actor.getELU() + ", AGI " + actor.getAGI() + ", rate " + rate);
        return Utils.rate(rate);
    }

    public static String fillDataToFormula(String formula, Character actor, Character target) {
        String tmp = formula;
        if (target != null) {
            tmp = tmp.
                    replace("EMAXHP", "" + target.getMaxHP());
        }
        tmp = tmp.
                replace("HP", "" + actor.getMaxHP()).
                replace("STR", "" + actor.getSTR()).
                replace("INT", "" + actor.getINT()).
                replace("ATK", "" + actor.getATK()).
                replace("ARM", "" + actor.getARM()).
                replace("MR", "" + actor.getMR()).
                replace("DEF", "" + actor.getDEF()).
                replace("DEX", "" + actor.getDEX()).
                replace("AGI", "" + actor.getAGI()).
                replace("ELU", "" + actor.getELU()).
                replace("APEN", "" + actor.getAPEN()).
                replace("MPEN", "" + actor.getMPEN()).
                replace("DPEN", "" + actor.getDPEN()).
                replace("CRIT", "" + actor.getCRIT()).
                replace("CRITBONUS", "" + actor.getCRITBONUS()).
                replace("TEN", "" + actor.getTEN());
        return tmp;
    }

    public static List<Character> findBuffTarget(String target, Character actor) {
        BasePlayer master = actor.getMaster();
        List<Character> targets = new ArrayList<>();
        String[] arrStrTarget = target.split(",");

        for (String strTarget : arrStrTarget) {
            List<Character> subTargets = new ArrayList<>();
            switch (EHealsTarget.fromStr(strTarget)) {
                case ME_RANDOM:
                    List<Character> liveCharacter = new ArrayList<>();
                    for (TeamSlot slot : master.getTeam()) {
                        if (slot.haveCharacter() && slot.getCharacter().isLive()) {
                            if (!slot.getCharacter().equals(actor)) {
                                liveCharacter.add(slot.getCharacter());
                            }
                        }
                    }

                    int targetNumber = 0;
                    String[] args = strTarget.split("_");
                    if (args.length == 3) {
                        targetNumber = Integer.parseInt(args[2]);
                    }
                    if (args.length == 4) {
                        targetNumber = Utils.randomInRange(Integer.parseInt(args[2]), Integer.parseInt(args[3]));
                    }

                    if (liveCharacter.size() < targetNumber) {
                        subTargets.addAll(liveCharacter);
                        if (actor.getType() != ECharacterType.Sage && actor.getType() != ECharacterType.Celestial) {
                            subTargets.add(actor); //ko đủ thì tính cả mình
                        }
                        break;
                    }
                    Collections.shuffle(liveCharacter);
                    List<Character> sub = liveCharacter.subList(0, targetNumber);

                    //có đối tượng ưu tiên
                    if(master.allyTarget != null && master.allyTarget.getCharacter().isLive()){
                        if(!sub.contains(master.allyTarget.getCharacter())){
                            sub.remove(0);
                            sub.add(master.allyTarget.getCharacter());
                        }
                    }

                    subTargets.addAll(sub);
                    break;
                case ME_HA:
                    String[] argsME_HA = strTarget.split("_");
                    int targetME_HA = Integer.parseInt(argsME_HA[2]);

                    //get live team
                    List<Character> liveTeamME_HA = new ArrayList<>();
                    for (TeamSlot slot : master.getTeam()) {
                        if (slot.haveCharacter() && slot.getCharacter().isLive()) {
                            if (!slot.getCharacter().equals(actor)) {
                                liveTeamME_HA.add(slot.getCharacter());
                            }
                        }
                    }

                    if (liveTeamME_HA.size() <= targetME_HA) {
                        subTargets.addAll(liveTeamME_HA);
                        if (liveTeamME_HA.size() < targetME_HA) {
                            if (actor.getType() != ECharacterType.Sage && actor.getType() != ECharacterType.Celestial) {
                                subTargets.add(actor);
                            }
                        }
                    } else {
                        //soft
                        liveTeamME_HA.sort(new Comparator<Character>() {
                            @Override
                            public int compare(Character o1, Character o2) {
                                if ( (o1.getSTR() > o1.getINT() ? o1.getSTR() : o1.getINT()) < (o2.getSTR() > o2.getINT() ? o2.getSTR() : o2.getINT()) ) return 1;
                                else return -1;
                            }
                        });

                        subTargets.addAll(liveTeamME_HA.subList(0, targetME_HA));
                    }
                    break;
                case ALLY_LOWEST_HP:
                    String[] args2 = strTarget.split("_");
                    int targetNumber2 = Integer.parseInt(args2[3]);

                    //get live team
                    List<Character> liveTeam = new ArrayList<>();
                    for (TeamSlot slot : master.getTeam()) {
                        if (slot.haveCharacter() && slot.getCharacter().isLive()) {
                            if (!slot.getCharacter().equals(actor)) {
                                liveTeam.add(slot.getCharacter());
                            }
                        }
                    }

                    if (liveTeam.size() <= targetNumber2) {
                        subTargets.addAll(liveTeam);
                        if (liveTeam.size() < targetNumber2) {
                            if (actor.getType() != ECharacterType.Sage && actor.getType() != ECharacterType.Celestial) {
                                subTargets.add(actor);
                            }
                        }
                    } else {
                        //soft
                        liveTeam.sort(new Comparator<Character>() {
                            @Override
                            public int compare(Character o1, Character o2) {
                                if (o1.getCurrentHPPercent() < o2.getCurrentHPPercent()) return -1;
                                else return 1;
                            }
                        });

                        subTargets.addAll(liveTeam.subList(0, targetNumber2));
                    }
                    break;
                case ENEMY_RANDOM:
                    break;
                case ME:
                    subTargets.add(actor);
                    break;
                case SAGE:
                    if (master.sage != null) {
                        subTargets.add(master.sage);
                    }
                    break;
                case CELESTIAL:
                    if (master.celestial != null) {
                        subTargets.add(master.celestial);
                    }
                    break;
            }

            for (Character character : subTargets) {
                if (!targets.contains(character)) {
                    targets.add(character);
                }
            }
        }

        return targets;
    }
}
