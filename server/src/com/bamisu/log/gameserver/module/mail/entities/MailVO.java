package com.bamisu.log.gameserver.module.mail.entities;


import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.module.mail.define.EMailDefine;

import java.util.List;
import java.util.stream.Collectors;

public class MailVO {
    public String idMail;
    public String title;
    public String content;
    public String time;
    public boolean statusRead;         //Read or not    true chưa đọc, false doc roi
    public boolean statusReceive;      //Received or not    true là chưa nhận, false là nhận rồi
    public List<ResourcePackage> listGift;

    public MailVO() {
    }

    public MailVO(String title, String content, List<ResourcePackage> listGift) {
        this.idMail = Utils.genMailHash();
        this.title = title;
        this.content = content;
        this.time = Utils.timeNowString();
        this.statusRead = EMailDefine.UNREAD.getStatus();
        this.statusReceive = !listGift.isEmpty();
        this.listGift = listGift;
    }

    public MailVO cloneMail() {
        MailVO vo = new MailVO();
        vo.idMail = this.idMail;
        vo.title = this.title;
        vo.content = this.content;
        vo.time = this.time;
        vo.statusRead = this.statusRead;
        vo.statusReceive = this.statusReceive;
        vo.listGift = this.listGift.stream().map(ResourcePackage::new).collect(Collectors.toList());
        return vo;
    }
}
