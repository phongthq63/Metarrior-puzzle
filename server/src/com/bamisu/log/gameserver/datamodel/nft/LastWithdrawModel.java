package com.bamisu.log.gameserver.datamodel.nft;

import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.nft.entities.WithdrawTransactionVO;
import com.smartfoxserver.v2.entities.Zone;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LastWithdrawModel extends DataModel {
    public long uid;
    public List<WithdrawTransactionVO> transactions;
    public double total;
    public String lastWithdraw;

    public LastWithdrawModel() {

    }

    public LastWithdrawModel(long uid) {
        this.uid = uid;
        this.transactions = new ArrayList<>();
        this.total = 0;
        this.lastWithdraw = getLocalDate();
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

    public static LastWithdrawModel load(long uid, Zone zone) {
        LastWithdrawModel model = null;
        try {
            String str = (String) getModel(String.valueOf(uid), LastWithdrawModel.class, zone);
            if (str != null) {
                model = Utils.fromJson(str, LastWithdrawModel.class);
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }

        if (model == null) {
            model = new LastWithdrawModel(uid);
            model.saveToDB(zone);
        }

        String currentDate = getLocalDate();
        if (!currentDate.equalsIgnoreCase(model.lastWithdraw)) {
            model.lastWithdraw = currentDate;
            model.total = 0;
            model.saveToDB(zone);
        }

        return model;
    }

    private static String getLocalDate(Timestamp timestamp) {
        LocalDate localDate = timestamp.toLocalDateTime().toLocalDate();
        return localDate.toString();
    }

    private static String getLocalDate() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        return getLocalDate(timestamp);
    }
}
