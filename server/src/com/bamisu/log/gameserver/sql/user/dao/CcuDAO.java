package com.bamisu.log.gameserver.sql.user.dao;

import com.bamisu.gamelib.base.BaseExtension;
import com.bamisu.gamelib.base.db.SQLController;
import com.bamisu.gamelib.sql.game.dbo.CcuDBO;
import com.bamisu.gamelib.task.LizThreadManager;
import com.bamisu.log.gameserver.manager.ServerManager;
import com.smartfoxserver.v2.entities.Zone;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.hibernate.Session;
import org.hibernate.context.internal.ManagedSessionContext;

import java.util.concurrent.TimeUnit;

public class CcuDAO {

    /**
     * Thread create sql manager
     *
     * @param zone
     */
    public static void startThreadSaveCCU(Zone zone) {
        //Update sql
        saveCCU(zone);

        LizThreadManager.getInstance().getFixExecutorServiceByName(LizThreadManager.EThreadPool.SQL_QUERY, 1).schedule(() -> startThreadSaveCCU(zone), 60, TimeUnit.SECONDS);
    }

    @WithSpan
    private synchronized static void saveCCU(Zone zone) {
        SQLController sqlController = ((BaseExtension) zone.getExtension()).getSQLController();
        Session session = null;
        try {
            session = sqlController.getSessionFactory().openSession();
            ManagedSessionContext.bind(session);
            session.beginTransaction();
            //logic
            CcuDBO ccuDBO = CcuDBO.create(ServerManager.getInstance().getCCU().get(zone.getName()));
            session.saveOrUpdate(ccuDBO);

            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ManagedSessionContext.unbind(sqlController.getSessionFactory());
            if (session != null) {
                session.close();
            }
        }
    }
}
