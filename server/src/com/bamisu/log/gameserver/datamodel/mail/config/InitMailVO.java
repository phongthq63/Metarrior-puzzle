package com.bamisu.log.gameserver.datamodel.mail.config;

import com.bamisu.gamelib.entities.ResourcePackage;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by Popeye on 3:54 PM, 10/22/2020
 */
public class InitMailVO {
    public String title;
    public String content;
    public List<ResourcePackage> gifts = new ArrayList<>();
    public int fromTime;
    public int toTime;
}
