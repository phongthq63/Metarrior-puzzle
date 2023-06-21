package com.bamisu.gamelib.sql.sdk.dao;

import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.sql.sdk.dbo.AccountDBO;
import com.bamisu.gamelib.base.db.SQLController;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.context.internal.ManagedSessionContext;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AccountDAO {
    public static AccountDBO get(SQLController sqlController, String id){
        AccountDBO accountDBO = null;
        Session session = null;
        try {
            session = sqlController.getSessionFactory().openSession();
            ManagedSessionContext.bind(session);
            session.beginTransaction();
            accountDBO = (AccountDBO) session.createQuery("from AccountDBO where id=:id").setString("id", id).setMaxResults(1).uniqueResult();
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

        return accountDBO;
    }

    public static void create(SQLController sqlController, String id, String inviteCode, int timestampInSecond) {
        Session session = null;
        try {
            session = sqlController.getSessionFactory().openSession();
            ManagedSessionContext.bind(session);
            session.beginTransaction();
            //logic
            AccountDBO accountDBO = AccountDBO.create(id, inviteCode, timestampInSecond);
            session.saveOrUpdate(accountDBO);
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

    public static void save(SQLController sqlController, AccountDBO accountDBO) {
        Session session = null;
        try {
            session = sqlController.getSessionFactory().openSession();
            ManagedSessionContext.bind(session);
            session.beginTransaction();
            //logic
            session.saveOrUpdate(accountDBO);
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

    public static int countAccountByRefrralCode(SQLController sqlController, String refcode){
        int count = 0;
        Session session = null;
        try {
            session = sqlController.getSessionFactory().openSession();
            ManagedSessionContext.bind(session);
            session.beginTransaction();
            //logic
            count = Integer.parseInt(String.valueOf(session.createSQLQuery("" +
                    "SELECT count(*) from account " +
                    "where presenter like :refcode "
            ).setString("refcode", refcode).uniqueResult()));
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

    public static int countAccountLinkedByRefrralCode(SQLController sqlController, String refcode){
        int count = 0;
        Session session = null;
        try {
            session = sqlController.getSessionFactory().openSession();
            ManagedSessionContext.bind(session);
            session.beginTransaction();
            //logic
            count = Integer.parseInt(String.valueOf(session.createSQLQuery("" +
                    "SELECT count(*) FROM account " +
                    "WHERE presenter LIKE :refcode AND linked = 1"
            ).setString("refcode", refcode).uniqueResult()));
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


    public static int countAccount50ByRefrralCode(SQLController sqlController, String refcode){
        int count = 0;
        Session session = null;
        try {
            session = sqlController.getSessionFactory().openSession();
            ManagedSessionContext.bind(session);
            session.beginTransaction();
            //logic
            count = Integer.parseInt(String.valueOf(session.createSQLQuery("" +
                    "SELECT count(*) FROM account " +
                    "WHERE presenter LIKE :refcode AND level50 = 1"
            ).setString("refcode", refcode).uniqueResult()));
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


    public static boolean updateUserRefrralCode(SQLController sqlController, String refcodeOld, String refcodeNew){
        boolean haveSuccess = false;
        Session session = null;
        try {
            session = sqlController.getSessionFactory().openSession();
            ManagedSessionContext.bind(session);
            session.beginTransaction();
            //logic
            String sql = "SELECT * FROM account WHERE referral_code LIKE '" + refcodeOld + "'";
            SQLQuery sqlQuery = session.createSQLQuery(sql).addEntity(AccountDBO.class);
            List<AccountDBO> listResult = sqlQuery.list();
            if(listResult.isEmpty()) return false;
            //Update Refferal Code moi
            listResult.get(0).inviteCode = refcodeNew;
            session.update(listResult.get(0));
            //
            session.getTransaction().commit();
            haveSuccess = true;
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

        return haveSuccess;
    }
}
