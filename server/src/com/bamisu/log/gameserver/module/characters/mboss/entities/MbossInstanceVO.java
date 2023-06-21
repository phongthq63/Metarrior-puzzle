package com.bamisu.log.gameserver.module.characters.mboss.entities;

/**
 * Create by Popeye on 11:23 AM, 4/28/2020
 */
public class MbossInstanceVO {
    public String id;
    public short level;
    public short star;
    public String kingdom;
    public String element;

    public static MbossInstanceVO createMBossInstanceVO(String id, int level, int star, String kingdom, String element) {
        MbossInstanceVO mBossInstanceVO = new MbossInstanceVO();
        mBossInstanceVO.id = id;
        mBossInstanceVO.level = (short) level;
        mBossInstanceVO.star = (short) star;
        mBossInstanceVO.kingdom = kingdom;
        mBossInstanceVO.element = element;

        return mBossInstanceVO;
    }
}
