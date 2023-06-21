package com.bamisu.log.gameserver.module.skill.template.entities;

import com.bamisu.log.gameserver.module.ingame.entities.character.Character;
import com.bamisu.log.gameserver.module.ingame.entities.player.BasePlayer;
import com.bamisu.log.gameserver.module.ingame.entities.player.TeamSlot;
import com.bamisu.log.gameserver.module.skill.SkillUtils;
import com.bamisu.gamelib.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Create by Popeye on 7:44 PM, 6/3/2020
 */
public class Heals {
    /**
     * EHealsType
     * sẽ bằng rỗng nếu chỉ make SE mà ko hồi phục gì cả
     */
    public String type;

    /**
     * Xác định bằng hàm findHealValue()
     */
    public String value = ""; //công thức tính giá trị máu hồi
    public String target = ""; //target được hồi máu
    public List<SkillMakeSEDesc> se = null;

    public String hpRate; //dùng cho hồi sinh
    public String hpMaxRate; //dùng cho hồi sinh
    public int useTime; //dùng cho hồi sinh, là số lần sử dụng

    public EHealsType findType() {
        return EHealsType.fromStr(type);
    }

    public int findHealValue(Character actor, Character target, int totalDame, int totalMana) {
        if(value.contains("total_dame")){
            if(value.split("_").length == 3) {  //% của damage gây ra
                return (int) (totalDame * Float.parseFloat(value.split("_")[2]) / 100);
            }
            return totalDame;
        }
        else if(value.contains("total_mana")){
            if(value.split("_").length == 3) {  //% của mana damage gây ra
                return (int) (totalDame * Float.parseFloat(value.split("_")[2]) / 100);
            }
            return totalMana;
        }
        else if(value.contains("max_hp")){
            String strValue = value.split("_")[2];
            return Math.toIntExact(Math.round(Double.parseDouble(String.valueOf(Utils.calculationFormula(strValue + " * " + target.getMaxHP())))));
        }else {
            return Math.toIntExact(Math.round(Double.parseDouble(String.valueOf(Utils.calculationFormula(SkillUtils.fillDataToFormula(value, actor, null))))));
        }
    }
}
