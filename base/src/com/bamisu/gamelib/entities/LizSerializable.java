package com.bamisu.gamelib.entities;

import com.bamisu.gamelib.encryption.Encrypter;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.gamelib.encryption.Encrypter;
import com.bamisu.gamelib.utils.Utils;

/**
 * Created by Popeye on 9/29/2017.
 */
public abstract class LizSerializable {
    protected String encryptString = "";
    public String toJson() {
        return Utils.toJson(this);
    }

    public String encrypt() {
        encryptString = Encrypter.getInstance().encrypt(this.toJson());
        return encryptString;
    }

    public String encryptedString() {
        if (encryptString.equals("")) {
            this.encrypt();
        }
        return encryptString;
    }
}
