package com.bamisu.gamelib.sql.luckydraw;

import javax.persistence.*;

@Entity
@Table(name = "t_rank_endlessnight_user")
public class HistoryRankEndlessnightDBO {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;
    @Column(name = "uid")
    public Long uid;
    @Column(name = "season")
    public int season;
    @Column(name = "league_id")
    public int league_id;
    @Column(name = "score")
    public int score;
    @Column(name = "update_time")
    public int update_time;
}
