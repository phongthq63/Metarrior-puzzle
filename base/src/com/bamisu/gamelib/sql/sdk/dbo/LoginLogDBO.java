package com.bamisu.gamelib.sql.sdk.dbo;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Create by Popeye on 10:48 AM, 10/3/2020
 */
@Entity
@Table(name = "loginlog")
public class LoginLogDBO implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public String id;

    public String account;

    @Column(name = "login_time")
    public int loginTime;

    public int server;

    //số lần đã login liên tục
    public int count;

    public String ip;

    public int os;

    public String did;

    public LoginLogDBO() {
    }

    public LoginLogDBO(String account, int loginTime, int server, int count, String ip, int os, String did) {
        this.account = account;
        this.loginTime = loginTime;
        this.server = server;
        this.count = count;
        this.ip = ip;
        this.os = os;
        this.did = did;
    }
}
