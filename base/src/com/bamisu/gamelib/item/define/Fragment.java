package com.bamisu.gamelib.item.define;

public enum Fragment {
    RANDOM("R"),        //Random
    HERO("T");          //Chi dinh Hero
//    ELEMENT("E"),       //Depend Element
//    KINGDOM("K");       //Depend Kingdom

    String id;

    Fragment(String id) {
        this.id = id;
    }

    Fragment(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id){
        this.id = id;
    }

    public static Fragment fromID(String id){
        for(Fragment fragment : Fragment.values()){
            if(fragment.id.equals(String.valueOf(id.charAt(3)))){
                return fragment;
            }
        }
        return null;
    }

    public static String getIdSummon(String id){
        switch (fromID(id)){
            case RANDOM:
                return id.substring(4);
            case HERO:
                return id.substring(3);
        }
        return "";
    }
}
