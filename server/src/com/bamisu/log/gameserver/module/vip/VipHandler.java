package com.bamisu.log.gameserver.module.vip;

import com.bamisu.log.gameserver.module.store.StoreManager;
import com.bamisu.log.gameserver.module.vip.cmd.receive.RecClaimHonorGift;
import com.bamisu.log.gameserver.module.vip.cmd.receive.RecShowInfoVipIAP;
import com.bamisu.log.gameserver.module.vip.cmd.receive.RecShowLevelHonor;
import com.bamisu.log.gameserver.module.vip.cmd.send.SendClaimHonorGift;
import com.bamisu.log.gameserver.module.vip.cmd.send.SendShowInfoVipIAP;
import com.bamisu.gamelib.base.BaseExtension;
import com.bamisu.gamelib.base.ExtensionBaseClientRequestHandler;
import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.VipData;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.exceptions.SFSException;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.apache.thrift.TException;

import java.util.ArrayList;
import java.util.List;

public class VipHandler extends ExtensionBaseClientRequestHandler {
    VipManager vipManager;
    public VipHandler(BaseExtension extension) {
        super(extension);
        this.MODULE_ID = Params.Module.MODULE_VIP;
        vipManager = VipManager.getInstance();
        vipManager.setVipHandler(this);
    }

    public UserModel getUserModel(long uid){
        return extension.getUserManager().getUserModel(uid);
    }

    @WithSpan
    @Override
    protected void doClientRequest(int cmdId, User user, ISFSObject data) {
        switch (cmdId){
            case CMD.CMD_CLAIM_HONOR_GIFT:
                claimHonorGift(user, data);
                break;
            case CMD.CMD_SHOW_LIST_HONOR:
                showLevelHonor(user, data);
                break;
            case CMD.CMD_SHOW_INFO_VIP_IAP:
                try {
                    showInfoVipIAP(user, data);
                } catch (TException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @WithSpan
    private void showInfoVipIAP(User user, ISFSObject data) throws TException {
        RecShowInfoVipIAP rec = new RecShowInfoVipIAP(data);
        rec.unpackData();
        SendShowInfoVipIAP send = new SendShowInfoVipIAP();
        UserModel um = extension.getUserManager().getUserModel(user);
        List<VipData> list = new ArrayList<>();
        list.addAll(vipManager.getVip(um.accountID));
        send.list = list;
        send(send, user);

    }

    @WithSpan
    private void showLevelHonor(User user, ISFSObject data) {
        RecShowLevelHonor rec = new RecShowLevelHonor(data);
        rec.unpackData();
        vipManager.showLevelHonor(user, extension);
    }

    @WithSpan
    private void claimHonorGift(User user, ISFSObject data) {
        RecClaimHonorGift rec = new RecClaimHonorGift(data);
        rec.unpackData();
        vipManager.claimHonorGift(user, extension, rec.idHonor);

    }

    @Override
    protected void doServerEvent(SFSEventType type, ISFSEvent event) throws SFSException {

    }

    @Override
    protected void initHandlerClientRequest() {
        this.extension.addRequestHandler(Params.Module.MODULE_VIP, this);
    }

    @Override
    protected void initHandlerServerEvent() {
        this.extension.addServerHandler(Params.Module.MODULE_VIP, this);
    }
}
