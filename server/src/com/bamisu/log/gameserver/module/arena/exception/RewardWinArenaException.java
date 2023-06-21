package com.bamisu.log.gameserver.module.arena.exception;

import com.bamisu.gamelib.base.excepions.BaseServerException;

public class RewardWinArenaException extends BaseServerException {

    public RewardWinArenaException() {
        super((short) -1, "Lỗi nhận thưởng thắng Arena");
    }
}
