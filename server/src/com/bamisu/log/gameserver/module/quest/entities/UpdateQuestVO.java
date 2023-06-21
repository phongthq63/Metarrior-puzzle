package com.bamisu.log.gameserver.module.quest.entities;

public class UpdateQuestVO {
    public String id;
    public int point;

    public static UpdateQuestVO create(String id, int point){
        UpdateQuestVO update = new UpdateQuestVO();
        update.id = id;
        update.point = point;

        return update;
    }
}
