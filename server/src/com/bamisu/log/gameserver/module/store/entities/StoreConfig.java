package com.bamisu.log.gameserver.module.store.entities;

import java.util.List;

public class StoreConfig {
    public List<StoreVO> listStore;
    public List<ItemVO> listItem;

    public StoreConfig(List<StoreVO> listStore, List<ItemVO> listItem) {
        this.listStore = listStore;
        this.listItem = listItem;
    }

    public StoreConfig() {
    }
}
