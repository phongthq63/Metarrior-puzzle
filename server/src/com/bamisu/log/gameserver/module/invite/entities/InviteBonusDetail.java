package com.bamisu.log.gameserver.module.invite.entities;

import java.util.HashSet;
import java.util.Set;

public class InviteBonusDetail {
    public String id;
    public short point = 0;
    public Set<Short> complete = new HashSet<>();
}
