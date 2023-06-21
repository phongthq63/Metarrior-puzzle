package com.bamisu.log.gameserver.sql.store.dao;

import com.bamisu.gamelib.base.BaseExtension;
import com.bamisu.gamelib.base.db.SQLController;
import com.bamisu.gamelib.sql.game.dbo.IAPPackageDBO;
import com.bamisu.gamelib.task.LizThreadManager;
import com.bamisu.log.gameserver.datamodel.IAP.IAPCacheSaveModel;
import com.bamisu.log.gameserver.module.IAPBuy.IAPBuyManager;
import com.smartfoxserver.v2.entities.Zone;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.hibernate.Session;
import org.hibernate.context.internal.ManagedSessionContext;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class IAPPackageDAO {
    @WithSpan
    public static void save(IAPPackageDBO iapPackageDBO, Zone zone){
        SQLController sqlController = ((BaseExtension) zone.getExtension()).getSQLController();
        Session session = null;
        try {
            session = sqlController.getSessionFactory().openSession();
            ManagedSessionContext.bind(session);
            session.beginTransaction();
            //logic
            session.saveOrUpdate(iapPackageDBO);
            //
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
    /**
     * Thread create sql manager
     * @param zone
     */
//    public static void startThreadSaveIAP(Zone zone){
//        //Update sql
//        savePurchaseIAP(zone);
//
//        LizThreadManager.getInstance().getFixExecutorServiceByName(LizThreadManager.EThreadPool.SQL_QUERY, 1).schedule(() -> startThreadSaveIAP(zone),10, TimeUnit.SECONDS);
//    }

//    private static void savePurchaseIAP(Zone zone){
//        IAPCacheSaveModel cacheSaveModel = IAPBuyManager.getInstance().getIAPCacheSaveModel(zone);
//        if(!cacheSaveModel.haveCache())return;
//
//        SQLController sqlController = ((BaseExtension) zone.getExtension()).getSQLController();
//        Session session = null;
//        try {
//            session = sqlController.getSessionFactory().openSession();
//            ManagedSessionContext.bind(session);
//            session.beginTransaction();
//            //logic
//            List<IAPPackageDBO> iapPackageDBOList = IAPBuyManager.getInstance().readCacheIAPSaveModel(cacheSaveModel);
//            for(IAPPackageDBO dbo :iapPackageDBOList){
//                session.saveOrUpdate(dbo);
//            }
//
//            session.getTransaction().commit();
//            IAPBuyManager.getInstance().clearCacheIAPSaveModel(cacheSaveModel, zone);
//            //
//        } catch (Exception e) {
//            e.printStackTrace();
//            if (session != null && session.isOpen() && session.getTransaction() != null)
//                session.getTransaction().rollback();
//        } finally {
//            ManagedSessionContext.unbind(sqlController.getSessionFactory());
//            if (session != null) {
//                session.close();
//            }
//        }
//    }
}
