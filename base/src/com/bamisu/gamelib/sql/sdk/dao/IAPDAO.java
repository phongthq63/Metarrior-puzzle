package com.bamisu.gamelib.sql.sdk.dao;

import com.bamisu.gamelib.base.db.SQLController;
import com.bamisu.gamelib.sql.sdk.dbo.IAPDBO;
import org.hibernate.Session;
import org.hibernate.context.internal.ManagedSessionContext;

/**
 * Create by Popeye on 1:03 PM, 10/31/2020
 */
public class IAPDAO {
    public static void save(SQLController sqlController, IAPDBO dbo){
        Session session = null;
        try {
            session = sqlController.getSessionFactory().openSession();
            ManagedSessionContext.bind(session);
            session.beginTransaction();
            //logic
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
