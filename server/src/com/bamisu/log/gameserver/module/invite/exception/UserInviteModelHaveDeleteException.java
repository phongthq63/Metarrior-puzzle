package com.bamisu.log.gameserver.module.invite.exception;

import com.bamisu.gamelib.base.excepions.BaseServerException;

public class UserInviteModelHaveDeleteException extends BaseServerException {

    public UserInviteModelHaveDeleteException() {
        super((short) -1, "Model UserInviteModel đã bị xóa");
    }
}
