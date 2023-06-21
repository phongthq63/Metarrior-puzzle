package com.bamisu.gamelib.sql.sdk.dbo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "account")
public class AccountDBO implements Serializable {
    @Id
    @Column(name = "id")
    public String id;

    @Column(name = "referral_code")
    public String inviteCode;

    @Column(name = "create_time")
    public int timeStamp;

    public String presenter;    //referral code của người giới thiệu

    public byte linked;    //0 chưa, 1 rồi

    public byte level50;    //0 chưa, 1 rồi

    public static AccountDBO create(String id, String inviteCode, int timestampInSecond) {
        AccountDBO accountDBO = new AccountDBO();
        accountDBO.id = id;
        accountDBO.inviteCode = inviteCode;
        accountDBO.timeStamp = timestampInSecond;
        accountDBO.presenter = "";
        return accountDBO;
    }
}
