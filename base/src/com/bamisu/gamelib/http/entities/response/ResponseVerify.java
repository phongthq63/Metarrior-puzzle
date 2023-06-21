package com.bamisu.gamelib.http.entities.response;

import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * Created by Popeye on 2/9/2018.
 */
public class ResponseVerify {

    public int ec = 0;

    public ResponseVerify(ISFSObject res) {
        this.ec = res.getShort(Params.ERROR_CODE);
    }

}
