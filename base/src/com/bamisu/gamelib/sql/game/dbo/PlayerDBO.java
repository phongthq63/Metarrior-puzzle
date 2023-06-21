package com.bamisu.gamelib.sql.game.dbo;

import com.bamisu.gamelib.utils.Utils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "player")
public class PlayerDBO implements Serializable {
    @Id
    @Column(name = "uid")
    public long uid;

    @Column(name = "account_id")
    public String accountID;

    @Column(name = "level")
    public int level;

    @Column(name = "create_time")
    public int create;

    @Column(name = "update_time")
    public int update;


    public static PlayerDBO create(long uid, String accountID, int create, int update) {
        PlayerDBO player = new PlayerDBO();
        player.uid = uid;
        player.level = 1;
        player.create = create;
        player.update = update;
        player.accountID = accountID;
        return player;
    }
}
