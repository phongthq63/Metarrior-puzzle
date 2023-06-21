package com.bamisu.log.sdk.module.multiserver.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Create by Popeye on 5:37 PM, 8/28/2020
 */
public class Cluster {
    @JsonProperty("id")
    public String id;

    @JsonProperty("server_list")
    public List<Integer> serverList;

    @JsonProperty("addr")
    public String addr;

    @JsonProperty("socket_port")
    public int socketPort;

    @JsonProperty("proxy_port")
    public int proxyPort;
}
