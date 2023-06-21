package com.bamisu.log.gameserver.module.lucky_draw.config.entities;

import com.bamisu.gamelib.entities.TokenResourcePackage;

import java.util.List;

public class LuckyDrawVO {
    public String item_id;
    public String type;
    public int status;
    public List<TokenResourcePackage> reward;
    public double item_win_rate;
    public String item_img;
    public String description;

}
