package com.bamisu.gamelib.item.define;

public enum ColorWeapon {
    GREY(1),
    GREEN(2),
    TEAL(3),
    BLUE(4),
    PURPLE(5);

    int star;

    ColorWeapon(int value) {
        this.star = value;
    }

    public int getStar() {
        return star;
    }

    public static ColorWeapon fromValue(int star){
        for(ColorWeapon color : ColorWeapon.values()){
            if(color.star == star){
                return color;
            }
        }
        return null;
    }
}
