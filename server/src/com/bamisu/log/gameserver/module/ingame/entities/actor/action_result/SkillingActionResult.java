package com.bamisu.log.gameserver.module.ingame.entities.actor.action_result;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.bamisu.log.gameserver.module.skill.template.entities.ReplateDiamond;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by Popeye on 4:16 PM, 6/4/2020
 */
public class SkillingActionResult extends ActionResult {
    @JsonIgnore
    public Object skillIndex = - 1;
    @JsonIgnore
    public List<String> fightTarget = new ArrayList<>();
    @JsonIgnore
    public boolean isMiss = false;
    @JsonIgnore
    public List<String> buffTarget = new ArrayList<>();
    @JsonIgnore
    public ReplateDiamond replateDiamond = new ReplateDiamond();

    public SkillingActionResult packData(){
        this.props = new ArrayList<>();
        props.add(skillIndex);
        props.add(fightTarget);
        props.add(isMiss);
        props.add(buffTarget);
        props.add(replateDiamond);
        return this;
    }
}
