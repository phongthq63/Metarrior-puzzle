package com.bamisu.gamelib.item.define;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public enum ColorGem {
    EMERALD_OF_VITALITY(1),
    RUBY_OF_MIGHT(2),
    AQUAMARINE_OF_SIGHT(3),
    SAPPHIRE_OF_PROTECTION(4),
    MOONSTONE_OF_SWIFTNESS(5),
    TOPAZ_OF_FORTUNE(6),
    GARNET_OF_FEROCITY(7),
    ONYX_OF_TENACITY(8),
    AMETHYST_OF_LETHALITY(9);

    int value;

    ColorGem(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ColorGem fromValue(int star){
        for(ColorGem color : ColorGem.values()){
            if(color.value == star){
                return color;
            }
        }
        return null;
    }

    private static final List<ColorGem> values = Collections.unmodifiableList(Arrays.asList(values()));
    private static final int size = values.size();
    public static ColorGem randomGem(){
        Random random = new Random();
        return values.get(random.nextInt(size));
    }
}
