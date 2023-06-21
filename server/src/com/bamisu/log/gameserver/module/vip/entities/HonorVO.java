package com.bamisu.log.gameserver.module.vip.entities;

import com.bamisu.log.gameserver.module.characters.summon.entities.RewardVO;

import java.util.List;

public class HonorVO {
    public int id;
    public String name;
    public int requirement;
    public List<Benefits> rewards_1; //Gift can claim
//    public List<Benefits> rewards_2; //Receive when up honor
    public List<Benefits> benefits_1; //Benefits each day about money
    public List<Benefits> benefits_2; //Benefits other

    public HonorVO(int id, String name, int requirement, List<Benefits> rewards_1, List<Benefits> benefits_1, List<Benefits> benefits_2) {
        this.id = id;
        this.name = name;
        this.requirement = requirement;
        this.rewards_1 = rewards_1;
//        this.rewards_2 = rewards_2;
        this.benefits_1 = benefits_1;
        this.benefits_2 = benefits_2;
    }

    public HonorVO() {
    }
}
