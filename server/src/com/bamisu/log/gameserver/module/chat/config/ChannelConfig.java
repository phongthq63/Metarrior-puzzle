package com.bamisu.log.gameserver.module.chat.config;

import com.bamisu.log.gameserver.module.chat.config.entities.ChannelVO;

import java.util.List;
import java.util.stream.Collectors;

public class ChannelConfig {
    public ChannelVO defaultChannel;
    public List<ChannelVO> list;

    public ChannelVO createChannel(ChannelVO channel, List<ChannelVO> list){
        ChannelVO creater = new ChannelVO();
        int countChannel = (int) list.stream().
                filter(obj -> obj.type.toLowerCase().equals(channel.type.toLowerCase())).
                count();
        //Id channel = type + so luong
        //VD: vn4, en2
        creater.id = channel.type + (countChannel + 1);
        creater.name = channel.name.substring(0, channel.name.lastIndexOf(" ")) + " " + (countChannel + 1);
        creater.type = channel.type;
        creater.maxUser = channel.maxUser;

        return creater;
    }
}
