package com.bamisu.log.gameserver.module.hero.exception;

import com.bamisu.gamelib.base.excepions.BaseServerException;

public class IndexOutOfBoundByChangeConfigException extends BaseServerException {

    public IndexOutOfBoundByChangeConfigException() {
        super((short) -1, "Thay đổi config gây lỗi vượt quá mảng do data chưa refresh");
    }
}
