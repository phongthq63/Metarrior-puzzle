package com.bamisu.log.gameserver.datamodel.bag.entities;

import com.bamisu.gamelib.item.define.SpecialItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by Popeye on 2:44 PM, 1/13/2020
 */
public class ChangeSpecialItemResult implements IChangeItemResult {
    private boolean success = true;
    private List<SpecialItem> notEnoughList = new ArrayList<>();

    public ChangeSpecialItemResult() {
    }

    public ChangeSpecialItemResult(boolean result, List<SpecialItem> notEnoughList) {
        this.success = result;
        this.notEnoughList = notEnoughList;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<SpecialItem> getNotEnoughList() {
        return notEnoughList;
    }

    public void setNotEnoughList(List<SpecialItem> notEnoughList) {
        this.notEnoughList = notEnoughList;
    }
}
