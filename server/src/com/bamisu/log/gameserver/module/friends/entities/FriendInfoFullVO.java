package com.bamisu.log.gameserver.module.friends.entities;

import com.bamisu.log.gameserver.datamodel.hero.entities.HeroModel;

import java.util.List;

public class FriendInfoFullVO extends FriendInfoVO{
    public List<HeroModel> listHero;
    public String statusText;
    public String gName;
    public String gAvatar;
    public boolean statusBlock;

    public FriendInfoFullVO(){}
    public FriendInfoFullVO(List<HeroModel> listHero, String statusText, String gName, String gAvatar, boolean statusBlock) {
        this.listHero = listHero;
        this.statusText = statusText;
        this.gName = gName;
        this.gAvatar = gAvatar;
    }

    public FriendInfoFullVO(int gender, String statusText,int avatarFrame, long uid, boolean send, boolean receive, int level, String avatar, String name, int server, int power, String campaign, int active, List<HeroModel> listHero, String gName, String gAvatar, boolean statusBlock) {
        super(gender, avatarFrame, uid, send, receive, level, avatar, name, server, power, campaign, active);
        this.listHero = listHero;
        this.statusText = statusText;
        this.gName = gName;
        this.gAvatar = gAvatar;
        this.statusBlock = statusBlock;
    }

    public FriendInfoFullVO(int level, String statusText, String avatar, String name, int server, int power, String campaign, int active, List<HeroModel> listHero) {
        super(level, avatar, name, server, power, campaign, active);
        this.listHero = listHero;
        this.statusText = statusText;
    }
}
