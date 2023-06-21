package com.bamisu.gamelib.sql.game.dbo;

import com.bamisu.gamelib.entities.TransactionDetail;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "transaction_details")
public class TransactionDBO {
    @Id
    @GeneratedValue
    public long id;

    @Column(name = "user_id")
    public long userId;

    @Column(name = "transaction_type")
    public short transactionId;

    @Column(name = "money_type")
    public String type;

    public double before_balance;

    public double delta;

    public double balance;

    public String description;

    public String remark;

    public Timestamp created;

    public TransactionDBO() {}

    private TransactionDBO(long userId, String type, double delta, double balance, String description, String remark) {
        this.userId = userId;
        this.transactionId = 0;
        this.type = type;
        this.delta = delta;
        this.balance = balance;
        this.description = description;
        this.remark = remark;
        this.created = new Timestamp(System.currentTimeMillis());
    }

    private TransactionDBO(long userId, String type, long delta, long balance, String description, String remark) {
        this.userId = userId;
        this.transactionId = 0;
        this.type = type;
        this.delta = delta * 1.0;
        this.balance = balance * 1.0;
        this.description = description;
        this.remark = remark;
        this.created = new Timestamp(System.currentTimeMillis());
    }

    public TransactionDBO(long userId, String type, double beforeBalance, double delta, double balance, TransactionDetail transactionDetail) {
        this(userId, type, delta, balance, "", "");
        this.before_balance = beforeBalance;
        if (transactionDetail != null) {
            this.description = transactionDetail.desc;
            this.transactionId = transactionDetail.id;
        }
    }

    public TransactionDBO(long userId, String type, double before_balance, long delta, long balance, TransactionDetail transactionDetail) {
        this(userId, type, before_balance, delta * 1.0, balance * 1.0, transactionDetail);

    }

    public TransactionDBO(long userId, String type, double before_balance, double delta, long balance, TransactionDetail transactionDetail) {
        this(userId, type, before_balance, delta, balance * 1.0, transactionDetail);
    }

}
