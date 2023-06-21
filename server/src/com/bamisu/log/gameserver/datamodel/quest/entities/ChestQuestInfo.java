package com.bamisu.log.gameserver.datamodel.quest.entities;

import java.util.HashSet;
import java.util.Set;

public class ChestQuestInfo {
    public int point;
    public Set<String> complete = new HashSet<>();

    public static ChestQuestInfo create(){
        ChestQuestInfo data = new ChestQuestInfo();
        data.point = 0;

        return data;
    }
}
