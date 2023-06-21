package com.bamisu.gamelib.sql.sdk.dao;

import com.bamisu.gamelib.base.db.SQLController;
import com.bamisu.gamelib.sql.sdk.dbo.GiftcodeDBO;
import org.hibernate.Session;
import org.hibernate.context.internal.ManagedSessionContext;

import java.sql.Timestamp;
import java.util.List;

public class GiftcodeDAO {
    public static void create(List<GiftcodeDBO> codes, SQLController sqlController) {
        if (codes.isEmpty()) {
            return;
        }

        Session session = null;
        try {
            session = sqlController.getSessionFactory().openSession();
            ManagedSessionContext.bind(session);
            session.beginTransaction();
            for (GiftcodeDBO code : codes) {
                session.save(code);
            }

            session.getTransaction().commit();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            ManagedSessionContext.unbind(sqlController.getSessionFactory());
            if (session != null) {
                session.close();
            }
        }
    }

    public static void update(String code, long userId, SQLController sqlController) {
        Session session = null;
        try {
            session = sqlController.getSessionFactory().openSession();
            ManagedSessionContext.bind(session);
            GiftcodeDBO giftcodeDBO = (GiftcodeDBO) session.createQuery("from GiftcodeDBO where code=:code")
                    .setString("code", code)
                    .setMaxResults(1)
                    .uniqueResult();
            if (giftcodeDBO == null) {
                return;
            }

            giftcodeDBO.user_id = userId;
            giftcodeDBO.used_at = new Timestamp(System.currentTimeMillis());
            session.saveOrUpdate(giftcodeDBO);
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            ManagedSessionContext.unbind(sqlController.getSessionFactory());
            if (session != null) {
                session.close();
            }
        }
    }
}
