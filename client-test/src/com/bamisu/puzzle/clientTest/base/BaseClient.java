package com.bamisu.puzzle.clientTest.base;

import com.bamisu.gamelib.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by Popeye on 10:46 AM, 7/28/2020
 */
public abstract class BaseClient {
    public List<ClientAction> actionList = new ArrayList<>();

    public void addAction(ClientAction action){
        actionList.add(action);
    }

    public void doRandomAction(){
        actionList.get(Utils.randomInRange(0, actionList.size() - 1)).doAction();
    }
}
