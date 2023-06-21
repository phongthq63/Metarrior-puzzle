package com.bamisu.log.gameserver.module.invite.entities;

public class UpdateRewardDetail {
    public String accountID;
    public String id;
    public int point;

    public static UpdateRewardDetail create(String accountID, String id, int point){
        UpdateRewardDetail update = new UpdateRewardDetail();
        update.accountID = accountID;
        update.id = id;
        update.point = point;

        return update;
    }
}
