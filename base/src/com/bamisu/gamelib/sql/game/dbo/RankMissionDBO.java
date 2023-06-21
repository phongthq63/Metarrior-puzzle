package com.bamisu.gamelib.sql.game.dbo;

import javax.persistence.*;

/**
 * Created by Quach Thanh Phong
 * On 11/22/2022 - 2:14 AM
 */
@Entity
@Table(name = "t_rank_mission")
public class RankMissionDBO {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    public Long uid;

    public Integer season;

    public Integer score;

    @Column(name = "update_time")
    public int updateTime;
}
