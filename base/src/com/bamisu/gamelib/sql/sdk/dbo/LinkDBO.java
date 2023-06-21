package com.bamisu.gamelib.sql.sdk.dbo;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "link")
public class LinkDBO implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public String id;

    public String account;
    public String social;

    @Column(name = "social_key")
    public String socialKey;

    @Column(name = "create_time")
    public int createTime;

    public LinkDBO() {
    }

    public LinkDBO(String account, String social, String socialKey, int createTime) {
        this.account = account;
        this.social = social;
        this.socialKey = socialKey;
        this.createTime = createTime;
    }
}
