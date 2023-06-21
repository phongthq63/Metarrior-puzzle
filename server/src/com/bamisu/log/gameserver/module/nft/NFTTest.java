package com.bamisu.log.gameserver.module.nft;

import com.bamisu.gamelib.entities.MoneyType;
import com.bamisu.log.gameserver.module.characters.CharactersConfigManager;
import com.bamisu.log.gameserver.module.characters.summon.entities.SummonVO;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class NFTTest {
    public static void main(String[] args) {
        long now = System.currentTimeMillis() / 1000;
        long now2 = now + 60 * 60 * 24 + 3599;
        long delta = now2 - now;
        int hours = (int) TimeUnit.SECONDS.toHours(delta);
        System.out.println(hours);
        int day = (int) Math.ceil(hours * 1.0 / 24);
        System.out.println(day);
    }
}
