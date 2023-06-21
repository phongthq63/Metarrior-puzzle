package com.bamisu.log.gameserver.datamodel.guild;

import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.Zone;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GuildManagerStatusModel extends DataModel {
    private final static long id = 0;

    public Set<String> setId = new HashSet<>();

    public static GuildManagerStatusModel createGuildManagerStatusModel(Zone zone){
        GuildManagerStatusModel guildManagerStatusModel = new GuildManagerStatusModel();
        guildManagerStatusModel.saveToDB(zone);

        return guildManagerStatusModel;
    }

    public final boolean saveToDB(Zone zone) {
        try {
            saveModel(String.valueOf(this.id), zone);
            return true;

        } catch (DataControllerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static GuildManagerStatusModel copyFromDBtoObject(Zone zone) {
        GuildManagerStatusModel pInfo = null;
        try {
            String str = (String) getModel(String.valueOf(id), GuildManagerStatusModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, GuildManagerStatusModel.class);
                if (pInfo != null) {
//                    pInfo.wrNiteLog();
                }
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }
        if(pInfo == null){
            pInfo = createGuildManagerStatusModel(zone);
        }
        return pInfo;
    }



    /*-----------------------------------------------------------------------------------------------------------------*/
    /*-----------------------------------------------------------------------------------------------------------------*/

    public Set<String> readID(){
        return setId;
    }

    public boolean addID(String gId, Zone zone){
        setId.add(gId);
        return saveToDB(zone);
    }

    public boolean removeID(String gId, Zone zone){
        setId.remove(gId);
        return saveToDB(zone);
    }
}
