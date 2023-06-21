package com.bamisu.log.gameserver.datamodel.bag.entities;

import com.bamisu.gamelib.entities.MoneyType;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by Popeye on 11:41 AM, 1/11/2020
 */

/**
 * neu success thi ko can quan tam den notEnoughList, neu ko success thi notEnoughList la nhung loai tien ko du
 */
public class ChangeMoneyResult implements IChangeItemResult{
    private boolean success = true;
    private List<MoneyType> notEnoughList = new ArrayList<>();

    public ChangeMoneyResult() {
    }

    public ChangeMoneyResult(boolean result, List<MoneyType> notEnoughList) {
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

    public List<MoneyType> getNotEnoughList() {
        return notEnoughList;
    }

    public void setNotEnoughList(List<MoneyType> notEnoughList) {
        this.notEnoughList = notEnoughList;
    }
}
