package com.bamisu.puzzle.clientTest.base;

import com.bamisu.puzzle.clientTest.Client;

/**
 * Create by Popeye on 10:47 AM, 7/28/2020
 */
public abstract class ClientAction {
    public Client client;

    public ClientAction(Client client) {
        this.client = client;
    }

    public abstract void doAction();
}
