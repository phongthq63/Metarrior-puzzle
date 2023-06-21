package com.bamisu.log.gameserver.datamodel.chat.entities;

import java.util.ArrayList;
import java.util.List;

public interface IMessageChat {

    InfoMessage create(long uid, String message);
}
