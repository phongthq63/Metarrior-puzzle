package com.bamisu.log.gameserver.module.arena.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.log.gameserver.datamodel.hero.UserAllHeroModel;
import com.bamisu.log.gameserver.datamodel.hero.UserBlessingHeroModel;
import com.bamisu.log.gameserver.datamodel.hero.entities.HeroModel;
import com.bamisu.log.gameserver.module.characters.CharactersConfigManager;
import com.bamisu.log.gameserver.module.hero.HeroManager;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SendLoadSceneTeamHeroArena extends BaseMsg {

    public UserAllHeroModel userAllHeroModel;       //Da dc deep clone
    public UserBlessingHeroModel userBlessingHeroModel;
    public Zone zone;

    public List<String> team;
    public List<HeroModel> listHeroModel;

    public SendLoadSceneTeamHeroArena() {
        super(CMD.CMD_LOAD_SCENE_TEAM_HERO_ARENA);
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

        for(HeroModel heroModel : listHeroModel.parallelStream().
                map(HeroModel::createByHeroModel).
                collect(Collectors.toList())){

            heroSFS = new SFSObject();
            heroSFS.putUtfString(Params.ModuleHero.HASH, heroModel.hash);
            heroSFS.putUtfString(Params.ID, heroModel.id);
            //Neu dc ban phuoc
            blessing = listBlessing.contains(heroModel.hash);
            heroSFS.putBool(Params.BLESSING, blessing);
            if(blessing){
                //Neu dc ban phuoc
                heroModel.level = (short) HeroManager.BlessingManager.getInstance().getLevelBlessingHero(userAllHeroModel, heroModel.id);
            }

            heroSFS.putShort(Params.LEVEL, heroModel.readLevel());
            heroSFS.putShort(Params.STAR, heroModel.star);
            heroSFS.putInt(Params.POWER, HeroManager.getInstance().getPower(heroModel, zone));

            listHero.addSFSObject(heroSFS);
        }

        data.putSFSArray(Params.ModuleHero.LIST_HERO, listHero);
        data.putUtfStringArray(Params.TEAM, team);
    }
}
