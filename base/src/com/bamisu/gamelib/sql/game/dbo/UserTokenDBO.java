package com.bamisu.gamelib.sql.game.dbo;

import com.bamisu.gamelib.entities.TransactionDetail;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "user_tokens")
public class UserTokenDBO {
    @Id
    @Column(name = "user_id")
    public long userId;

    public double busd;

    public double mewa;

    public double sog;

    public double ticket;

    @Column(name = "super_ticket")
    public double superTicket;

    public Timestamp updated;

    public UserTokenDBO() {}

    public UserTokenDBO(long userId) {
        this.userId = userId;
        this.busd = 0;
        this.mewa = 0;
        this.sog = 0;
        this.ticket = 0;
        this.superTicket = 0;
        this.updated = new Timestamp(System.currentTimeMillis());
    }

    public UserTokenDBO(long userId, double busd, double mewa, double sog, double ticket, double superTicket) {
        this.userId = userId;
        this.busd = busd;
        this.mewa = mewa;
        this.sog = sog;
        this.ticket = ticket;
        this.superTicket = superTicket;
        this.updated = new Timestamp(System.currentTimeMillis());
    }
}
