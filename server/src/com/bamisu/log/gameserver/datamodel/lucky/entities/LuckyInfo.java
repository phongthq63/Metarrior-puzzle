package com.bamisu.log.gameserver.datamodel.lucky.entities;

public class LuckyInfo {
    public int no1;
    public int no2;
    public int no3;
    public int time;
    public String buyDate;
    public int amount;
    public String type;
    public String isRevReward; // đã nhận - chưa nhận thưởng (1-null)

    public String isWin;// trúng thưởng (1-2-3) số
    public int priceAmt;
}
