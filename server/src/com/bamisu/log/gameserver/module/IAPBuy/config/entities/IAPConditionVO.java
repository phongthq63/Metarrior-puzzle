package com.bamisu.log.gameserver.module.IAPBuy.config.entities;

import com.bamisu.log.gameserver.module.IAPBuy.entities.ConditionVO;

public class IAPConditionVO {
    public String id;
    public ConditionPropertiesVO condition;

    public ConditionVO createConditionVO(int point){
        int starPoint;      //Khoi dau moc
        int perPoint;       //Tang moi moc
        int conPoint;
        for(int i = condition.formula.size() - 1; i >= 0; i--){
            starPoint = condition.formula.get(i).s;
            perPoint = condition.formula.get(i).f;

            if(starPoint <= point){
                if(perPoint == 0){
                    return new ConditionVO(id, starPoint);
                }
                conPoint = starPoint;
                while (conPoint + perPoint < point){
                    conPoint += perPoint;
                }
                return new ConditionVO(id, conPoint);
            }
        }
        return null;
    }
}
