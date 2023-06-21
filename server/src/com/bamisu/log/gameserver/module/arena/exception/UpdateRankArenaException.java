package com.bamisu.log.gameserver.module.arena.exception;

import com.bamisu.gamelib.base.excepions.BaseServerException;

public class UpdateRankArenaException extends BaseServerException {

    public UpdateRankArenaException() {
        super((short) -1, "Lỗi cập nhật xếp hạng Arena");
    }
}
