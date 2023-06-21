package com.bamisu.log.gameserver.module.ingame.entities;

import com.bamisu.log.gameserver.module.characters.element.Element;
import com.bamisu.gamelib.utils.Utils;

/**
 * Create by Popeye on 4:09 PM, 10/28/2019
 */
public enum Diamond {
    RED(0),
    GREEN(1),
    BLUE(2),
    YELLOW(3),
    PURPLE(4),

    B_RED(10),
    B_GREEN(11),
    B_BLUE(12),
    B_YELLOW(13),
    B_PURPLE(14),

    F_RED(20),
    F_GREEN(21),
    F_BLUE(22),
    F_YELLOW(23),
    F_PURPLE(24),

    BLACK_HOLD(100);

    int value;

    Diamond(int value) {
        this.value = value;
    }

    public static Diamond getBombFromNomal(Diamond diamond) {
        return valueOf(diamond.getValue() % 10 + 10);
    }

    public static Diamond getFlashFromNomal(Diamond diamond) {
        return valueOf(diamond.getValue() % 10 + 20);
    }

    public int getValue() {
        return value;
    }

    public static Diamond valueOf(int value) {
        for (Diamond diamond : Diamond.values()) {
            if (diamond.getValue() == value) {
                return diamond;
            }
        }

        return null;
    }

    public boolean equalColor(Diamond diamond) {
        return this.getValue() == diamond.getValue();
    }

    public boolean sameColor(Diamond diamond) {
        return this.getValue() % 10 == diamond.getValue() % 10;
    }

    public static Diamond getRandomNomal() {
        return valueOf(Utils.randomInRange(RED.getValue(), PURPLE.getValue()));
    }

    public static Diamond getRandomBomb() {
        return valueOf(Utils.randomInRange(B_RED.getValue(), B_PURPLE.getValue()));
    }

    public static Diamond getRandomFlash() {
        return valueOf(Utils.randomInRange(F_RED.getValue(), F_PURPLE.getValue()));
    }

    @Override
    public String toString() {
        switch (this) {
            case RED:
                return "R";
            case GREEN:
                return "G";
            case BLUE:
                return "B";
            case YELLOW:
                return "Y";
            case PURPLE:
                return "P";

            case B_RED:
                return "(R)";
            case B_GREEN:
                return "(G)";
            case B_BLUE:
                return "(B)";
            case B_YELLOW:
                return "(Y)";
            case B_PURPLE:
                return "(P)";

            case F_RED:
                return "<R>";
            case F_GREEN:
                return "<G>";
            case F_BLUE:
                return "<B>";
            case F_YELLOW:
                return "<Y>";
            case F_PURPLE:
                return "<P>";
        }

        return "NULL";
    }

    public boolean canTouch() {
        return (getValue() >= B_RED.getValue() && getValue() <= B_PURPLE.getValue()) || (getValue() >= F_RED.getValue() && getValue() <= F_PURPLE.getValue());
    }

    public int getColor() {
        return valueOf(getValue() % 10).getValue();
    }

    public Element getElement() {
        if (this == RED) {
            return Element.FIRE;
        }

        if (this == GREEN) {
            return Element.FOREST;
        }

        if (this == BLUE) {
            return Element.ICE;
        }

        if (this == YELLOW) {
            return Element.LIGHTNING;
        }

        if (this == PURPLE) {
            return Element.GROUND;
        }

        return Element.FIRE;
    }

    public static Diamond fromElement(Element element) {
        if (element == Element.FIRE) {
            return RED;
        }

        if (element == Element.FOREST) {
            return GREEN;
        }

        if (element == Element.ICE) {
            return BLUE;
        }

        if (element == Element.GROUND) {
            return PURPLE;
        }

        if (element == Element.LIGHTNING) {
            return YELLOW;
        }

        return RED;
    }

    public static Diamond fromName(String element) {
        if (element.equalsIgnoreCase("Fire")) {
            return RED;
        }

        if (element.equalsIgnoreCase("Forest")) {
            return GREEN;
        }

        if (element.equalsIgnoreCase("Ice")) {
            return BLUE;
        }

        if (element.equalsIgnoreCase("Ground")) {
            return PURPLE;
        }

        if (element.equalsIgnoreCase("Lightning")) {
            return YELLOW;
        }

        return RED;
    }

    public static Diamond fromID(int value) {
        for(Diamond diamond : values()){
            if(diamond.getValue() == value) return diamond;
        }

        return null;
    }
}
