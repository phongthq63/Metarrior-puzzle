package com.bamisu.gamelib.sql.lucky;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;


@Entity
@Table(name = "lucky_buy_history")
public class BuyLuckyUserDBO {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;
    @Column(name = "user_id")
    public Long userId;
    @Column(name = "no1")
    public int no1;
    @Column(name = "no2")
    public int no2;
    @Column(name = "no3")
    public int no3;
    @Column(name = "time")
    public int time;
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "buy_date")
    public Date buyDate;
    @Column(name = "amount")
    public int amount;
    @Column(name = "type")
    public String type;
    @Column(name = "is_rev_reward")
    public String isRevReward;
}
