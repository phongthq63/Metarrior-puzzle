package com.bamisu.log.gameserver.module.invite.config.entities;

import com.bamisu.log.gameserver.module.IAPBuy.config.entities.ConditionPropertiesVO;
import com.bamisu.log.gameserver.module.IAPBuy.config.entities.FormulaConditionPropertiesVO;
import com.bamisu.log.gameserver.module.IAPBuy.entities.ConditionVO;

public class InviteConditionVO {
    public String id;
    public ConditionPropertiesVO conditionReward;
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

    public boolean containCondition(int point){
        FormulaConditionPropertiesVO formula = conditionReward.formula.get(conditionReward.formula.size() - 1);
        if(point > formula.s){
            if(formula.f == 0)return false;
            return (point - formula.s) % formula.f == 0;
        }

        for(int i = conditionReward.formula.size() - 1; i >= 0; i--){
            if(conditionReward.formula.get(i).s == point)return true;
            if(conditionReward.formula.get(i).s < point){
                return (point - conditionReward.formula.get(i).s) % conditionReward.formula.get(i).f == 0;
            }
        }

        return false;
    }

    public int indexRewardBonus(int point){
        int index = 0;
        int mark = point;
        for(int i = conditionReward.formula.size() - 1; i >= 0; i--){
            if(mark > conditionReward.formula.get(i).s){
                index += (mark - conditionReward.formula.get(i).s) / conditionReward.formula.get(i).f;
                mark = conditionReward.formula.get(i).s;
            }
        }

        return index;
    }
}
