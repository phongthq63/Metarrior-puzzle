package com.bamisu.log.sdk.module.invitecode.model.entities;

import java.util.HashSet;
import java.util.Set;

public class InviteBonusDetail {
    public String id;
    public short point = 0;
    public Set<Short> complete = new HashSet<>();

    public static InviteBonusDetail create(String id, int point){
        InviteBonusDetail inviteBonusDetail = new InviteBonusDetail();
        inviteBonusDetail.id = id;
        inviteBonusDetail.point = (short) point;

        return inviteBonusDetail;
    }
}
