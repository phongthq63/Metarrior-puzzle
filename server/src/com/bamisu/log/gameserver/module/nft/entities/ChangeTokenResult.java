package com.bamisu.log.gameserver.module.nft.entities;

import com.bamisu.log.gameserver.datamodel.bag.entities.IChangeItemResult;
import com.bamisu.log.gameserver.module.nft.defind.ETokenBC;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Quach Thanh Phong
 * On 3/8/2022 - 10:28 PM
 */
public class ChangeTokenResult implements IChangeItemResult {
    private boolean success = true;
    private List<ETokenBC> notEnoughList = new ArrayList<>();

    public ChangeTokenResult() {
    }

    public ChangeTokenResult(boolean result, List<ETokenBC> notEnoughList) {
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

    public List<ETokenBC> getNotEnoughList() {
        return notEnoughList;
    }

    public void setNotEnoughList(List<ETokenBC> notEnoughList) {
        this.notEnoughList = notEnoughList;
    }
}
