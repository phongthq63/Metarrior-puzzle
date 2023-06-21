package com.bamisu.log.gameserver.module.bag.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.datamodel.hero.entities.HeroModel;
import com.bamisu.log.gameserver.module.characters.CharactersConfigManager;
import com.bamisu.log.gameserver.module.characters.hero.entities.HeroVO;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.List;

public class SendUsingFragmentHero extends BaseMsg {

    public List<HeroModel> listHero;


    public SendUsingFragmentHero() {
        super(CMD.CMD_USING_FRAGMENT_HERO);
    }

    public SendUsingFragmentHero(short errorCode) {
        super(CMD.CMD_USING_FRAGMENT_HERO, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if (isError())return;

        ISFSArray arrayPack = new SFSArray();
        ISFSObject objPack;
        CharactersConfigManager charactersConfigManager = CharactersConfigManager.getInstance();
        for (HeroModel model: listHero){
            objPack = new SFSObject();
            objPack.putUtfString(Params.HASH, model.hash);
            objPack.putUtfString(Params.ID, model.id);
            objPack.putShort(Params.LEVEL, model.level);
            objPack.putShort(Params.ModuleHero.STAR, model.star);

            HeroVO heroCf = charactersConfigManager.getHeroConfig(model.id);
            objPack.putUtfString("clas", heroCf.clas);
            objPack.putSFSObject(Params.STATS, SFSObject.newFromJsonData(Utils.toJson(heroCf)));
            objPack.putSFSObject(Params.GROW, SFSObject.newFromJsonData(Utils.toJson(charactersConfigManager.getHeroStatsGrowConfig(model.id))));

            arrayPack.addSFSObject(objPack);
        }
        data.putSFSArray(Params.LIST, arrayPack);
    }
}
