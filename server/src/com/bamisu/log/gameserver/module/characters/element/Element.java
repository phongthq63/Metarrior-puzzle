package com.bamisu.log.gameserver.module.characters.element;

import java.util.Arrays;
import java.util.List;

/**
 * Create by Popeye on 11:41 AM, 2/21/2020
 */
public enum Element {
    FIRE("E401", "Fire"),
    ICE("E402", "Ice"),
    FOREST("E403", "Forest"),
    GROUND("E404", "Ground"),
    LIGHTNING("E405", "Lightning"),
    LIGHT("E406", "Light"),
    DARK("E407", "Dark");

    public static List<Element> v1 = Arrays.asList(FIRE, ICE, FOREST, GROUND, LIGHTNING);
    public static List<Element> v2 = Arrays.asList(FIRE, ICE, FOREST, GROUND, LIGHTNING, LIGHT, DARK);

    String id;
    String name;

    Element(String id, String name){
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static Element fromID(String id){
        for(Element element : values()){
            if(element.getId().equalsIgnoreCase(id)){
                return element;
            }
        }

        return null;
    }

    public static Element fromName(String name){
        for(Element element : values()){
            if(element.getName().equalsIgnoreCase(name)){
                return element;
            }
        }

        return null;
    }
}
