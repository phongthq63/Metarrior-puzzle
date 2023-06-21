package com.bamisu.log.gameserver.module.hero.cmd.send;

import com.bamisu.log.gameserver.datamodel.hero.UserAllHeroModel;
import com.bamisu.log.gameserver.datamodel.hero.UserBlessingHeroModel;
import com.bamisu.log.gameserver.datamodel.hero.entities.HeroModel;
import com.bamisu.log.gameserver.module.characters.CharactersConfigManager;
import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.log.gameserver.module.hero.HeroManager;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SendLoadSceneUpStarHero extends BaseMsg {

    public UserAllHeroModel userAllHeroModel;
    public UserBlessingHeroModel userBlessingHeroModel;
    public Zone zone;

    public List<HeroModel> listHeroModel;

    public SendLoadSceneUpStarHero() {
        super(CMD.CMD_LOAD_SCENE_UP_STAR_HERO);
    }

    @Override
    public void packData() {
        super.packData();

        Set<String> listBlessing = HeroManager.BlessingManager.getInstance().getListHeroSlotBlessing(userBlessingHeroModel, zone).stream().
                map(obj -> obj.hashHero).
                collect(Collectors.toSet());

        ISFSArray listHero = new SFSArray();
        boolean blessing;
        ISFSObject heroSFS;
        for(HeroModel heroModel : listHeroModel){
            heroSFS = new SFSObject();
            heroSFS.putUtfString(Params.ModuleHero.HASH, heroModel.hash);
            heroSFS.putUtfString(Params.ID, heroModel.id);
            //Neu dc ban phuoc
            blessing = listBlessing.contains(heroModel.hash);
            heroSFS.putBool(Params.BLESSING, blessing);
            if(blessing){

                heroSFS.putShort(Params.LEVEL, (short) HeroManager.BlessingManager.getInstance().getLevelBlessingHero(userAllHeroModel, heroModel.id));
            }else {
                //Neu khong dc ban phuoc
                heroSFS.putShort(Params.LEVEL, heroModel.readLevel());
            }
            heroSFS.putShort(Params.ModuleHero.STAR, heroModel.star);
            listHero.addSFSObject(heroSFS);
        }

        data.putSFSArray(Params.ModuleHero.LIST_HERO, listHero);
    }
}
