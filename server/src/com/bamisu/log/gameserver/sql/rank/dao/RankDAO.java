package com.bamisu.log.gameserver.sql.rank.dao;

import com.bamisu.gamelib.base.BaseExtension;
import com.bamisu.gamelib.base.db.SQLController;
import com.bamisu.gamelib.sql.game.dbo.*;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.ZoneExtension;
import com.smartfoxserver.v2.entities.Zone;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.context.internal.ManagedSessionContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Quach Thanh Phong
 * On 5/28/2022 - 9:56 PM
 */
public class RankDAO {

    @WithSpan
    public static ConfigRankLeagueDBO getConfigRankLeagueDBO(Zone zone) {
        ConfigRankLeagueDBO configRankLeagueDBO = null;

        SQLController sqlController = ((BaseExtension) zone.getExtension()).getSQLController();
        Session session = null;
        try {
            session = sqlController.getSessionFactory().openSession();
            ManagedSessionContext.bind(session);
            session.beginTransaction();

            //logic
            configRankLeagueDBO = (ConfigRankLeagueDBO) session.get(ConfigRankLeagueDBO.class, 1);

            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ManagedSessionContext.unbind(sqlController.getSessionFactory());
            if (session != null) {
                session.close();
            }
        }

        return configRankLeagueDBO;
    }

    @WithSpan
    public static RankLeagueDBO getRankLeague(Zone zone, long uid) {
        RankLeagueDBO rankLeagueDBO = null;

        SQLController sqlController = ((BaseExtension) zone.getExtension()).getSQLController();
        Session session = null;
        try {
            session = sqlController.getSessionFactory().openSession();
            ManagedSessionContext.bind(session);
            session.beginTransaction();

            //logic
            ConfigRankLeagueDBO configRankLeagueDBO = (ConfigRankLeagueDBO) session.get(ConfigRankLeagueDBO.class, 1);
            int season = configRankLeagueDBO == null ? 0 : configRankLeagueDBO.season;
            String sql = "SELECT * FROM t_rank_league WHERE uid = " + uid + " and season = " + season;
            SQLQuery sqlQuery = session.createSQLQuery(sql).addEntity(RankLeagueDBO.class);
            List<RankLeagueDBO> rankLeagueDBOS = sqlQuery.list();

            ((ZoneExtension) zone.getExtension()).trace("RankDAO - getRankLeague - " + uid + " " + season +" | " + Utils.toJson(rankLeagueDBOS));

            if (!rankLeagueDBOS.isEmpty()) {
                rankLeagueDBO = rankLeagueDBOS.get(0);
            }

            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ManagedSessionContext.unbind(sqlController.getSessionFactory());
            if (session != null) {
                session.close();
            }
        }

        return rankLeagueDBO;
    }

    @WithSpan
    public static RankLeagueDBO getRankLeagueByLeagueId(Zone zone, long leagueId) {
        RankLeagueDBO rankLeagueDBO = null;

        SQLController sqlController = ((BaseExtension) zone.getExtension()).getSQLController();
        Session session = null;
        try {
            session = sqlController.getSessionFactory().openSession();
            ManagedSessionContext.bind(session);
            session.beginTransaction();

            //logic
            ConfigRankLeagueDBO configRankLeagueDBO = (ConfigRankLeagueDBO) session.get(ConfigRankLeagueDBO.class, 1);
            int season = configRankLeagueDBO == null ? 0 : configRankLeagueDBO.season;
            String sql = "SELECT * FROM t_rank_league WHERE league_id = " + leagueId + " and season = " + season;
            SQLQuery sqlQuery = session.createSQLQuery(sql).addEntity(RankLeagueDBO.class);
            List<RankLeagueDBO> rankLeagueDBOS = sqlQuery.list();
            if (!rankLeagueDBOS.isEmpty()) {
                rankLeagueDBO = rankLeagueDBOS.get(0);
            }

            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ManagedSessionContext.unbind(sqlController.getSessionFactory());
            if (session != null) {
                session.close();
            }
        }

        return rankLeagueDBO;
    }

    @WithSpan
    public static RankLeagueDBO updateRankLeagueByType(Zone zone, long uid, int type) {
        RankLeagueDBO rankLeagueDBO = null;

        SQLController sqlController = ((BaseExtension) zone.getExtension()).getSQLController();
        Session session = null;
        try {
            session = sqlController.getSessionFactory().openSession();
            ManagedSessionContext.bind(session);
            session.beginTransaction();

            //logic
            ConfigRankLeagueDBO configRankLeagueDBO = (ConfigRankLeagueDBO) session.get(ConfigRankLeagueDBO.class, 1);
            int season = configRankLeagueDBO == null ? 0 : configRankLeagueDBO.season;

            RankLeagueDBO matchLeague;
            int totalUserInLeague = 100;
            String sql = "select * from t_rank_league where league_id = " +
                    "(select league_id from " +
                    "(SELECT count(*) as count, t_rank_league.league_id, t_rank_league.type FROM t_rank_league " +
                    "where season = " + season + " group by league_id) gt where count < " + totalUserInLeague + " and " +
                    "type <= " + type + " limit 1) and season = " + season;
            SQLQuery sqlQuery = session.createSQLQuery(sql).addEntity(RankLeagueDBO.class);
            List<RankLeagueDBO> rankLeagueDBOS = sqlQuery.list();

            ((ZoneExtension) zone.getExtension()).trace("RankDAO - updateRankLeagueByType - " + uid + " " + type + " " + season +" | " + Utils.toJson(rankLeagueDBOS));

            if (!rankLeagueDBOS.isEmpty()) {
                matchLeague = rankLeagueDBOS.get(0);

                sql = "SELECT * FROM t_rank_league WHERE uid = " + uid + " and season = " + season;
                sqlQuery = session.createSQLQuery(sql).addEntity(RankLeagueDBO.class);
                List<RankLeagueDBO> userRankLeagueDBOS = sqlQuery.list();
                rankLeagueDBO = (RankLeagueDBO) session.get(RankLeagueDBO.class, userRankLeagueDBOS.get(0).id);

                rankLeagueDBO.name = matchLeague.name;
                rankLeagueDBO.leagueId = matchLeague.leagueId;
                rankLeagueDBO.type = matchLeague.type;
                session.saveOrUpdate(rankLeagueDBO);
            }

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

        return rankLeagueDBO;
    }

    @WithSpan
    public static RankLeagueDBO addRankLeague(Zone zone, long uid) {
        RankLeagueDBO rankLeagueDBO = null;

        SQLController sqlController = ((BaseExtension) zone.getExtension()).getSQLController();
        Session session = null;
        try {
            session = sqlController.getSessionFactory().openSession();
            ManagedSessionContext.bind(session);
            session.beginTransaction();

            //logic
            ConfigRankLeagueDBO configRankLeagueDBO = (ConfigRankLeagueDBO) session.get(ConfigRankLeagueDBO.class, 1);
            int season = configRankLeagueDBO == null ? 0 : configRankLeagueDBO.season;
            String sql = "SELECT * FROM t_rank_league WHERE uid = " + uid + " and season = " + season;
            SQLQuery sqlQuery = session.createSQLQuery(sql).addEntity(RankLeagueDBO.class);
            List<RankLeagueDBO> rankLeagueDBOS = sqlQuery.list();

            if (!rankLeagueDBOS.isEmpty()) {
                rankLeagueDBO = rankLeagueDBOS.get(0);
            } else {
                //Add to league
                sql = "select * from t_rank_league where type = 0 and season = " + season + " order by league_id desc limit 1";
                sqlQuery = session.createSQLQuery(sql).addEntity(RankLeagueDBO.class);
                rankLeagueDBOS = sqlQuery.list();
                int leagueId = 1;
                int countGroupLeague = 1;
                if (!rankLeagueDBOS.isEmpty()) {
                    sql = "SELECT count(*) as count, league_id FROM t_rank_league where type = 0 and league_id = (select league_id from t_rank_league where season = " + season + " order by league_id desc limit 1) and season = " + season;
                    sqlQuery = session.createSQLQuery(sql).addEntity(LeagueCountDBO.class);
                    List<LeagueCountDBO> result = sqlQuery.list();
                    int maxPlayerPerLeague = 100;
                    if (!result.isEmpty() && result.get(0) != null && result.get(0).count != null && result.get(0).leagueId != null) {
                        if (result.get(0).count >= maxPlayerPerLeague) {
                            leagueId = result.get(0).leagueId + 1;
                        } else {
                            leagueId = result.get(0).leagueId;
                        }
                    }
                    if (rankLeagueDBOS.get(0) != null && rankLeagueDBOS.get(0).name != null) {
                        countGroupLeague = Integer.parseInt(rankLeagueDBOS.get(0).name.split("-")[1].trim());
                    }
                }

                rankLeagueDBO = new RankLeagueDBO();
                rankLeagueDBO.uid = uid;
                rankLeagueDBO.name = "Iron - " + countGroupLeague;
                rankLeagueDBO.leagueId = leagueId;
                rankLeagueDBO.type = 0;
                rankLeagueDBO.tierPoint = 0;
                rankLeagueDBO.season = season;
                session.saveOrUpdate(rankLeagueDBO);
            }

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

        return rankLeagueDBO;
    }

    @WithSpan
    public static List<RankMissionDBO> getListRankMission(Zone zone, int page, int size) {
        List<RankMissionDBO> rankMissionDBOS = new ArrayList<>();

        SQLController sqlController = ((BaseExtension) zone.getExtension()).getSQLController();
        Session session = null;
        try {
            session = sqlController.getSessionFactory().openSession();
            ManagedSessionContext.bind(session);
            session.beginTransaction();

            //logic
            String sql = "select t_rank_mission.* from t_rank_mission, t_config_rank_league where t_rank_mission.season = t_config_rank_league.season order by score desc, update_time asc limit " + page * size + ", " + size;
            SQLQuery sqlQuery = session.createSQLQuery(sql).addEntity(RankMissionDBO.class);
            rankMissionDBOS = sqlQuery.list();

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

        return rankMissionDBOS;
    }

    @WithSpan
    public static UserRankMissionDBO getUserRankMission(Zone zone, long uid) {
        UserRankMissionDBO userRankMissionDBO = null;

        SQLController sqlController = ((BaseExtension) zone.getExtension()).getSQLController();
        Session session = null;
        try {
            session = sqlController.getSessionFactory().openSession();
            ManagedSessionContext.bind(session);
            session.beginTransaction();

            //logic
            ConfigRankLeagueDBO configRankLeagueDBO = (ConfigRankLeagueDBO) session.get(ConfigRankLeagueDBO.class, 1);
            String sql = "select leaderboard.* from (select *, RANK() OVER (ORDER BY score desc) as rank from t_rank_mission where season = " + configRankLeagueDBO.season + ") leaderboard where uid = " + uid;
            SQLQuery sqlQuery = session.createSQLQuery(sql).addEntity(UserRankMissionDBO.class);
            List<UserRankMissionDBO> userRankMissionDBOS = sqlQuery.list();
            if (!userRankMissionDBOS.isEmpty()) {
                userRankMissionDBO = userRankMissionDBOS.get(0);
            }

            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ManagedSessionContext.unbind(sqlController.getSessionFactory());
            if (session != null) {
                session.close();
            }
        }

        return userRankMissionDBO;
    }

    @WithSpan
    public static boolean updateRankMission(Zone zone, long uid, int score) {
        boolean result = false;

        SQLController sqlController = ((BaseExtension) zone.getExtension()).getSQLController();
        Session session = null;
        try {
            session = sqlController.getSessionFactory().openSession();
            ManagedSessionContext.bind(session);
            session.beginTransaction();

            //logic
            ConfigRankLeagueDBO configRankLeagueDBO = (ConfigRankLeagueDBO) session.get(ConfigRankLeagueDBO.class, 1);
            String sql = "select * from t_rank_mission where uid = " + uid + " and season = " + configRankLeagueDBO.season;
            SQLQuery sqlQuery = session.createSQLQuery(sql).addEntity(RankMissionDBO.class);
            List<RankMissionDBO> rankMissionDBOS = sqlQuery.list();
            RankMissionDBO rankMissionDBO;
            if (rankMissionDBOS.isEmpty()) {
                rankMissionDBO = new RankMissionDBO();
                rankMissionDBO.uid = uid;
                rankMissionDBO.season = configRankLeagueDBO.season;
                rankMissionDBO.score = 0;
            } else {
                rankMissionDBO = rankMissionDBOS.get(0);
            }
            rankMissionDBO.score += score;
            rankMissionDBO.updateTime = Utils.getTimestampInSecond();
            session.saveOrUpdate(rankMissionDBO);

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
