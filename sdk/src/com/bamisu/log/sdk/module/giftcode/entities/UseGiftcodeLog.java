package com.bamisu.log.sdk.module.giftcode.entities;

/**
 * Create by Popeye on 9:22 AM, 4/23/2020
 */
public class UseGiftcodeLog {
    public String acountID;
    public String uid;
    public String serverID;
    public String giftcode;
    public int time;

    public UseGiftcodeLog() {
    }

    public UseGiftcodeLog(String acountID) {
        this.acountID = acountID;
    }

    public UseGiftcodeLog(String acountID, String uid, String giftcode, int time) {
        this.acountID = acountID;
        this.uid = uid;
        this.giftcode = giftcode;
        this.time = time;
    }
}
