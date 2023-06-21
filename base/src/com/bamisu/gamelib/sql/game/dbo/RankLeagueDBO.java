package com.bamisu.gamelib.sql.game.dbo;

import javax.persistence.*;

/**
 * Created by Quach Thanh Phong
 * On 5/29/2022 - 12:38 AM
 */
@Entity
@Table(name = "t_rank_league")
public class RankLeagueDBO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "uid")
    public Long uid;

    @Column(name = "name")
    public String name;

    @Column(name = "league_id")
    public Integer leagueId;

    @Column(name = "tier_point")
    public Integer tierPoint;

    @Column(name = "type")
    public Integer type;

    @Column(name = "season")
    public Integer season;

}
