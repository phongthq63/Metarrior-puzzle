package com.bamisu.log.gameserver.sql.bag.dao;

import com.bamisu.gamelib.base.BaseExtension;
import com.bamisu.gamelib.base.db.SQLController;
import com.bamisu.gamelib.sql.game.dbo.MoneyChangeDBO;
import com.bamisu.gamelib.task.LizThreadManager;
import com.bamisu.log.gameserver.module.bag.BagManager;
import com.smartfoxserver.v2.entities.Zone;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.hibernate.Session;
import org.hibernate.context.internal.ManagedSessionContext;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MoneyChangeDAO {

    /**
     * Thread create sql manager
     *
     * @param zone
     */
    public static void startThreadSaveMoney(Zone zone) {
        //Update sql
        saveChangeMoney(zone);

        LizThreadManager.getInstance().getFixExecutorServiceByName(LizThreadManager.EThreadPool.SQL_QUERY, 1).schedule(() -> startThreadSaveMoney(zone), 10, TimeUnit.SECONDS);
    }

    @WithSpan
    private synchronized static void saveChangeMoney(Zone zone) {
        List<MoneyChangeDBO> moneyChangeDBOList = BagManager.getInstance().getMoneyCacheSaveModel(zone);
        List<MoneyChangeDBO> tmpDbo = new ArrayList<>(moneyChangeDBOList);
        BagManager.getInstance().clearCacheMoneyChange(zone);

        SQLController sqlController = ((BaseExtension) zone.getExtension()).getSQLController();
        Session session = null;
        try {
            session = sqlController.getSessionFactory().openSession();
            ManagedSessionContext.bind(session);
            session.beginTransaction();
            //logic
            for (MoneyChangeDBO dbo : tmpDbo) {
                session.saveOrUpdate(dbo);
            }
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (session != null && session.isOpen() && session.getTransaction() != null)
                session.getTransaction().rollback();
        } finally {
            ManagedSessionContext.unbind(sqlController.getSessionFactory());
            if (session != null) {
                session.close();
            }
        }
    }
}
