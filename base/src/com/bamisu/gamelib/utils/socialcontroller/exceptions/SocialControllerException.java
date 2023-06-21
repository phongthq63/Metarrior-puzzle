package com.bamisu.gamelib.utils.socialcontroller.exceptions;

public class SocialControllerException extends Exception {
    public short error = 0;
    public String message = "";

    public SocialControllerException(short error, String message) {
        this.error = error;
        this.message = message;
    }
}
