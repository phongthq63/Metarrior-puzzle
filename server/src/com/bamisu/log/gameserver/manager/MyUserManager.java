package com.bamisu.log.gameserver.manager;

public class MyUserManager {
    private static MyUserManager ourInstance = new MyUserManager();

    public static MyUserManager getInstance() {
        return ourInstance;
    }

    private MyUserManager() {
    }
}
