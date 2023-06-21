package com.bamisu.log.gameserver.datamodel.guild;

import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.Zone;

public class GuildIDModel extends DataModel {
    public String id;
    public long gId;



    public static GuildIDModel create(String id, long gId, Zone zone){
        GuildIDModel guildIDModel = new GuildIDModel();
        guildIDModel.id = id;
        guildIDModel.gId = gId;
        guildIDModel.saveToDB(zone);

        return guildIDModel;
    }

    public final boolean saveToDB(Zone zone) {
        try {
            saveModel(id, zone);
            return true;
        } catch (DataControllerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static GuildIDModel copyFromDBtoObject(String id, Zone zone) {
        GuildIDModel pInfo = null;
        try {
            String str = (String) getModel(id, GuildIDModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, GuildIDModel.class);
                if (pInfo != null) {
//                    pInfo.writeLog();
                }
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }
        return pInfo;
    }



    /*----------------------------------------------------------------------------------------------------------------*/
    /*----------------------------------------------------------------------------------------------------------------*/
    public boolean haveGuild(){
        return gId > 0;
    }



    public boolean remove(Zone zone){
        id = "";
        gId = -1;
        return saveToDB(zone);
    }
}
