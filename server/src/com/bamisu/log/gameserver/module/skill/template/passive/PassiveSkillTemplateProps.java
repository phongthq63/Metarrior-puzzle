package com.bamisu.log.gameserver.module.skill.template.passive;

import com.bamisu.gamelib.skill.passive.Statbuff;
import com.bamisu.log.gameserver.module.skill.template.SkillProps;
import com.bamisu.log.gameserver.module.skill.template.entities.Heals;
import com.bamisu.log.gameserver.module.skill.template.entities.IncreaseStrengthWhenHPLoss;
import com.bamisu.log.gameserver.module.skill.template.entities.SkillMakeSEDesc;

import java.util.List;

/**
 * Create by Popeye on 3:54 PM, 6/19/2020
 */
public class PassiveSkillTemplateProps extends SkillProps {
    //tăng chỉ số cơ bản
    public List<Statbuff> statbuff;

    //đờ đòn cho đồng minh
    public TankEffect tank;

    //phản đòn
    public CounterEffect counterEffect;

    //cơ hội gây SE mỗi lần tấn công
    public List<SkillMakeSEDesc> makeSEPerAttack;

    //đánh 1 skill chủ động trước mỗi turn
    public String skillingBefoTurn;

    //đánh 1 skill khi có 1 đồng đội chết
    public String skillingOnAllyDie;

    //hồi phục khi bị đánh
    public Heals healsOnDamageOn;

    //miễn nhiễm hiệu ứng | EffectCategory
    public String immunity;

    //bất tử khi máu về 0
    public OnDiePassive immortal;

    //tăng chỉ số sau mỗi lần đánh, cộng dồn đến hết trận
    public List<Statbuff> incAttrPerAttack;

    //tăng chỉ số nếu máu mát đi
    public IncreaseStrengthWhenHPLoss increaseStrengthWhenHPLoss;
}
