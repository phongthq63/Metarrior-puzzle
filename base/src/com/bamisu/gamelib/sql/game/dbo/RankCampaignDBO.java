package com.bamisu.gamelib.sql.game.dbo;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Calendar;

/**
 * Created by Quach Thanh Phong
 * On 5/29/2022 - 12:38 AM
 */
@Entity
@Table(name = "t_rank_campaign")
public class RankCampaignDBO {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    public Long uid;

    public Integer season;

    @Column(name = "league_id")
    public Integer leagueId;

    @Column(name = "score")
    public Integer score;

    @Column(name = "update_time")
    public int updateTime;
}
