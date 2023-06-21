package com.bamisu.gamelib.entities;

import java.util.HashMap;

/**
 * Created by Popeye on 2/26/2018.
 */
public class TransactionDetail {
    public static HashMap<Short,TransactionDetail> hsm = new HashMap<>();
    public short id;
    public String desc;

    public TransactionDetail(short id, String desc){
        this.id = id;
        this.desc = desc;

        hsm.put(this.id, this);
    }

    public TransactionDetail(TransactionDetail transactionDetail){
        this.id = transactionDetail.id;
        this.desc = transactionDetail.desc;
    }
}
