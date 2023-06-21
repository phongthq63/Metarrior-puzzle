package com.bamisu.gamelib.sql.game.dbo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * Created by Quach Thanh Phong
 * On 11/12/2022 - 3:28 PM
 */
@Entity
public class LeagueCountDBO implements Serializable {

    @Column(name = "count")
    public Integer count;

    @Id
    @Column(name = "league_id")
    public Integer leagueId;

}
