package com.bamisu.gamelib.sql.game.dbo;

import com.bamisu.gamelib.utils.Utils;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "ccu")
public class CcuDBO implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;

    @Column(name = "count")
    public int count;

    @Column(name = "time")
    public int timeStamp;

    public static CcuDBO create(int count) {
        CcuDBO ccuDBO = new CcuDBO();
        ccuDBO.count = count;
        ccuDBO.timeStamp = Utils.getTimestampInSecond();

        return ccuDBO;
    }
}
