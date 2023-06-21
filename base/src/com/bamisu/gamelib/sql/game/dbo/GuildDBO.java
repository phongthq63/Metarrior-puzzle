package com.bamisu.gamelib.sql.game.dbo;

import javax.persistence.Entity;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "guild")
public class GuildDBO implements Serializable {

    @Column(name = "id")
    public String id;

    @Id
    @Column(name = "name")
    public String name;

    @Column(name = "status")
    public int status;


    public static GuildDBO create(String id, String name) {
        GuildDBO guild = new GuildDBO();
        guild.id = id;
        guild.name = name;
        guild.status = 0;

        return guild;
    }

    public static GuildDBO create(String id, String name, int status) {
        GuildDBO guild = new GuildDBO();
        guild.id = id;
        guild.name = name;
        guild.status = status;

        return guild;
    }
}
