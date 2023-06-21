package com.bamisu.gamelib.config;

import com.bamisu.gamelib.utils.Utils;
import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * Create by Popeye on 5:09 PM, 2/1/2021
 */
public class AvatarConfig {
    private static AvatarConfig instance;

    public static AvatarConfig getInstance() {
        if (instance == null) {
            instance = Utils.fromJson(Utils.loadConfig("user/avatar.json"), AvatarConfig.class);
        }
        return instance;
    }

    public List<String> avatarList;
}
