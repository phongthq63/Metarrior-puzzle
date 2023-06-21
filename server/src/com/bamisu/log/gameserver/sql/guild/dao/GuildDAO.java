package com.bamisu.log.gameserver.sql.guild.dao;

import com.bamisu.gamelib.base.BaseExtension;
import com.bamisu.log.gameserver.module.guild.GuildManager;
import com.bamisu.gamelib.sql.game.dbo.GuildDBO;
import com.bamisu.gamelib.base.db.SQLController;
import com.bamisu.gamelib.task.LizThreadManager;
import com.smartfoxserver.v2.entities.Zone;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.context.internal.ManagedSessionContext;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class GuildDAO {

    /**
     * Thread create sql manager
     *
     * @param zone
     */
    public static void startThreadSaveGuild(Zone zone) {
        //Update sql
        updateStatusGuild(zone);

        LizThreadManager.getInstance().getFixExecutorServiceByName(LizThreadManager.EThreadPool.SQL_QUERY, 1).schedule(() -> startThreadSaveGuild(zone), GuildManager.getInstance().getTimeThreadUpdateDb(), TimeUnit.SECONDS);
    }

    @WithSpan
    private static void updateStatusGuild(Zone zone) {
        List<GuildDBO> cache = GuildManager.getInstance().getGuildCacheSaveModel(zone);
        if(cache.isEmpty()){
            return;
        }
        List<GuildDBO> copy = new ArrayList<>(cache);
        GuildManager.getInstance().clearGuildDBOcache(zone);

        SQLController sqlController = ((BaseExtension) zone.getExtension()).getSQLController();
        Session session = null;
        try {
            session = sqlController.getSessionFactory().openSession();
            ManagedSessionContext.bind(session);
            session.beginTransaction();
            //logic
            for (GuildDBO dboStatus : copy) {
                session.saveOrUpdate(dboStatus);
            }
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

    @WithSpan
    public static List<String> searchGuild(Zone zone, String name) {
        List<String> stringList = new ArrayList<>();

        SQLController sqlController = ((BaseExtension) zone.getExtension()).getSQLController();
        Session session = null;
        try {
            session = sqlController.getSessionFactory().openSession();
            ManagedSessionContext.bind(session);
            session.beginTransaction();
            //logic
            String sql = "select * from guild where name like '%" + name + "%' and status = 0 limit 50";
            SQLQuery sqlQuery = session.createSQLQuery(sql).addEntity(GuildDBO.class);

            List<GuildDBO> result = sqlQuery.list();
            stringList = result.parallelStream().map(obj -> obj.id).collect(Collectors.toList());
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
        return stringList;
    }
}
