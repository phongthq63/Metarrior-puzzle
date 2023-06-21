package com.bamisu.log.gameserver.datamodel.arena.entities;

public class UserArenaInfo {
    public long rank = -1;
    public long uid;
    public int point;
    public int timeStamp;

    public static UserArenaInfo create(long uid, int point){
        UserArenaInfo infoRankUser = new UserArenaInfo();
        infoRankUser.uid = uid;
        infoRankUser.point = point;

        return infoRankUser;
    }

    public static UserArenaInfo create(long rank, long uid, int point){
        UserArenaInfo infoRankUser = new UserArenaInfo();
        infoRankUser.rank = rank;
        infoRankUser.uid = uid;
        infoRankUser.point = point;

        return infoRankUser;
    }

    public static UserArenaInfo create(long rank, long uid, int point, int timeStamp){
        UserArenaInfo infoRankUser = new UserArenaInfo();
        infoRankUser.rank = rank;
        infoRankUser.uid = uid;
        infoRankUser.point = point;
        infoRankUser.timeStamp = timeStamp;

        return infoRankUser;
    }

    public static UserArenaInfo create(UserArenaInfo data){
        UserArenaInfo infoRankUser = new UserArenaInfo();
        infoRankUser.rank = data.rank;
        infoRankUser.uid = data.uid;
        infoRankUser.point = data.point;
        infoRankUser.timeStamp = data.timeStamp;

        return infoRankUser;
    }
}
