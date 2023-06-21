package com.bamisu.log.gameserver.entities;

import com.smartfoxserver.v2.util.IDisconnectionReason;

/**
 * Created by Popeye on 7/18/2017.
 */
public enum LizClientDissconnectionReason implements IDisconnectionReason{
    LOGINBYOTHER(4);

    private final int value;

    private LizClientDissconnectionReason(int id) {
        this.value = id;
    }

    @Override
    public int getValue() {
        return this.value;
    }

    @Override
    public byte getByteValue() {
        return (byte)this.value;
    }
}
