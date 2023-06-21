package com.bamisu.gamelib.sql.game.dbo;

import com.bamisu.gamelib.utils.Utils;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "money_change")
public class MoneyChangeDBO implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;

    @Column(name = "uid")
    public long uid;

    @Column(name = "id_transection")
    public short idTransection;

    @Column(name = "money_before")
    public long before;

    @Column(name = "money_after")
    public long after;

    @Column(name = "time")
    public int timeStamp;

    public static MoneyChangeDBO create(long uid, short idTransection, long before, long after) {
        MoneyChangeDBO moneyChangeDBO = new MoneyChangeDBO();
        moneyChangeDBO.uid = uid;
        moneyChangeDBO.idTransection = idTransection;
        moneyChangeDBO.before = before;
        moneyChangeDBO.after = after;
        moneyChangeDBO.timeStamp = Utils.getTimestampInSecond();

        return moneyChangeDBO;
    }
}
