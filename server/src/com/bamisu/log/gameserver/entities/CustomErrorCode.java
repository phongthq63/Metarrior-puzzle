package com.bamisu.log.gameserver.entities;

import com.smartfoxserver.v2.exceptions.IErrorCode;

/**
 * Created by Popeye on 7/8/2017.
 */
public enum CustomErrorCode implements IErrorCode {
    // 100
    ERROR_SYSTEM(100),
    ERROR_SERVER_MAINTENANCE(101),

    ERR_USER_NOT_FOUND(201),
    ERR_USER_EXIST(202),
    ERR_WRONG_PASS(203),
    ERR_SESSION_NOT_LIVE(204),
    ERR_UNAME_SPECIAL_CHAR(205),
    ERR_PASS_SPECIAL_CHAR(206),
    ERR_UNAME_LONG(207),
    ERR_PASS_LONG(208),
    ERR_UNAME_EMPTY(209),
    ERR_PASS_EMPTY(210),
    ERR_MAX_ACC(211),
    ERR_DISPLAYNAME_INVALID(212),
    ERR_STEXT_INVALID(213),
    ERR_DISPLAYNAME_IS_EXIST(214),
    ERR_USER_IS_BANNED(215),
    ERR_TO_MANY_ACC_PER_DEVICE(216),
    ERR_INVALID_USERNAME_OR_PASSWORD(217);

    private short id;

    private CustomErrorCode(int id) {
        this.id = (short)id;
    }

    public short getId() {
        return this.id;
    }
}
