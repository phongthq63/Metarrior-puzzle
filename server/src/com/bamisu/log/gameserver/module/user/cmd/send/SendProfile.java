package com.bamisu.log.gameserver.module.user.cmd.send;

import com.bamisu.log.gameserver.datamodel.hero.entities.HeroModel;
import com.bamisu.gamelib.entities.MoneyType;
import com.bamisu.log.gameserver.module.bag.BagManager;
import com.bamisu.log.gameserver.module.hero.HeroManager;
import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.gamelib.entities.ServerConstant;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Create by Popeye on 3:37 PM, 4/7/2020
 */
public class SendProfile extends BaseMsg {
    public UserModel userModel = null;
    public List<HeroModel> primaryHeroes = null;
    public String alliance = "";
    public List<String> avatarList = new ArrayList<>();
    public Zone zone;

    public SendProfile() {
        super(CMD.CMD_GET_PROFILE);
    }

    public SendProfile(short errorCode) {
        super(CMD.CMD_GET_PROFILE, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError()) return;

        data.putUtfString(Params.ID, String.valueOf(userModel.userID));
        data.putUtfString(Params.USER_DISPLAY_NAME, userModel.displayName);
        data.putSFSObject(Params.CHANGE_DISPLAY_NAME_PRICE, Utils.isDefaultDisplayName(userModel.displayName) ? new ResourcePackage(MoneyType.DIAMOND.getId(), 0).toSFSObject() : new ResourcePackage(MoneyType.DIAMOND.getId(), 100).toSFSObject());
        data.putInt(Params.LEVEL, BagManager.getInstance().getLevelUser(userModel.userID, zone));
        data.putInt(Params.POWER, HeroManager.getInstance().getPower(userModel.userID, zone));
        data.putUtfString(Params.USER_STATUS_TEXT, userModel.statusText);
        data.putUtfString(Params.AVATAR_ID, userModel.avatar);
        data.putInt(Params.AVATAR_FRAME, userModel.avatarFrame);
        data.putIntArray(Params.AVATAR_FRAME_LIST, userModel.avatarFrameList);
        data.putInt(Params.HONOR, 2);

        ISFSArray avatarArray = new SFSArray();
        for(String s : avatarList){
            avatarArray.addText(s);
        }
        data.putSFSArray(Params.AVATAR_LIST, avatarArray);

        data.putUtfString(Params.ALLIANCE, alliance);
        data.putShort(Params.GENDER, userModel.gender);
        data.putUtfString(Params.LANGUAGE, userModel.lang);

        data.putUtfString(Params.TIME_ZONE, ServerConstant.TIME_ZONE);
        data.putInt(Params.TIMESTAMP_SERVER, Utils.getTimestampInSecond());
        data.putUtfString(Params.TIME_SERVER, LocalDateTime.now(ServerConstant.TIME_ZONE_ID).format(DateTimeFormatter.ofPattern(ServerConstant.DATE_TIME_FORMAT)));
        data.putUtfString(Params.VERSION, ServerConstant.VERSION);

        ISFSArray listHero = new SFSArray();
        ISFSObject heroSFS;
        for(HeroModel heroModel : primaryHeroes){
            heroSFS = new SFSObject();
            heroSFS.putUtfString(Params.ModuleHero.HASH, heroModel.hash);
            heroSFS.putUtfString(Params.ID, heroModel.id);
            heroSFS.putShort(Params.LEVEL, heroModel.readLevel());
            heroSFS.putShort(Params.STAR, heroModel.star);
            listHero.addSFSObject(heroSFS);
        }

        data.putSFSArray(Params.PRIMARY_HEROES, listHero);
    }
}
