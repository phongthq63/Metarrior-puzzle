package com.bamisu.log.gameserver.module.mail.entities;

import com.bamisu.gamelib.entities.ResourcePackage;

import java.util.List;

public class MailBoxVO {
    public long idMail;
    public String title;
    public String time;
    public boolean statusRead;         //Read or not
    public boolean statusReceive;      //Received or not
    public List<ResourcePackage> listGift;

    public MailBoxVO() {
    }

    public MailBoxVO(long idMail, String title, String time, boolean statusRead, boolean statusReceive, List<ResourcePackage> listGift) {
        this.idMail = idMail;
        this.title = title;
        this.time = time;
        this.statusRead = statusRead;
        this.statusReceive = statusReceive;
        this.listGift = listGift;
    }
}
