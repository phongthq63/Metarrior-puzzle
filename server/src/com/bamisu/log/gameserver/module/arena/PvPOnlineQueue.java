package com.bamisu.log.gameserver.module.arena;

import com.bamisu.gamelib.task.LizThreadManager;
import com.bamisu.log.gameserver.module.arena.config.PvPOnlineConfig;
import com.bamisu.log.gameserver.module.arena.config.entities.BetVO;
import com.bamisu.log.gameserver.module.arena.exception.AlreadyOnQueeException;
import com.bamisu.log.gameserver.module.arena.exception.BetNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Create by Popeye on 4:09 PM, 4/23/2021
 */
public class PvPOnlineQueue {
    private Map<Short, List<Long>> queue;
    private ScheduledExecutorService executorService = LizThreadManager.getInstance().getFixExecutorServiceByName("pvponline", 1);
    private ArenaManager arenaManager;
    private ArenaHandler arenaHandler;

    public PvPOnlineQueue(ArenaManager arenaManager, ArenaHandler arenaHandler) {
        this.arenaManager = arenaManager;
        this.arenaHandler = arenaHandler;
        initQueue(arenaManager.getPvPOnlineConfig());
        executorService.schedule(()->checkQueueLoop(), 2, TimeUnit.SECONDS);
    }

    private void checkQueueLoop() {
        //check queue
        try {
            synchronized (queue){
                List<Long> removeList = new ArrayList<>();
                for(short bet : queue.keySet()){
                    List<Long> listUID = queue.get(bet);
                    removeList.clear();
                    long uid1 = -1;
                    long uid2 = -1;
                    for(Long uid : listUID){
                        if(uid == -1) {
                            uid1 = uid;
                            continue;
                        }
                        if(uid2 == -1){
                            uid2 = uid;
                            removeList.add(uid1);
                            removeList.add(uid2);
                            arenaManager.createPvPOnlineMatch(arenaHandler, bet, uid1, uid2);
                            uid1 = -1;
                            uid2 = -1;
                        }
                    }
                    if(!removeList.isEmpty()){
                        listUID.removeAll(removeList);
                    }
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            executorService.schedule(()->checkQueueLoop(), 2, TimeUnit.SECONDS);
        }
    }

    private void initQueue(PvPOnlineConfig pvPOnlineConfig) {
        queue = new HashMap<>();
        for (BetVO betVO : pvPOnlineConfig.bets) {
            queue.put(betVO.id, new ArrayList<>());
        }
    }

    public void join(short betID, long uid) throws BetNotFoundException, AlreadyOnQueeException {
        synchronized (queue){
            if (queue.containsKey(betID)) {
                if (!queue.get(betID).contains(uid)) {
                    queue.get(betID).add(uid);
                }else {
                    throw new AlreadyOnQueeException();
                }
            } else {
                throw new BetNotFoundException();
            }
        }
    }
}
