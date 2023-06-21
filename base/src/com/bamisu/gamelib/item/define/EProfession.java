package com.bamisu.gamelib.item.define;

public enum EProfession {
    NOTHING("0"),       //không có gì
    BRUTE("1"),         //Quái thú
    AGILE("2"),         //Nhanh nhẹn
    MAGE("3");          //Pháp sư

    String id;

    EProfession(String id) {
        this.id.equals(id);
    }

    public String getValue() {
        return id;
    }

    public static EProfession fromValue(String id){
        for(EProfession profession : EProfession.values()){
            if(profession.id.equals(id)){
                return profession;
            }
        }
        return null;
    }
}
