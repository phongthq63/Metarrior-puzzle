package com.bamisu.log.gameserver.sql.user.dao;

import com.bamisu.gamelib.base.BaseExtension;
import com.bamisu.gamelib.sql.game.dbo.PlayerDBO;
import com.bamisu.gamelib.base.db.SQLController;
import com.bamisu.gamelib.sql.sdk.dbo.AccountDBO;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.Zone;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.context.internal.ManagedSessionContext;

public class PlayerDAO {

    @WithSpan
    public static void save(Zone zone, long uid, String accountID) {
        SQLController sqlController = ((BaseExtension) zone.getExtension()).getSQLController();
        Session session = null;
        try {
            session = sqlController.getSessionFactory().openSession();
            ManagedSessionContext.bind(session);
            session.beginTransaction();
            //logic
            PlayerDBO dbo = PlayerDBO.create(uid, accountID, Utils.getTimestampInSecond(), Utils.getTimestampInSecond());
            session.saveOrUpdate(dbo);
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
}
