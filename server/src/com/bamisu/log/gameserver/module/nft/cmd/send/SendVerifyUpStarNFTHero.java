package com.bamisu.log.gameserver.module.nft.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.gamelib.item.entities.EquipDataVO;
import com.bamisu.gamelib.item.entities.StoneSlotVO;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.List;

/**
 * Created by Quach Thanh Phong
 * On 3/23/2022 - 8:48 PM
 */
public class SendVerifyUpStarNFTHero extends BaseMsg {

    public List<EquipDataVO> listEquipData;
    public List<ResourcePackage> listResource;

    public SendVerifyUpStarNFTHero() {
        super(CMD.CMD_VERIFY_UP_STAR_NFT_HERO);
    }

    public SendVerifyUpStarNFTHero(short errorCode) {
        super(CMD.CMD_VERIFY_UP_STAR_NFT_HERO, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError()) return;

        ISFSArray arrayPack = new SFSArray();
        ISFSObject objPack;
        //MONEY
        for(ResourcePackage money : listResource){
            objPack = new SFSObject();
            objPack.putUtfString(Params.ID, money.id);
            objPack.putInt(Params.AMOUNT, money.amount);

            arrayPack.addSFSObject(objPack);
        }

        //ITEM
        ISFSArray listStonePack;
        ISFSObject stonePack;
        for(EquipDataVO item : listEquipData){
            objPack = new SFSObject();
            objPack.putUtfString(Params.HASH, item.hash);
            objPack.putUtfString(Params.ID, item.id);
            objPack.putInt(Params.LEVEL, item.level);
            objPack.putInt(Params.STAR, item.star);
            objPack.putInt(Params.COUNT, item.count);

            listStonePack = new SFSArray();
            for(StoneSlotVO slot : item.listSlotStone){
                if(!slot.haveLock() || slot.stoneVO == null) continue;

                stonePack = new SFSObject();

                stonePack.putUtfString(Params.HASH, slot.stoneVO.hash);
                stonePack.putUtfString(Params.ID, slot.stoneVO.id);
                stonePack.putInt(Params.LEVEL, slot.stoneVO.level);

                listStonePack.addSFSObject(stonePack);
            }
            objPack.putSFSArray(Params.STONE, listStonePack);

            arrayPack.addSFSObject(objPack);
        }

        data.putSFSArray(Params.LIST, arrayPack);
    }
}
