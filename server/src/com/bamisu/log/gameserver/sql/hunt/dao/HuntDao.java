package com.bamisu.log.gameserver.sql.hunt.dao;

import com.bamisu.gamelib.base.BaseExtension;
import com.bamisu.gamelib.base.db.SQLController;
import com.bamisu.gamelib.sql.hunt.HuntRewardDBO;
import com.smartfoxserver.v2.entities.Zone;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.hibernate.Session;
import org.hibernate.context.internal.ManagedSessionContext;

public class HuntDao {

    @WithSpan
    public boolean createHuntRewardToDB(Zone zone, HuntRewardDBO obj) {
        boolean result = false;

        SQLController sqlController = ((BaseExtension) zone.getExtension()).getSQLController();
        Session session = null;
        try {
            session = sqlController.getSessionFactory().openSession();
            ManagedSessionContext.bind(session);
            session.beginTransaction();

            session.saveOrUpdate(obj);

            session.getTransaction().commit();
            result = true;
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
        return result;
    }
}
