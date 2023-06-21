package com.bamisu.log.gameserver.module.arena.exception;

import com.bamisu.gamelib.base.excepions.BaseServerException;

public class UpdateArenaPointException extends BaseServerException {

    public UpdateArenaPointException() {
        super((short) -1, "Lỗi cập nhật điểm Arena");
    }
}
