package com.bamisu.gamelib.sql.hunt;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "hunt")
public class HuntRewardDBO {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @Column(name = "user_id")
    public Long userId;

    @Column(name = "type")
    public String type;

    @Column(name = "amount")
    public int amount;

    @Column(name = "date")
    public Date date;
}
