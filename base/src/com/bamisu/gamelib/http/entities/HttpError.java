package com.bamisu.gamelib.http.entities;

/**
 * Create by Popeye on 12:32 PM, 10/12/2019
 */
public class HttpError {
    public int ec;
    public String mess;

    public HttpError(int ec, String mess) {
        this.ec = ec;
        this.mess = mess;
    }
}
