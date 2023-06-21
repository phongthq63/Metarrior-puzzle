package com.bamisu.log.gameserver.datamodel.quest.entities;

public class QuestInfo {

    public String id;
    public byte complete = 0;
    public int point;

    public static QuestInfo create(String id, int point) {
        QuestInfo questInfo = new QuestInfo();
        questInfo.id = id;
        questInfo.point = point;

        return questInfo;
    }
}
