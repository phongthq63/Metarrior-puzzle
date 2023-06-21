package com.bamisu.log.sdk.telegrambot.exception;

import com.bamisu.gamelib.base.excepions.BaseServerException;

public class TimeEventInvalidException extends BaseServerException {

    public TimeEventInvalidException() {
        super((short) -1, "Thời gian tạo sự kiện không hợp lệ");
    }
}
