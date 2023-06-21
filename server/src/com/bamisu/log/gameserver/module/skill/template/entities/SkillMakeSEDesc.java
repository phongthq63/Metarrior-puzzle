package com.bamisu.log.gameserver.module.skill.template.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.bamisu.log.gameserver.module.ingame.entities.character.Character;
import com.bamisu.log.gameserver.module.skill.SkillUtils;
import com.bamisu.gamelib.entities.LIZRandom;
import com.bamisu.gamelib.entities.RandomObj;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.gamelib.utils.business.Debug;

import java.util.List;

/**
 * Create by Popeye on 3:53 PM, 5/6/2020
 */
public class SkillMakeSEDesc {
    @JsonProperty("se")
    public String SEName;

    public String rate; //công thức tính rate
    public double maxRate;
    public int turn;
    public List<Object> props;

    public boolean canMake(Character character){
        double _rate = Double.parseDouble(String.valueOf(Utils.calculationFormula(SkillUtils.fillDataToFormula(rate, character, null))));
        _rate = _rate > maxRate ? maxRate : _rate;
        return Utils.rate(_rate);
    }

    public String readSEName(){
        String[] ses = SEName.split(",");
        if(ses.length == 0){
            Debug.trace("ERR SE: " + ses);
            return null;
        }

        if(ses.length == 1){
            return ses[0];
        }

        //random
        LIZRandom lizRandom = new LIZRandom();
        for(String se : ses){
            lizRandom.push(new RandomObj(se, 1));
        }
        return String.valueOf(lizRandom.next().value);
    }

    public static void main(String[] args){
//        //System.out.println(Math.round(Double.parseDouble(String.valueOf(Utils.calculationFormula("1/3"))) * 1000.0) / 1000.0);
//        //System.out.println(Double.parseDouble(String.valueOf(Utils.calculationFormula("0.01*3/10"))));
//        //System.out.println(Utils.round(1.0/3, 3));
//        //System.out.println(Utils.roundDouble(1.0/3, 3));
    }
}
