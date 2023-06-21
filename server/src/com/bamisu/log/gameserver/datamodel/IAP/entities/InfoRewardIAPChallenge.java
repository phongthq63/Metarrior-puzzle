package com.bamisu.log.gameserver.datamodel.IAP.entities;

import com.bamisu.log.gameserver.module.IAPBuy.defind.EIAPClaimType;

public class InfoRewardIAPChallenge {
    public int point;
    public boolean free;        //True da nhan --- False chua nhan
    public boolean predium;

    public static InfoRewardIAPChallenge create(int point, boolean free, boolean predium) {
        InfoRewardIAPChallenge infoRewardIAPChallenge = new InfoRewardIAPChallenge();
        infoRewardIAPChallenge.point = point;
        infoRewardIAPChallenge.free = free;
        infoRewardIAPChallenge.predium = predium;

        return infoRewardIAPChallenge;
    }
    public static InfoRewardIAPChallenge create(InfoRewardIAPChallenge data) {
        InfoRewardIAPChallenge infoRewardIAPChallenge = new InfoRewardIAPChallenge();
        infoRewardIAPChallenge.point = data.point;
        infoRewardIAPChallenge.free = data.free;
        infoRewardIAPChallenge.predium = data.predium;

        return infoRewardIAPChallenge;
    }

    public boolean canClaimReward(EIAPClaimType type){
        switch (type){
            case FREE:
                return !free;
            case PREDIUM:
                return !predium;
            case FREE_PREDIUM:
                return !predium || !free;
        }
        return false;
    }

    public boolean claimReward(EIAPClaimType type){
        switch (type){
            case FREE:
                free = true;
                return true;
            case PREDIUM:
                predium = true;
                return true;
            case FREE_PREDIUM:
                free = true;
                predium = true;
                return true;
        }
        return false;
    }
}
