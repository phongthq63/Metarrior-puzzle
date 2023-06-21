package com.bamisu.gamelib.base.event.exception;

/**
 * Created by Popeye on 7/6/2017.
 */
public class ExceptionInternalMessage extends Exception {
    public short error = 0;
    public String message = "";

    public ExceptionInternalMessage(short error, String message) {
        this.error = error;
        this.message = message;
    }
}
