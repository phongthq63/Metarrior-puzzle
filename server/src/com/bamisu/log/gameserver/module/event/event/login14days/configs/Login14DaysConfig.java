package com.bamisu.log.gameserver.module.event.event.login14days.configs;

import com.bamisu.gamelib.entities.ResourcePackage;

import java.util.List;

public class Login14DaysConfig {
    private String id;
    private String name;
    private int session;
    private long startTime;
    private long endTime;
    private boolean isActive;
    private List<List<ResourcePackage>> gifts;

    public Login14DaysConfig() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSession() {
        return session;
    }

    public void setSession(int session) {
        this.session = session;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public List<List<ResourcePackage>> getGifts() {
        return gifts;
    }

    public void setGifts(List<List<ResourcePackage>> gifts) {
        this.gifts = gifts;
    }
}
