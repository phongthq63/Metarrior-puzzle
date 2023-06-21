package com.bamisu.gamelib.task;

import com.bamisu.gamelib.base.BaseExtension;
import com.bamisu.gamelib.base.db.SQLController;
import com.bamisu.gamelib.base.shutdown.IShutdownListener;
import com.bamisu.gamelib.base.shutdown.ShutdownManager;
import com.bamisu.gamelib.sql.game.dbo.TransactionDBO;
import com.bamisu.gamelib.sql.game.dbo.UserTokenDBO;
import com.smartfoxserver.v2.SmartFoxServer;
import com.smartfoxserver.v2.entities.Zone;
import org.hibernate.Session;
import org.hibernate.context.internal.ManagedSessionContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TransactionTask extends BaseTask implements IShutdownListener {
    private static final TransactionTask instance = new TransactionTask();
    public static TransactionTask getInstance() {
        return instance;
    }

    private final List<TransactionDBO> transactions;
    private final Map<Long, UserTokenDBO> userTokens;

    private TransactionTask() {
        super();
        this.transactions = new ArrayList<>();
        this.userTokens = new HashMap<>();
        this.startWriteLog();
        ShutdownManager.getInstance().register(this);
    }

    private void startWriteLog() {
        this.scheduledFuture = this.SCHEDULER.scheduleAtFixedRate(this::writeLog, 10, 10, TimeUnit.SECONDS);
        this.scheduledFuture = this.SCHEDULER.scheduleAtFixedRate(this::writeLogToken, 10, 10, TimeUnit.SECONDS);
    }

    private synchronized void writeLogToken() {
        if (this.userTokens.size() == 0) {
            return;
        }

        List<Zone> zones = SmartFoxServer.getInstance().getZoneManager().getZoneList();
        if (zones.size() == 0) {
            return;
        }

        Zone zone = zones.get(0);
        Map<Long, UserTokenDBO> map = new HashMap<>(this.userTokens);
        this.userTokens.clear();
        SQLController sqlController = ((BaseExtension) zone.getExtension()).getSQLController();
        Session session = null;
        try {
            session = sqlController.getSessionFactory().openSession();
            ManagedSessionContext.bind(session);
            session.beginTransaction();
            for (Map.Entry<Long, UserTokenDBO> entry : map.entrySet()) {
                session.saveOrUpdate(entry.getValue());
            }

            session.getTransaction().commit();
        } catch (Exception e){
            this.logger.info("TransactionTask update token error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            ManagedSessionContext.unbind(sqlController.getSessionFactory());
            if (session != null) {
                session.close();
            }
        }
    }

    private synchronized void writeLog() {
        if (this.transactions.size() > 0) {
            List<Zone> zones = SmartFoxServer.getInstance().getZoneManager().getZoneList();
            if (zones.size() == 0) {
                return;
            }

            Zone zone = zones.get(0);
            List<TransactionDBO> lst = new ArrayList<>(this.transactions);
            this.transactions.clear();
            SQLController sqlController = ((BaseExtension) zone.getExtension()).getSQLController();
            Session session = null;
            try {
                session = sqlController.getSessionFactory().openSession();
                ManagedSessionContext.bind(session);
                session.beginTransaction();
                for (TransactionDBO transactionDBO : lst) {
                    session.save(transactionDBO);
                }

                session.getTransaction().commit();
            } catch (Exception e){
                this.logger.info("TransactionTask error: " + e.getMessage());
                e.printStackTrace();
            } finally {
                ManagedSessionContext.unbind(sqlController.getSessionFactory());
                if (session != null) {
                    session.close();
                }
            }
        }
    }

    @Override
    public void onShutdown() {
        this.writeLog();
        this.writeLogToken();
    }

    public void add(TransactionDBO dbo) {
        this.transactions.add(dbo);
    }

    public void add(UserTokenDBO dbo) {
        this.userTokens.put(dbo.userId, dbo);
    }
}
