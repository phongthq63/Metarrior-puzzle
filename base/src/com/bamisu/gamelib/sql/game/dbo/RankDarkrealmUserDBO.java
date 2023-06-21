package com.bamisu.gamelib.sql.game.dbo;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created by Quach Thanh Phong
 * On 6/17/2022 - 12:38 AM
 */
@Entity
@Table(name = "t_rank_darkrealm_user")
public class RankDarkrealmUserDBO implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    public Long uid;

    public Integer season;

    @Column(name = "league_id")
    public Integer leagueId;

    @Column(name = "score")
    public Long score;

    @Column(name = "update_time")
    public Integer updateTime;
}
