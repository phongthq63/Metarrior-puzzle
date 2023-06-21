package com.bamisu.log.gameserver.module.store;

import com.bamisu.gamelib.utils.UserUtils;
import com.bamisu.log.gameserver.datamodel.bag.UserBagModel;
import com.bamisu.log.gameserver.datamodel.store.StoreModel;
import com.bamisu.log.gameserver.module.bag.BagManager;
import com.bamisu.log.gameserver.module.store.cmd.send.SendRefreshStore;
import com.bamisu.log.gameserver.module.store.cmd.send.SendShowStoreInGame;
import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.log.gameserver.module.store.entities.*;
import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.entities.LIZRandom;
import com.bamisu.gamelib.entities.RandomObj;
import com.bamisu.gamelib.entities.ServerConstant;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.Zone;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

public class StoreManager {
    private static StoreManager ourInstance = null;

    public static StoreManager getInstance() {
        if (ourInstance == null) {
            ourInstance = new StoreManager();
        }
        return ourInstance;
    }

    private StoreConfig storeConfig;

    private StoreManager() {
        storeConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Store.FILE_PATH_CONFIG_STORE), StoreConfig.class);
    }

    StoreHandler storeHandler;

    public StoreHandler getAdventureHandler() {
        return storeHandler;
    }

    public void setStoreHandler(StoreHandler storeHandler) {
        this.storeHandler = storeHandler;
    }


    /*------GET CONFIG------*/
    public List<StoreVO> getListStoreConfig() {
        return storeConfig.listStore;
    }

    public List<ItemVO> getListItemConfig() {
        if (storeConfig == null) {
            storeConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Store.FILE_PATH_CONFIG_STORE), StoreConfig.class);
        }
        return storeConfig.listItem;
    }

    /*----------------------*/
    public StoreModel getStoreModel(long uid, Zone zone) {
        return StoreModel.copyFromDBtoObject(uid, zone);
    }

    public StoreVO getStoreDependOnId(int idStore) {
        for (StoreVO storeVO : getListStoreConfig()) {
            if (storeVO.id == idStore) {
                return storeVO;
            }
        }
        return null;
    }

    /**
     * Get list random item in store
     */
    public String[] getListItemStore(int idStore) {
        StoreVO storeVO = getStoreDependOnId(idStore);
        String[] items = new String[storeVO.size];
        int count = 0;
        while (count < items.length) {
            for (int j = 0; j < storeVO.sells.size(); j++) {
                for (int k = 0; k < storeVO.sells.get(j).slots.length; k++) {
                    int slot = storeVO.sells.get(j).slots[k] - 1;   //checked
                    int idItem = getRandomItem(storeVO.sells.get(j).items) - 1; //checked
                    boolean status = true;          //checked
                    int discount = getDiscountItem(storeVO.sells.get(j).discount); //checked
                    String item = slot + "-" + idItem + "-" + status + "-" + discount;
                    items[count] = item;
                    count++;
                }
            }
        }

        return items;
    }

    /**
     * Get the numbers of discount
     */
    private int getDiscountItem(List<DiscountStoreVO> discount) {
        if (discount.size() == 0) {
            return 0;
        } else {
            LIZRandom lizRandom = new LIZRandom();
            for (DiscountStoreVO discountStoreVO : discount) {
                lizRandom.push(new RandomObj(discountStoreVO.sale, discountStoreVO.rate));
            }
            return (int) lizRandom.next().value;
        }
    }

    /**
     * Choose one item in config
     */
    private int getRandomItem(int[] items) {
        int rnd = new Random().nextInt(items.length);
        return items[rnd];
    }

    /**
     * Get Store In Data depend on idStore
     */
    public StoreDataVO getDataInStore(long uid, Zone zone, int idStore) {
        StoreModel storeModel = getStoreModel(uid, zone);
        for (StoreDataVO storeDataVO : storeModel.listStore) {
            if (storeDataVO.idStore == idStore) {
                return storeDataVO;
            }
        }
        return null;
    }

    /**
     * refresh store depend on id store
     */
    public boolean refreshInStore(Zone zone, int idStore, StoreModel storeModel) {
        String[] listItem = getListItemStore(idStore);
        for (StoreDataVO storeDataVO : storeModel.listStore) {
            if (idStore == storeDataVO.idStore) {
                storeDataVO.listItem = listItem;
                return storeModel.saveToDB(zone);
            }
        }
        return false;
    }

    public boolean usingMoneyToRefreshStore(UserModel um, Zone zone, int idStore, User user) {
//        UserBagModel bag = BagManager.getInstance().getUserBagModel(um.userID, zone);
        StoreModel storeModel = getStoreModel(um.userID, zone);
        for (StoreDataVO storeDataVO : storeModel.listStore) {
            if (idStore == storeDataVO.idStore) {
                //Check number of turns
                if (storeDataVO.count <= 0) {
                    SendRefreshStore send = new SendRefreshStore(ServerConstant.ErrorCode.ERR_NOT_ENOUGH_TURN_TO_GET_REWARD);
                    storeHandler.send(send, user);
                    return false;
                } else {
                    int count = storeDataVO.count - 1;
                    storeDataVO.count--;
                    ResourcePackage money = new ResourcePackage(getRefreshStore(idStore, count));
                    money.amount = -money.amount;
                    List<ResourcePackage> list = new ArrayList<>();
                    list.add(money);
                    //Check change money successfully
                    if (BagManager.getInstance().addItemToDB(list, um.userID, zone, UserUtils.TransactionType.STORE_IN_GAME)) {
                        return refreshInStore(zone, idStore, storeModel);
                    }
                }

            }
        }
        return false;
    }

    public ResourcePackage getRefreshStore(int idStore, int position) {
        StoreVO storeVO = getStoreDependOnId(idStore);
        int value = storeVO.refresh.size() - 1 - position;
        return storeVO.refresh.get(value);
    }

    /**
     * Buy an item in store (Need idStore & slot)
     */
    public boolean buy(int idStore, int slot, UserModel um, Zone zone) {
        StoreModel storeModel = getStoreModel(um.userID, zone);
        for (StoreDataVO storeDataVO : storeModel.listStore) {
            if (storeDataVO.idStore == idStore) {

                List<ResourcePackage> cost = new ArrayList<>();
                String[] value = storeDataVO.listItem[slot].split("-"); //Architecture: Slot - Position - Status - Discount

                if (Boolean.parseBoolean(value[2])) {
                    ItemVO itemVO = getListItemConfig().get(Integer.parseInt(value[1]));
                    ResourcePackage price = itemVO.price.cloneNew();
                    //Check discount
                    if (Integer.parseInt(value[3]) != 0) {
                        price.amount -= ((price.amount * Integer.parseInt(value[3])) / 100);
                    }
                    price.amount = -price.amount;
                    cost.add(price);

                    //Change money
                    if (BagManager.getInstance().addItemToDB(cost, um.userID, zone, UserUtils.TransactionType.BUY_IN_STORE)) {
                        List<ResourcePackage> item = Collections.singletonList(itemVO.item);
                        BagManager.getInstance().addItemToDB(item, um.userID, zone, UserUtils.TransactionType.BUY_IN_STORE);
                        value[2] = "false";
                        storeDataVO.listItem[slot] = value[0] + "-" + value[1] + "-" + value[2] + "-" + value[3];
                        return storeModel.saveToDB(zone);
                    } else {
                        return false;
                    }
                }
                return false;

            }
        }
        return false;
    }

    /**
     * Auto Refresh when login
     */
    public StoreModel autoRefreshStore(long uid, Zone zone) {
        StoreModel storeModel = getStoreModel(uid, zone);
        for (StoreDataVO storeDataVO : storeModel.listStore) {
            //Check time
            if (Utils.getTimestampInSecond() > storeDataVO.time) {
//                storeDataVO.time = Utils.getTimestampInSecond() + (StoreManager.getInstance().getStoreDependOnId(storeDataVO.idStore).time * 24 * 60 *60);
                storeDataVO.time = getTimeRefresh(getStoreDependOnId(storeDataVO.idStore).time);
                storeDataVO.listItem = getListItemStore(storeDataVO.idStore);
                storeDataVO.count = getStoreDependOnId(storeDataVO.idStore).refresh.size();
//                refreshInStore(zone, storeDataVO.idStore, storeModel);
            }
        }
        storeModel.saveToDB(zone);
        return storeModel;
    }

//    public long calculateDate(String dateBeforeString, String dateAfterString) {
//        //Parsing the date
//        LocalDate dateBefore = LocalDate.parse(dateBeforeString);
//        LocalDate dateAfter = LocalDate.parse(dateAfterString);
//
//
//        //calculating number of days in between
//        long numOfDaysBetween = ChronoUnit.DAYS.between(dateBefore, dateAfter);
//        return numOfDaysBetween;
//    }

    public long getDeltaTime(long timeModel) {
        long delta = timeModel - Utils.getTimestampInSecond();
        return delta;
    }

    private Date convertStringToDate(String time) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void showItemInStore(int idStore, User user, long uid, Zone zone) {
//        StoreModel storeModel = getStoreModel(uid, zone);
        StoreModel storeModel = autoRefreshStore(uid, zone);
        for (StoreDataVO storeDataVO : storeModel.listStore) {
            if (storeDataVO.idStore == idStore) {
                SendShowStoreInGame send = new SendShowStoreInGame();
                send.list = storeDataVO;
                storeHandler.send(send, user);
                return;
            }
        }
        SendShowStoreInGame send = new SendShowStoreInGame(ServerConstant.ErrorCode.ERR_SYS);
        storeHandler.send(send, user);
    }

    public long getTimeRefresh(int days) {
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDate today = LocalDate.now();
        ZonedDateTime newDay = today.plusDays(days).atStartOfDay(zoneId);
        System.out.println(newDay.toLocalDate().atStartOfDay());

        Timestamp timestamp = Timestamp.valueOf(newDay.toLocalDate().atStartOfDay());
        return timestamp.getTime() / 1000;
    }
}
