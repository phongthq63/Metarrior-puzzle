package com.bamisu.gamelib.sql.luckydraw;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "lucky_draw")
public class HistoryLuckyDrawDBO {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;
    @Column(name = "user_id")
    public Long userId;
    @Column(name = "type")
    public String type;
    @Column(name = "amount")
    public double amount;
//    @CreationTimestamp
//    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date")
    public Date date;
    @Column(name = "season")
    public int season;
    @Column(name = "top_user")
    public String top_user;
}
