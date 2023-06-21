package com.bamisu.log.gameserver.module.hero.cmd.send;

import com.bamisu.log.gameserver.datamodel.hero.entities.HeroModel;
import com.bamisu.log.gameserver.module.characters.CharactersConfigManager;
import com.bamisu.gamelib.item.entities.ItemSlotVO;
import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.ResourcePackage;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SendLoadSceneRetireHero extends BaseMsg {

    public int count;
    public boolean auto;
    public List<HeroModel> listHeroModel;

    public SendLoadSceneRetireHero() {
        super(CMD.CMD_LOAD_SCENE_RETIRE_HERO);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError())return;

        ISFSArray listHero = new SFSArray();
        ISFSObject heroSFS;
        ISFSArray arrayPack;
        ISFSObject objPack;
        for(HeroModel heroModel : listHeroModel){
            heroSFS = new SFSObject();
            heroSFS.putUtfString(Params.HASH_HERO, heroModel.hash);
            heroSFS.putUtfString(Params.ID, heroModel.id);
            heroSFS.putShort(Params.LEVEL, heroModel.readLevel());
            heroSFS.putShort(Params.STAR, heroModel.star);

            //Tai nguyen sau khi phan giai
            arrayPack = new SFSArray();
            objPack = new SFSObject();

            //List tai nguyen len cap hero
            List<ResourcePackage> resReset = heroModel.readResourceResetHeroModel();
            resReset.addAll(CharactersConfigManager.getInstance().getResourceRetireHeroConfig(heroModel.star));
            resReset = resReset.parallelStream().collect(Collectors.toMap(obj -> obj.id, Function.identity(), (keyOld, keyNew) -> new ResourcePackage(keyOld.id, keyOld.amount + keyNew.amount))).values().parallelStream().
                    collect(Collectors.toList());
            for(ResourcePackage res : resReset){
                objPack = new SFSObject();
                objPack.putUtfString(Params.ID, res.id);
                objPack.putLong(Params.AMOUNT, res.amount);

                arrayPack.addSFSObject(objPack);
            }

            //List do hero dang mac
            for(ItemSlotVO slot : heroModel.equipment){
                if(!slot.haveLock() || slot.equip == null){
                    continue;
                }
                objPack = new SFSObject();
                objPack.putUtfString(Params.HASH_WEAPON, slot.equip.hash);
                objPack.putUtfString(Params.ID, slot.equip.id);
                objPack.putShort(Params.STAR, (short) slot.equip.star);
                objPack.putShort(Params.LEVEL, (short) slot.equip.level);

                arrayPack.addSFSObject(objPack);
            }

            heroSFS.putSFSArray(Params.RESOURCE, arrayPack);

            listHero.addSFSObject(heroSFS);
        }

        data.putSFSArray(Params.LIST, listHero);
        data.putInt(Params.COUNT, count);
        data.putBool(Params.IS_AUTO, auto);
    }
}
