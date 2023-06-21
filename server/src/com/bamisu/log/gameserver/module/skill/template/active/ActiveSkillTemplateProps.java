package com.bamisu.log.gameserver.module.skill.template.active;

import com.bamisu.log.gameserver.module.ingame.entities.character.Character;
import com.bamisu.log.gameserver.module.skill.SkillUtils;
import com.bamisu.log.gameserver.module.skill.template.SkillProps;
import com.bamisu.log.gameserver.module.skill.template.entities.*;
import com.bamisu.gamelib.utils.Utils;

import java.util.List;

/**
 * Create by Popeye on 10:02 AM, 5/26/2020
 */
public class ActiveSkillTemplateProps extends SkillProps {

    /**
     * Mô tả lượng sát thương gây ra
     */
    public List<DamageDesc> damage = null;

    /**
     * Khả target của bên địch
     */
    public String target = "";

    /**
     * Khả năng gây ra hiệu ứng
     */
    public List<SkillMakeSEDesc> se = null;

    /**
     * mô tả khả năng Cộng thêm thuộc tính khi thục hiện skill
     */
    public List<Additional> additional = null;

    /**
     * mô tả khả năng gây đột tử của skill
     */
    public SuddenDie suddenDie = null;

    /**
     * Mô tả khả năng hồi phục
     */
    public List<Heals> heals = null;

    /**
     * Mô tả việc thay đổi puzzle
     */
    public ReplateDiamond replateDiamond = null;

    /**
     *  Bỏ qua hiệu ứng tank
     */
    public boolean preventTank = false;

    //tính bonus chi mang
    public double caculationCit(Character character) {
        double add = 0;
        if(additional != null){
            for(Additional additional : additional){
                if(additional.type.equalsIgnoreCase(AdditionalType.Critical)){
                    double delta = Utils.roundDouble(Double.parseDouble(String.valueOf(Utils.calculationFormula(SkillUtils.fillDataToFormula(additional.value, character, null)))), 4);
                    if(delta > additional.maxValue){
                        delta = additional.maxValue;
                    }
                    add += delta;
                }
            }
        }
        return add;
    }

    private static class AdditionalType{
        public static final String Critical = "critical";
    }
}
