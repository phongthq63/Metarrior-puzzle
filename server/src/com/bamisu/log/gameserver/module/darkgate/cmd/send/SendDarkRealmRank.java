package com.bamisu.log.gameserver.module.darkgate.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.log.gameserver.module.darkgate.DarkGateManager;
import com.bamisu.log.gameserver.module.darkgate.model.DarkRealmModel;
import com.bamisu.log.gameserver.module.darkgate.model.entities.AllianceRankItemVO;
import com.bamisu.log.gameserver.module.darkgate.model.entities.SoloRankItemVO;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.List;

/**
 * Create by Popeye on 3:31 PM, 11/17/2020
 */
public class SendDarkRealmRank extends BaseMsg {
    public int leagueId;
    public int leagueType;
    public String leagueName;

    public List<SoloRankItemVO> soloRanks;
    public List<AllianceRankItemVO> allianceRanks;

    public int soloRank;
    public SoloRankItemVO soloRankItemVO;

    public int allianceRank;
    public AllianceRankItemVO allianceRankItemVO;

    public SendDarkRealmRank() {
        super(CMD.GET_DARK_REALM_RANK);
    }

    public SendDarkRealmRank(short errorCode) {
        super(CMD.GET_DARK_REALM_RANK, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError()) return;

        data.putInt("league", leagueId);
        data.putInt("league_type", leagueType);
        data.putUtfString("league_name", leagueName);

        data.putInt(Params.SOLO_RANK, soloRank);
        data.putSFSObject(Params.SOLO_RANK_INFO, soloRankItemVO.toSFSObject());

        ISFSArray arraySolo = new SFSArray();
        data.putSFSArray(Params.SOLO, arraySolo);
        for(SoloRankItemVO soloRankItemVO : soloRanks){
            arraySolo.addSFSObject(soloRankItemVO.toSFSObject());
        }

        if(allianceRankItemVO != null){
            data.putInt(Params.ALLIANCE_RANK, allianceRank);
            data.putSFSObject(Params.ALLIANCE_RANK_INFO, allianceRankItemVO.toSFSObject());
        }


        ISFSArray arrayAlliance = new SFSArray();
        data.putSFSArray(Params.ALLIANCE, arrayAlliance);
        for(AllianceRankItemVO vo : allianceRanks){
            arrayAlliance.addSFSObject(vo.toSFSObject());
        }
    }
}
