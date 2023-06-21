package com.bamisu.gamelib.entities;

import java.util.Collection;

/**
 * Create by Popeye on 11:45 AM, 7/14/2020
 */
public class VipDataToSend {
    public Collection<VipData> vipDataCollection;

    public VipDataToSend() {
    }

    public VipDataToSend(Collection<VipData> vipData) {
        this.vipDataCollection = vipData;
    }
}
