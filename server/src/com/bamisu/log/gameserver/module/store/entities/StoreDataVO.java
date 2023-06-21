package com.bamisu.log.gameserver.module.store.entities;

import com.bamisu.log.gameserver.module.store.StoreManager;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;


public class StoreDataVO {
    public int idStore;
    public String[] listItem;  //Architecture: Slot - Id Item - Bought or not - Discount
    public int count; //Refresh
    public long time;

    public StoreDataVO(int idStore, String[] listItem, int count, long time) {
        this.idStore = idStore;
        this.listItem = listItem;
        this.count = count;
        this.time = time;
    }

    public StoreDataVO() {
    }

//    public SFSObject toSFSObject(){
//        int delta = StoreManager.getInstance().getDeltaTime(time);
//        SFSObject sfsObject = new SFSObject();
//        sfsObject.putInt(Params.ID_STONE, idStore);
//        sfsObject.putInt(Params.TIME, delta);
//        sfsObject.putInt(Params.COUNT, count);
//        SFSArray sfsArray = new SFSArray();
//        for (String vo: listItem){
//            SFSObject value = new SFSObject();
//            String[] data = vo.split("-");
//            value.putInt(Params.SLOT, Integer.parseInt(data[0]));
//            value.putUtfString(Params.POSITION, data[1]);
//            value.putBool(Params.STATUS, Boolean.parseBoolean(data[2]));
//            value.putInt(Params.DISCOUNT, Integer.parseInt(data[3]));
//            sfsArray.addSFSObject(value);
//        }
//        sfsObject.putSFSArray(Params.LIST, sfsArray);
//        return sfsObject;
//    }
}
