package com.bamisu.gamelib.sql.sdk.dbo;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "giftcode")
public class GiftcodeDBO {
    @Id
    @GeneratedValue
    public int id;

    @Column
    public long user_id;

    @Column
    public String code;

    @Column
    public Timestamp used_at;

    @Column
    public Timestamp expired_at;

    public GiftcodeDBO() {

    }

    public GiftcodeDBO(String code, long expiredAt) {
        this.code = code;
        this.expired_at = new Timestamp(expiredAt * 1000);
    }
}
