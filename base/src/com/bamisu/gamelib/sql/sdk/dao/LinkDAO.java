package com.bamisu.gamelib.sql.sdk.dao;

import com.bamisu.gamelib.base.db.SQLController;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.sql.sdk.dbo.LinkDBO;
import com.bamisu.gamelib.task.LizThreadManager;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;
import org.hibernate.Session;
import org.hibernate.context.internal.ManagedSessionContext;

import java.util.Iterator;
import java.util.concurrent.ExecutorService;

/**
 * Create by Popeye on 4:21 PM, 10/2/2020
 */
public class LinkDAO {
    private static ExecutorService executorService = LizThreadManager.getInstance().getExecutorServiceByName("login");

    public static void save(SQLController sqlController, LinkDBO linkDBO) {
        executorService.submit(() -> {
            Session session = null;
            try {
                session = sqlController.getSessionFactory().openSession();
                ManagedSessionContext.bind(session);
                session.beginTransaction();
                //logic
                session.saveOrUpdate(linkDBO);
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

    /**
     * thong ke so tai khoan da lien ket xa hoi theo tháng
     */
    public static ISFSArray countLinkedOnMonth(SQLController sqlController, String month){
        ISFSArray sfsArray = new SFSArray();
        ISFSObject sfsObject = null;
        Session session = null;
        try {
            session = sqlController.getSessionFactory().openSession();
            ManagedSessionContext.bind(session);
            session.beginTransaction();
            //logic
            Iterator inIterator = session.createSQLQuery("" +
                    "SELECT count(*) as count, from_unixtime((create_time),\"%Y-%m-%d\") as strTime FROM link " +
                    "where from_unixtime((create_time),\"%Y-%m\") like :month " +
                    "group by strTime"
            ).setString("month", month).list().iterator();
            while (inIterator.hasNext()){
                Object[] tuple = (Object[]) inIterator.next();
                sfsObject = new SFSObject();
                sfsObject.putInt(Params.COUNT, Integer.parseInt(tuple[0].toString()));
                sfsObject.putUtfString(Params.DATE, tuple[1].toString());
                sfsArray.addSFSObject(sfsObject);
            }
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

        return sfsArray;
    }

    /**
     * thong ke so tai khoan da lien ket xa hoi theo năm
     */
    public static ISFSArray countLinkedOnYear(SQLController sqlController, String year){
        ISFSArray sfsArray = new SFSArray();
        ISFSObject sfsObject = null;
        Session session = null;
        try {
            session = sqlController.getSessionFactory().openSession();
            ManagedSessionContext.bind(session);
            session.beginTransaction();
            //logic
            Iterator inIterator = session.createSQLQuery("" +
                    "SELECT count(*) as count, from_unixtime((create_time),\"%Y-%m\") as strTime FROM link " +
                    "where from_unixtime((create_time),\"%Y\") like :year " +
                    "group by strTime"
            ).setString("year", year).list().iterator();
            while (inIterator.hasNext()){
                Object[] tuple = (Object[]) inIterator.next();
                sfsObject = new SFSObject();
                sfsObject.putInt(Params.COUNT, Integer.parseInt(tuple[0].toString()));
                sfsObject.putUtfString(Params.MONTH, tuple[1].toString());
                sfsArray.addSFSObject(sfsObject);
            }
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

        return sfsArray;
    }

    public static int countLinkedAllTime(SQLController sqlController) {
        int count = 0;
        Session session = null;
        try {
            session = sqlController.getSessionFactory().openSession();
            ManagedSessionContext.bind(session);
            session.beginTransaction();
            //logic
            count = Integer.parseInt(String.valueOf(session.createSQLQuery("SELECT count(*) FROM link "
            ).uniqueResult()));
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

        return count;
    }
}
