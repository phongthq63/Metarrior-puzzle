package com.bamisu.log.sdk.module.multiserver.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Create by Popeye on 5:36 PM, 8/28/2020
 */
public class ClusterMap {
    @JsonProperty("cluster_list")
    public List<Cluster> clusterList;
}
