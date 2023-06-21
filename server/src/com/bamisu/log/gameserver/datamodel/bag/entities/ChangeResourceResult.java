package com.bamisu.log.gameserver.datamodel.bag.entities;

import com.bamisu.log.gameserver.entities.ResType;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by Popeye on 11:41 AM, 1/11/2020
 */

/**
 * neu success thi ko can quan tam den notEnoughList, neu ko success thi notEnoughList la nhung loai tien ko du
 */
public class ChangeResourceResult implements IChangeItemResult{
    private boolean success = true;
    private List<ResType> notEnoughList = new ArrayList<>();

    public ChangeResourceResult() {
    }

    public ChangeResourceResult(boolean result, List<ResType> notEnoughList) {
        this.success = result;
        this.notEnoughList = notEnoughList;
    }

    @Override
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<ResType> getNotEnoughList() {
        return notEnoughList;
    }

    public void setNotEnoughList(List<ResType> notEnoughList) {
        this.notEnoughList = notEnoughList;
    }
}
