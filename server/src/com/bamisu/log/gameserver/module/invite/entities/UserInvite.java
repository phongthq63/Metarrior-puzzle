package com.bamisu.log.gameserver.module.invite.entities;

import com.bamisu.log.gameserver.module.invite.defind.EBonusInvite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserInvite {
    public String accountID;
    public String accountIDInvite;
    public String inviteCode;
    public Map<String,Map<EBonusInvite,Boolean>> mapInvited = new HashMap<>();      //   Uid ---- Link Account
    public List<InviteBonusDetail> inviteBonus = new ArrayList<>();

    private final Object lockBonusInvite = new Object();



    public boolean canRewardInviteBonus(String id, int point) {
        synchronized (lockBonusInvite) {
            for (InviteBonusDetail index : inviteBonus) {
                if (index.id.equals(id)) {
                    if (point > index.point) return false;
                    return !index.complete.contains((short) point);
                }
            }
        }
        return false;
    }
}
