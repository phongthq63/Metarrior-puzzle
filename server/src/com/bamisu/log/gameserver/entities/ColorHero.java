package com.bamisu.log.gameserver.entities;

public enum ColorHero {
    PURPLE_ELITE(-3, ""),
    PURPLE_RARE_3(-2, ""),
    BLUE_N_PURPLE(-1, ""),
    GREEN(0, "RARE"),
    GREEN_PLUS(1, "RARE+"),
    BLUE(2, "EPIC"),
    BLUE_PLUS(3, "EPIC+"),
    PURPLE(4, "LEGENDARY"),
    PURPLE_PLUS(5, "LEGENDARY+"),
    YELLOW(6, "FABLED"),
    YELLOW_PLUS(7, "FABLED+"),
    ORANGE(8, "MYTHIC"),
    ORANGE_PLUS(9, "MYTHIC+"),
    RED(10, "ASCENDED"),
    RED_PLUS(11, "ASCENDED+");

    int star;
    String name;

    ColorHero(int star, String description) {
        this.star = star;
        this.name = description;
    }

    public int getStar() {
        return star;
    }

    public String getName() {
        return name;
    }

    public static ColorHero fromValue(int star){
        for(ColorHero color : ColorHero.values()){
            if(color.star == star){
                return color;
            }
        }
        return null;
    }

    public static ColorHero fromName(String name){
        for(ColorHero color : ColorHero.values()){
            if(color.name.equalsIgnoreCase(name)){
                return color;
            }
        }
        return null;
    }

    public static boolean haveColor(String colorHero){
        if(ColorHero.valueOf(colorHero) != null){
            return true;
        }
        return false;
    }

    public static int getStarFromName(String description){
        for(ColorHero color : ColorHero.values()){
            if(color.name.toLowerCase().equals(description.toLowerCase())){
                return color.star;
            }
        }
        return -999;
    }
}
