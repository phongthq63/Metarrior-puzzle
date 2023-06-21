package com.bamisu.log.gameserver.module.hero.cmd.send;

import com.bamisu.log.gameserver.datamodel.hero.UserAllHeroModel;
import com.bamisu.log.gameserver.datamodel.hero.UserBlessingHeroModel;
import com.bamisu.log.gameserver.datamodel.hero.entities.HeroModel;
import com.bamisu.log.gameserver.datamodel.hero.entities.HeroSlotBlessing;
import com.bamisu.gamelib.entities.MoneyType;
import com.bamisu.log.gameserver.module.characters.CharactersConfigManager;
import com.bamisu.log.gameserver.module.characters.blessing.UnlockBlessingConfig;
import com.bamisu.log.gameserver.module.characters.blessing.entities.UnlockBlessingVO;
import com.bamisu.log.gameserver.module.hero.HeroManager;
import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SendLoadSceneHeroBlessing extends BaseMsg {

    public List<HeroModel> listHeroModel;
    public List<HeroModel> listLevelHighest;
    public List<HeroSlotBlessing> listBlessing;
    public UserBlessingHeroModel userBlessingHeroModel;
    public UserAllHeroModel userAllHeroModel;
    private int timeStamp = Utils.getTimestampInSecond();

    public SendLoadSceneHeroBlessing() {
        super(CMD.CMD_LOAD_SCENE_HERO_BLESSING);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError())return;

        //Do su ly = thread -> bat dong bo -> p lock
        final Object lockBlessing = new Object();
        final Object lockLevel = new Object();

        ISFSObject objPack;
        synchronized (lockLevel){
            //5 hero cao level nhat
            ISFSArray arrayLevelPack = new SFSArray();
            for(HeroModel heroModel : listLevelHighest){
                objPack = new SFSObject();
                objPack.putUtfString(Params.HASH_HERO, heroModel.hash);
                objPack.putUtfString(Params.ID, heroModel.id);
                objPack.putShort(Params.LEVEL, heroModel.readLevel());
                objPack.putShort(Params.STAR, heroModel.star);
                arrayLevelPack.addSFSObject(objPack);
            }
            data.putSFSArray(Params.LEVEL, arrayLevelPack);
        }


        int timeReblessing = CharactersConfigManager.getInstance().getTimeReblessingConfig();
        //List hero dc ban phuoc
        synchronized (lockBlessing){
            ISFSArray arrayBlesPack = new SFSArray();
            for(HeroSlotBlessing blessing : listBlessing){
                objPack = new SFSObject();

                objPack.putShort(Params.POSITION, blessing.position);
                if(blessing.hashHero == null || blessing.hashHero.isEmpty()){
                    objPack.putInt(Params.TIME, timeReblessing - (timeStamp - blessing.timeStamp));
                }else {
                    objPack.putUtfString(Params.HASH_HERO, blessing.hashHero);
                    objPack.putUtfString(Params.ID, blessing.idHero);

                    for(HeroModel heroModel : listHeroModel){
                        if(heroModel.hash.equals(blessing.hashHero)){
                            objPack.putShort(Params.STAR, heroModel.star);
                            break;
                        }
                    }

                    objPack.putShort(Params.OLD_LEVEL, blessing.level);
                    objPack.putShort(Params.LEVEL, (short) HeroManager.BlessingManager.getInstance().getLevelBlessingHero(userAllHeroModel, blessing.idHero));
                }

                arrayBlesPack.addSFSObject(objPack);
            }
            data.putSFSArray(Params.BLESSING, arrayBlesPack);
        }

        synchronized (lockLevel){
            synchronized (lockBlessing){
                //Loai bo cac hero duoc ban phuoc + team hero cao level nhat
                Set<String> setBlessing = listBlessing.parallelStream().map(obj -> obj.hashHero).collect(Collectors.toSet());
                setBlessing.addAll(listLevelHighest.parallelStream().map(obj -> obj.hash).collect(Collectors.toSet()));

                //List Hero chon
                ISFSArray arrayPack = new SFSArray();
                for(HeroModel heroModel : listHeroModel){
                    if(setBlessing.contains(heroModel.hash)){
                        continue;
                    }
                    objPack = new SFSObject();
                    objPack.putUtfString(Params.HASH_HERO, heroModel.hash);
                    objPack.putUtfString(Params.ID, heroModel.id);
                    objPack.putShort(Params.LEVEL, heroModel.readLevel());
                    objPack.putShort(Params.STAR, heroModel.star);
                    arrayPack.addSFSObject(objPack);
                }
                data.putSFSArray(Params.LIST, arrayPack);
            }
        }

        data.putInt(Params.ModuleHero.CURRENT_SIZE_BAG_HERO, userBlessingHeroModel.size);

        UnlockBlessingConfig unlockBlessingCf = CharactersConfigManager.getInstance().getUnlockBlessingConfig();
        int maxBag = unlockBlessingCf.initOpen;
        for(UnlockBlessingVO cf : unlockBlessingCf.unlock){
            maxBag += cf.cost.size();
        }
        data.putInt(Params.ModuleHero.MAX_SIZE_BAG_HERO, maxBag);

        data.putInt(MoneyType.MIRAGE_ESSENCE.getId(), userBlessingHeroModel.unlockEssence);
        data.putInt(MoneyType.DIAMOND.getId(), userBlessingHeroModel.unlockDiamont);
    }
}
