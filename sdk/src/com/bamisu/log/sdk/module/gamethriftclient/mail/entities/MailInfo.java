package com.bamisu.log.sdk.module.gamethriftclient.mail.entities;

public class MailInfo {
    public String title = "";
    public String content = "";
    public String gift = "[]";

    public static MailInfo create(String title, String content, String resource) {
        MailInfo maillInfo = new MailInfo();
        maillInfo.title = title;
        maillInfo.content = content;
        maillInfo.gift = resource;

        return maillInfo;
    }
}
