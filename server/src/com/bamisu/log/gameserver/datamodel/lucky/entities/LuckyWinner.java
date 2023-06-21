package com.bamisu.log.gameserver.datamodel.lucky.entities;

public class LuckyWinner {
    public long userId;
    public int no1;
    public int no2;
    public int no3;
    public String type;
    public int amount;
    public String isWin;
    public int priceAmt;
    public int totalPrice;
    public int received; // received = 0 : chưa nhận thưởng; received = 1 : đã nhận thưởng
    public String createDate;
    public String name;
}
