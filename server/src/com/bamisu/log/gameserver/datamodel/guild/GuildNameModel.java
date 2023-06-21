package com.bamisu.log.gameserver.datamodel.guild;

import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.Zone;

public class GuildNameModel extends DataModel {
    public String gName = "";
    public long gId;



    public static GuildNameModel create(String gName, long gId, Zone zone){
        GuildNameModel guildNameModel = new GuildNameModel();
        guildNameModel.gName = gName;
        guildNameModel.gId = gId;
        guildNameModel.saveToDB(zone);

        return guildNameModel;
    }

    public final boolean saveToDB(Zone zone) {
        try {
            saveModel(readSaveId(), zone);
            return true;
        } catch (DataControllerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static GuildNameModel copyFromDBtoObject(String gName, Zone zone) {
        GuildNameModel pInfo = null;
        try {
            String str = (String) getModel(readSaveId(gName), GuildNameModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, GuildNameModel.class);
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
    private String readSaveId(){
        return gName.toLowerCase();
    }
    private static String readSaveId(String id){
        return id.toLowerCase();
    }

    public boolean haveGuild(){
        return gId > 0;
    }


    public boolean remove(Zone zone){
        gName = "";
        gId = -1;
        return saveToDB(zone);
    }
}
