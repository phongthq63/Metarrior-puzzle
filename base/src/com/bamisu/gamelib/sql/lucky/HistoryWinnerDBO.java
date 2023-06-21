package com.bamisu.gamelib.sql.lucky;


import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "lucky_winer_history")
public class HistoryWinnerDBO {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;
    @Column(name = "user_id")
    public Long userId;
    @Column(name = "is_win")
    public String is_win;
    @Column(name = "price_amt")
    public int price_amt;
    @CreationTimestamp
    @Temporal(TemporalType.DATE)
    @Column(name = "create_date")
    public Date create_date;
    @Column(name = "total_price")
    public int total_price;
    @Column(name = "type")
    public String type;
    @Column(name = "buy_id")
    public Long buy_id;
}
