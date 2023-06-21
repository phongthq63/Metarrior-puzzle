package com.bamisu.log.gameserver.module.characters.clas;

public enum EClass {
    BRUTE("CLS201", "Brute"),
    AGILE("CLS203", "Agile"),
    MAGE("CLS206", "Mage");

    String id;
    String name;

    EClass(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static EClass fromID(String id){
        for(EClass index : EClass.values()){
            if(index.getId().equalsIgnoreCase(id)){
                return index;
            }
        }
        return null;
    }

    public static EClass fromName(String name){
        for(EClass index : EClass.values()){
            if(index.getName().equalsIgnoreCase(name)){
                return index;
            }
        }
        return null;
    }
}
