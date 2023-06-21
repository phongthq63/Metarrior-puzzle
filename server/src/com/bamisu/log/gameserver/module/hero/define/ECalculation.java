package com.bamisu.log.gameserver.module.hero.define;

public enum ECalculation {
    BASE(0, "base"),
    LEVEL(1, "level"),
    ENHANCE_LEVEL(2, "enhanceLevel"),
    STAR(3, "star"),
    ENHANCE_STAR(4, "enhanceStar"),
    BREAK_THOUGHT(5, "breakThought");

    int id;
    String params;

    public int getId() {
        return id;
    }

    public String getParams() {
        return params;
    }

    ECalculation(int id, String params) {
        this.id = id;
        this.params = params;
    }

    public static ECalculation fromID(int id){
        for(ECalculation index : ECalculation.values()){
            if(id == index.id){
                return index;
            }
        }
        return null;
    }
}
