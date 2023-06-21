package com.bamisu.log.gameserver.module.hero.cmd.send;

import com.bamisu.log.gameserver.datamodel.bag.UserBagModel;
import com.bamisu.log.gameserver.datamodel.hero.UserSummonHeroModel;
import com.bamisu.log.gameserver.entities.EStatus;
import com.bamisu.log.gameserver.module.characters.CharactersConfigManager;
import com.bamisu.log.gameserver.module.characters.kingdom.entities.KingdomVO;
import com.bamisu.log.gameserver.module.characters.summon.entities.KingdomSummonVO;
import com.bamisu.log.gameserver.module.characters.summon.entities.SummonVO;
import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.List;
import java.util.stream.Collectors;

public class SendLoadSceneSummonHero extends BaseMsg {

    public UserSummonHeroModel userSummonHeroModel;
    public int timeToNextDay;
    public UserBagModel userBagModel;
    public Zone zone;
    private int currentTime = Utils.getTimestampInSecond();

    public SendLoadSceneSummonHero() {
        super(CMD.CMD_LOAD_SCENE_SUMMON_HERO);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError())return;


        //Thong tin cua ruong
        data.putUtfString(Params.ModuleChracter.CHEST, userSummonHeroModel.idBonus);
        data.putInt(Params.ModuleChracter.POINT, userSummonHeroModel.bonusPoint);
        data.putInt(Params.TIME, timeToNextDay);

        ISFSObject packDay;
        int currentDay;
        int nextDay;

        //Ngay element cua server
//        List<ElementVO> listElement = CharactersConfigManager.getInstance().getElementConfig();
//        packDay = new SFSObject();
//        currentDay = userSummonHeroModel.readElementDay(zone);
//        nextDay = (currentDay < listElement.size() - 1) ? currentDay + 1 : 0;
//        packDay.putUtfString(Params.ModuleChracter.CURRENT, listElement.get(currentDay).id);
//        packDay.putUtfString(Params.ModuleChracter.NEXT, listElement.get(nextDay).id);
//        data.putSFSObject(Params.ModuleChracter.ELEMENT, packDay);

        //Ngay kingdom cua server
        List<KingdomSummonVO> listKingdom = CharactersConfigManager.getInstance().getKingdomSummonConfig();

        packDay = new SFSObject();
        currentDay = userSummonHeroModel.readKingdomDay(zone);
        nextDay = (currentDay < listKingdom.size() - 1) ? currentDay + 1 : 0;
        packDay.putUtfString(Params.ModuleChracter.CURRENT, listKingdom.get(currentDay).id);
        packDay.putUtfString(Params.ModuleChracter.NEXT, listKingdom.get(nextDay).id);
        data.putSFSObject(Params.ModuleChracter.KINGDOM, packDay);

        //Thong tin cua tung banner
        ISFSArray listSummon = new SFSArray();
        ISFSObject summon;
        for(SummonVO summonVO : CharactersConfigManager.getInstance().getSummonConfig().listSummonBanner){
            summon = new SFSObject();
            summon.putUtfString(Params.ID, summonVO.id);
            int timeFreeCf = CharactersConfigManager.getInstance().getDistanceTimeSummonFreeConfig(summonVO.id);
            if(timeFreeCf > 0){
                int freeTime = timeFreeCf - (currentTime - userSummonHeroModel.getBannerFreeTime(summonVO.id));
                if(summonVO.timeFree > 0){
                    summon.putInt(Params.TIME, (freeTime <= 0) ? 0 : freeTime);
                }else {
                    summon.putNull(Params.TIME);
                }
            }
            listSummon.addSFSObject(summon);
        }
        data.putSFSArray(Params.ModuleChracter.SUMMON, listSummon);
    }
}
