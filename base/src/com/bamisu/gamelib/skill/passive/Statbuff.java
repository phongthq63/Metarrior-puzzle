package com.bamisu.gamelib.skill.passive;

/**
 * Create by Popeye on 4:01 PM, 6/19/2020
 */
public class Statbuff {
    public String attr;
    public String rate;
    public String max;
    public String target; //EHealsTarget
    public boolean isDebuff = false;
    public String dependentHero = ""; //được áp dụng nếu có con hero này trong đội hình

    public Statbuff() {
    }

    public Statbuff(String attr, String rate, String max, String target, boolean isDebuff) {
        this.attr = attr;
        this.rate = rate;
        this.max = max;
        this.target = target;
        this.isDebuff = isDebuff;
    }

//    public float calculaRate(Character actor) {
//        float finalRate = Float.valueOf(String.valueOf(Utils.calculationFormula(SkillUtils.fillDataToFormula(rate, actor))));
//        if(max != null){
//            float maxRate = Float.valueOf(String.valueOf(Utils.calculationFormula(SkillUtils.fillDataToFormula(max, actor))));
//            if(finalRate > maxRate){
//                finalRate = maxRate;
//            }
//        }
//        return finalRate;
//    }
}
