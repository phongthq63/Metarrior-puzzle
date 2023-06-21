package com.bamisu.log.gameserver.datamodel.arena;

import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.datamodel.arena.entities.RecordArenaInfo;
import com.smartfoxserver.v2.entities.Zone;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserArenaRecordModel extends DataModel {

    public long uid;
    public List<RecordArenaInfo> records = new ArrayList<>();

    private final static int max = 20;



    public static UserArenaRecordModel create(long uid, Zone zone){
        UserArenaRecordModel userArenaRecordModel = new UserArenaRecordModel();
        userArenaRecordModel.uid = uid;
        userArenaRecordModel.saveToDB(zone);

        return userArenaRecordModel;
    }

    public final boolean saveToDB(Zone zone) {
        try {
            saveModel(String.valueOf(this.uid), zone);
            return true;
        } catch (DataControllerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static UserArenaRecordModel copyFromDBtoObject(long uid, Zone zone) {
        UserArenaRecordModel userArenaRecordModel = copyFromDBtoObject(String.valueOf(uid), zone);
        if(userArenaRecordModel == null){
            userArenaRecordModel = UserArenaRecordModel.create(uid, zone);
        }
        return userArenaRecordModel;
    }

    private static UserArenaRecordModel copyFromDBtoObject(String uid, Zone zone) {
        UserArenaRecordModel pInfo = null;
        try {
            String str = (String) getModel(uid, UserArenaRecordModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, UserArenaRecordModel.class);
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
    public List<RecordArenaInfo> readListRecord(){
        return records;
    }

    public boolean addArenaRecord(RecordArenaInfo data, Zone zone){
        if(data == null) return false;
        records.add(data);
        if(records.size() > max){
            records = records.stream().skip(records.size() - max).collect(Collectors.toList());
        }
        return saveToDB(zone);
    }
}
