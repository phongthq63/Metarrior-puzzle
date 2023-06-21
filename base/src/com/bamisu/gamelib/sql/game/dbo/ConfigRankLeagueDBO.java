package com.bamisu.gamelib.sql.game.dbo;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by Quach Thanh Phong
 * On 6/25/2022 - 3:13 PM
 */
@Entity
@Table(name = "t_config_rank_league")
public class ConfigRankLeagueDBO implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer id;

    public Integer season;

}
