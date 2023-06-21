package com.bamisu.log.gameserver.datamodel.bag.entities;

public enum EWeaponType {
    SWORD("WCL001", "Sword"),         //kiếm
    DAGGER("WCL002", "Dagger"),        //giao găm
    AXE("WCL003", "Axe"),           //rìu
    HAMMER("WCL004", "Hammer"),        //búa
    MACE("WCL005", "Mace"),          //trượng
    CLAW("WCL006", "Claw"),          //móng vuốt
    LONGBOW("WCL007", "Longbow"),       //cung dài
    CROSSBOW("WCL008", "Crossbow"),      //nỏ
    BOMB("WCL009", "Bomb"),          //bom
    STAFF("WCL010", "Staff"),        //gậy
    BOOK("WCL011", "Book"),         //sách
    ORB("WCL012", "Orb"),          //quả cầu
    RIFLE("WCL013", "Rifle"),          //súng
    SPEAR("WCL014", "Spear");          //giáo

    String id;
    String name;

    EWeaponType(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static EWeaponType fromID(String id){
        for(EWeaponType weapon : EWeaponType.values()){
            if(weapon.getID().equals(id)){
                return weapon;
            }
        }
        return null;
    }

    public static EWeaponType fromName(String name){
        for(EWeaponType weapon : EWeaponType.values()){
            if(weapon.getName().equals(name)){
                return weapon;
            }
        }
        return null;
    }
}
