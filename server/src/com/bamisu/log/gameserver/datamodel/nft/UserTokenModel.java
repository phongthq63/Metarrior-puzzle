package com.bamisu.log.gameserver.datamodel.nft;

import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.gamelib.entities.TokenResourcePackage;
import com.bamisu.gamelib.entities.TransactionDetail;
import com.bamisu.gamelib.sql.game.dbo.TransactionDBO;
import com.bamisu.gamelib.sql.game.dbo.UserTokenDBO;
import com.bamisu.gamelib.task.TransactionTask;
import com.bamisu.gamelib.utils.UserUtils;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.module.nft.defind.ETokenBC;
import com.bamisu.log.gameserver.module.nft.entities.ChangeTokenResult;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Created by Quach Thanh Phong
 * On 2/13/2022 - 8:37 PM
 */
public class UserTokenModel extends DataModel {

    public long uid;
    private Object lockToken = new Object();
    public long mewa = 0;
    public long sog = 0;
    public long card = 0;
    public long ticketSpin = 0;
    public long ticketSpinSuper = 0;
    public long ticket = 0;
    public long turnTicket = 0;
    public double busd = 0;

    private Logger logger = Logger.getLogger(UserTokenModel.class);


    private void init() {
        this.mewa = 30000L;
        this.sog = 100000L;
        this.card = 0;
        this.ticketSpin = 100L;
        this.ticketSpinSuper = 50L;
        this.ticket = 0;
        this.busd = 30000L;
    }

    public static UserTokenModel createUserMineTokenModel(long uid, Zone zone) {
        UserTokenModel userTokenModel = new UserTokenModel();
        userTokenModel.uid = uid;
//        userTokenModel.init();
        userTokenModel.saveToDB(zone);

        return userTokenModel;
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

    public static UserTokenModel copyFromDBtoObject(long uId, Zone zone) {
        return copyFromDBtoObject(String.valueOf(uId), zone);
    }

    private static UserTokenModel copyFromDBtoObject(String uId, Zone zone) {
        UserTokenModel pInfo = null;
        try {
            String str = (String) getModel(uId, UserTokenModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, UserTokenModel.class);
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
    /*public synchronized boolean updateCountToken(ETokenBC token, long count, Zone zone) {
        if (token == null) return false;
        long inDB = readToken(token);
        if (inDB + count < 0) return false;
        this.mapMined.put(token.getId(), inDB + count);

        return saveToDB(zone);
    }*/

    public ChangeTokenResult changeToken(List<TokenResourcePackage> resourcePackageList, TransactionDetail detail, Zone zone) {
        ChangeTokenResult result = new ChangeTokenResult();
        synchronized (lockToken) {
            for (TokenResourcePackage resourcePackage : resourcePackageList) {
                //ko du tien
                boolean isEnoughResource = true;
                double doubleValue = 0;
                long longValue = 0L;
                if (resourcePackage.getValue().getClass().getSimpleName().equalsIgnoreCase("double")) {
                    doubleValue = (double) resourcePackage.getValue();
                    longValue = (long) doubleValue;
                }

                if (resourcePackage.getValue().getClass().getSimpleName().equalsIgnoreCase("integer")) {
                    longValue = (int) resourcePackage.getValue();
                    doubleValue = longValue;
                }

                if (resourcePackage.getValue().getClass().getSimpleName().equalsIgnoreCase("long")) {
                    longValue = (long) resourcePackage.getValue();
                    doubleValue = longValue;
                }

                if (resourcePackage.getId().equalsIgnoreCase(ETokenBC.BUSD.getId())) {
                    double value = this.readToken(ETokenBC.BUSD);
                    value += doubleValue;
                    if (value < 0) {
                        isEnoughResource = false;
                    }
                } else {
                    long value = this.readToken(Objects.requireNonNull(ETokenBC.fromId(resourcePackage.getId())));
                    value += longValue;

                    if (value < 0) {
                        isEnoughResource = false;
                    }
                }
                if (!isEnoughResource) {
                    result.setSuccess(false);
                    result.getNotEnoughList().add(ETokenBC.fromId(resourcePackage.getId()));
                }
            }

            //check thành công
            if (result.isSuccess()) {
                //thay doi gia tri
                for (TokenResourcePackage resourcePackage : resourcePackageList) {
                    this.updateToken(resourcePackage.getId(), resourcePackage.getValue(), zone, detail);
                }

                //create thanh cong
                Map<String, Object> map = new HashMap<>();
                map.put(Params.UID, uid);
                if (saveToDB(zone)) {
                    SFSArray arrayCurrent = new SFSArray();
                    for (TokenResourcePackage vo : resourcePackageList) {
                        vo.setValue(this.readToken(vo.getId()));
                        SFSObject sfsObject = vo.toSFSObject(map);
                        arrayCurrent.addSFSObject(sfsObject);
                    }
                    //send notify change resource
                    UserUtils.changeToken(uid, arrayCurrent, detail, zone);
                    return result;
                }

                //create ko thanh cong
                return new ChangeTokenResult(false, new ArrayList<>());
            }


            return result;
        }
    }

    public <T> T readToken(ETokenBC token) {
        return (T) this.readToken(token.getId());
    }

    public Object readToken(String id) {
        if (ETokenBC.MEWA.getId().equalsIgnoreCase(id)) {
            return this.mewa;
        } else if (ETokenBC.SOG.getId().equalsIgnoreCase(id)) {
            return this.sog;
        } else if (ETokenBC.CARD.getId().equalsIgnoreCase(id)) {
            return this.card;
        } else if (ETokenBC.TICKET.getId().equalsIgnoreCase(id)) {
            return this.ticket;
        } else if (ETokenBC.TICKET_SPIN.getId().equalsIgnoreCase(id)) {
            return this.ticketSpin;
        } else if (ETokenBC.TICKET_SPIN_SUPER.getId().equalsIgnoreCase(id)) {
            return this.ticketSpinSuper;
        } else if (ETokenBC.BUSD.getId().equalsIgnoreCase(id)) {
            return this.busd;
        } else if (ETokenBC.TURN_TICKET.getId().equalsIgnoreCase(id)) {
            return this.turnTicket;
        }

        return 0;
    }

    private void updateToken(String id, Object value, Zone zone, TransactionDetail transactionDetail) {
        double doubleValue = 0;
        long longValue = 0;
        double moneyAfterUpdate = 0d;
        double moneyBeforeUpdate = 0d;
        boolean isUpdate = false;
        if (value.getClass().getSimpleName().equalsIgnoreCase("long")) {
            longValue = (long) value;
            doubleValue = longValue;
        }

        if (value.getClass().getSimpleName().equalsIgnoreCase("integer")) {
            longValue = (int) value;
            doubleValue = longValue;
        }

        if (value.getClass().getSimpleName().equalsIgnoreCase("double")) {
            doubleValue = (double) value;
            longValue = (long) doubleValue;
        }

        if (ETokenBC.MEWA.getId().equalsIgnoreCase(id)) {
            moneyBeforeUpdate = this.mewa;
            this.mewa += longValue;
            moneyAfterUpdate = this.mewa;
            isUpdate = true;
        } else if (ETokenBC.SOG.getId().equalsIgnoreCase(id)) {
            moneyBeforeUpdate = this.sog;
            this.sog += longValue;
            moneyAfterUpdate = this.sog;
            isUpdate = true;
        } else if (ETokenBC.CARD.getId().equalsIgnoreCase(id)) {
           this.card += longValue;
        } else if (ETokenBC.TICKET.getId().equalsIgnoreCase(id)) {
            this.ticket += longValue;
        } else if (ETokenBC.TICKET_SPIN.getId().equalsIgnoreCase(id)) {
            moneyBeforeUpdate = this.ticketSpin;
            this.ticketSpin += longValue;
            moneyAfterUpdate = this.ticketSpin;
            isUpdate = true;
        } else if (ETokenBC.TICKET_SPIN_SUPER.getId().equalsIgnoreCase(id)) {
            moneyBeforeUpdate = this.ticketSpinSuper;
           this.ticketSpinSuper += longValue;
            moneyAfterUpdate = this.ticketSpinSuper;
            isUpdate = true;
        } else if (ETokenBC.BUSD.getId().equalsIgnoreCase(id)) {
            moneyBeforeUpdate = this.busd;
           this.busd += doubleValue;
            moneyAfterUpdate = this.busd;
            isUpdate = true;
        } else if (ETokenBC.TURN_TICKET.getId().equalsIgnoreCase(id)) {
            this.turnTicket += longValue;
        }

        this.saveToDB(zone);
        if (isUpdate) {
            TransactionDBO dbo = new TransactionDBO(this.uid, id, moneyBeforeUpdate, doubleValue, moneyAfterUpdate, transactionDetail);
            UserTokenDBO userTokenDBO = new UserTokenDBO(this.uid, this.busd, this.mewa, this.sog, this.ticketSpin, this.ticketSpinSuper);
            TransactionTask.getInstance().add(dbo);
            TransactionTask.getInstance().add(userTokenDBO);
        }
    }

    @JsonIgnore
    public Map<String, Object> getAllToken() {
        Map<String, Object> map = new HashMap<>();
        map.put(ETokenBC.MEWA.getId(), this.readToken(ETokenBC.MEWA.getId()));
        map.put(ETokenBC.SOG.getId(), this.readToken(ETokenBC.SOG.getId()));
        map.put(ETokenBC.CARD.getId(), this.readToken(ETokenBC.CARD.getId()));
        map.put(ETokenBC.TICKET.getId(), this.readToken(ETokenBC.TICKET.getId()));
        map.put(ETokenBC.TICKET_SPIN.getId(), this.readToken(ETokenBC.TICKET_SPIN.getId()));
        map.put(ETokenBC.TICKET_SPIN_SUPER.getId(), this.readToken(ETokenBC.TICKET_SPIN_SUPER.getId()));
        map.put(ETokenBC.BUSD.getId(), this.readToken(ETokenBC.BUSD.getId()));
        return map;
    }
}
