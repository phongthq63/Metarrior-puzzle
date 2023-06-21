package com.bamisu.log.gameserver.module.darkgate.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.sql.game.dbo.RankLeagueDBO;
import com.bamisu.log.gameserver.module.darkgate.model.entities.AllianceRankItemVO;
import com.bamisu.log.gameserver.module.darkgate.model.entities.SoloRankItemVO;

/**
 * Create by Popeye on 9:25 AM, 11/25/2020
 */
public class SendEndlessNightMyRank extends BaseMsg {
    public RankLeagueDBO rankLeagueDBO;
    public int soloRank;
    public SoloRankItemVO soloRankItemVO;

    public int allianceRank;
    public AllianceRankItemVO allianceRankItemVO;

    public SendEndlessNightMyRank() {
        super(CMD.GET_ENDLESS_NIGHT_MY_RANK);
    }

    public SendEndlessNightMyRank(short errorCode) {
        super(CMD.GET_ENDLESS_NIGHT_MY_RANK, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError()) return;

        data.putInt("league_type", rankLeagueDBO != null && rankLeagueDBO.type != null ? rankLeagueDBO.type : -1);
        data.putInt(Params.SOLO_RANK, soloRank);
        data.putSFSObject(Params.SOLO_RANK_INFO, soloRankItemVO.toSFSObject());

        if(allianceRankItemVO != null){
            data.putInt(Params.ALLIANCE_RANK, allianceRank);
            data.putSFSObject(Params.ALLIANCE_RANK_INFO, allianceRankItemVO.toSFSObject());
        }
    }
}
