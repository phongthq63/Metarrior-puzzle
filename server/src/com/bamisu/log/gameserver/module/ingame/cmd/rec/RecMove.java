package com.bamisu.log.gameserver.module.ingame.cmd.rec;

import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.module.ingame.entities.skill.Combo;
import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Create by Popeye on 4:06 PM, 2/10/2020
 */
public class RecMove extends BaseCmd {
    public ISFSObject from;
    public ISFSObject to;
    public List<Combo> combos;
    public int comboCount;
    public ISFSArray puzzle;
    public boolean isEnd = false;
    public int point = 0;

    public RecMove(ISFSObject data) {
        super(data);
    }

    @Override
    public void unpackData() {
        from = data.getSFSObject(Params.FROM);
        if(data.containsKey(Params.IS_END)){
            isEnd = data.getBool(Params.IS_END);
        }

        if (data.containsKey(Params.TO)) {
            to = data.getSFSObject(Params.TO);
        }

        this.combos = new ArrayList<>();
        ISFSArray combosArr = data.getSFSArray(Params.COMBOS);
        for (int i = 0; i < combosArr.size(); i++) {
            ISFSObject object = combosArr.getSFSObject(i);
            combos.add(new Combo(object.getInt(Params.DIAMOND), object.getInt(Params.COUNT)));
        }

        comboCount = data.containsKey(Params.COMBO_COUNT) ? data.getInt(Params.COMBO_COUNT) : Utils.randomInRange(1, 5);
//        Collection<Integer> puzzle = data.getIntArray(Params.PUZZLE);

        if (data.containsKey(Params.POINT)) {
            point = data.getInt(Params.POINT);
        }
    }
}
