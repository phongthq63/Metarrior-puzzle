package com.bamisu.gamelib.sql.sdk.dao;

import com.bamisu.gamelib.base.db.SQLController;
import com.bamisu.gamelib.sql.sdk.dbo.LoginLogDBO;
import com.bamisu.gamelib.task.LizThreadManager;
import org.hibernate.Session;
import org.hibernate.context.internal.ManagedSessionContext;

import java.util.concurrent.ExecutorService;

/**
 * Create by Popeye on 10:48 AM, 10/3/2020
 */
public class LoginLogDAO {
    private static ExecutorService executorService = LizThreadManager.getInstance().getExecutorServiceByName("login");

    public static void save(SQLController sqlController, LoginLogDBO loginLogDBO) {
        executorService.submit(() -> {
            Session session = null;
            try {
                session = sqlController.getSessionFactory().openSession();
                ManagedSessionContext.bind(session);
                session.beginTransaction();
                //logic
                session.saveOrUpdate(loginLogDBO);
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
        });
    }
}
