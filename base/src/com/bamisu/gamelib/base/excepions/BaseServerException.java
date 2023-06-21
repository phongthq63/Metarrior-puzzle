package com.bamisu.gamelib.base.excepions;

/**
 * Created by Popeye on 7/12/2017.
 */
public class BaseServerException extends Exception {
    public short error = 0;
    public String message = "";

    public BaseServerException(short error, String message) {
        this.error = error;
        this.message = message;
    }

    @Override
    public void printStackTrace() {
        super.printStackTrace();
        System.err.println(message);
    }
}
