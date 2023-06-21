package com.bamisu.log.gameserver.module.lucky_draw;

import com.bamisu.gamelib.base.BaseExtension;
import com.bamisu.gamelib.base.ExtensionBaseClientRequestHandler;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.ServerConstant;
import com.bamisu.gamelib.entities.TokenResourcePackage;
import com.bamisu.gamelib.utils.UserUtils;
import com.bamisu.log.gameserver.datamodel.luckydraw.LuckyDrawRewardModel;
import com.bamisu.log.gameserver.datamodel.nft.UserTokenModel;
import com.bamisu.log.gameserver.module.bag.BagManager;
import com.bamisu.log.gameserver.module.lucky_draw.cmd.rev.RecLuckyDrawItemsConfig;
import com.bamisu.log.gameserver.module.lucky_draw.cmd.send.SendLuckyDrawItem;
import com.bamisu.log.gameserver.module.lucky_draw.cmd.send.SendLuckyDrawItemsConfig;
import com.bamisu.log.gameserver.module.lucky_draw.config.entities.LuckyDrawVO;
import com.bamisu.log.gameserver.module.nft.defind.ETokenBC;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.exceptions.SFSException;
import io.opentelemetry.instrumentation.annotations.WithSpan;

import java.util.ArrayList;
import java.util.List;

public class LuckyDrawHandler extends ExtensionBaseClientRequestHandler {
    public LuckyDrawHandler(BaseExtension extension) {
        super(extension);
        this.MODULE_ID = Params.Module.MODULE_LUCKY_DRAW;
//        new QuestGameEventHandler(extension.getParentZone());
    }

    @WithSpan
    @Override
    protected void doClientRequest(int cmdId, User user, ISFSObject data) {
        switch (cmdId) {
            case CMD.CMD_GET_LUCKY_DRAW_ITEMS:
                doGetConfigLucky(user, data);
                break;
            case CMD.CMD_GET_RESULT_LUCKY_DRAW:
                getResultSprint(user, data);
                break;
        }
    }

    @WithSpan
    private void doGetConfigLucky(User user, ISFSObject data) {
        long uid = extension.getUserManager().getUserModel(user).userID;
        RecLuckyDrawItemsConfig objGet = new RecLuckyDrawItemsConfig(data);
        UserTokenModel userTokenModel = UserTokenModel.copyFromDBtoObject(uid, user.getZone());

        // khoi tao hu
        LuckyDrawRewardModel ldrm = LuckyDrawRewardModel.copyFromDBtoObject(uid, user.getZone());
        if (ldrm == null) {
            ldrm = LuckyDrawRewardModel.createLuckyDrawReward(uid, user.getZone());
        }

        if (LuckyDrawManager.getInstance().checkTime()) {
            short errorCode = 0;
            errorCode = ServerConstant.ErrorCode.ERR_RECVICE_REWARD_LUCKY;
            SendLuckyDrawItemsConfig sendLuckyDrawItemsConfig = new SendLuckyDrawItemsConfig(errorCode);
            send(sendLuckyDrawItemsConfig, user);
        } else {
            // gui ra danh sach item vong quay ticket normal
            if (objGet.typeSpin == 0) {
                long numTicket = userTokenModel.readToken(ETokenBC.TICKET_SPIN);
                SendLuckyDrawItemsConfig objPut = new SendLuckyDrawItemsConfig();
                objPut.listItem = LuckyDrawManager.getInstance().getLuckyDrawConfig();
                objPut.amountTicket = numTicket;
                send(objPut, user);
            }

            // gui ra danh sach item vong quay ticket super
            if (objGet.typeSpin == 1) {
                long numTicketSuper = userTokenModel.readToken(ETokenBC.TICKET_SPIN_SUPER);
                SendLuckyDrawItemsConfig objPut = new SendLuckyDrawItemsConfig();
                objPut.listItem = LuckyDrawManager.getInstance().getLuckyDrawConfigSuper();
                objPut.amountTicket = numTicketSuper;
                send(objPut, user);
            }
        }

    }

    // tao ket qua vong quay
    @WithSpan
    private synchronized void getResultSprint(User user, ISFSObject data) {
        long uid = extension.getUserManager().getUserModel(user).userID;
        RecLuckyDrawItemsConfig objGet = new RecLuckyDrawItemsConfig(data);
        UserTokenModel userTokenModel = UserTokenModel.copyFromDBtoObject(uid, user.getZone());
        long numTicket = userTokenModel.readToken(ETokenBC.TICKET_SPIN);
        long numTicketSuper = userTokenModel.readToken(ETokenBC.TICKET_SPIN_SUPER);
        long sumTicket = userTokenModel.readToken(ETokenBC.TICKET); //  tổng số lượt quay (0 -> 4)
        long turnTicket = userTokenModel.readToken(ETokenBC.TURN_TICKET); // lượt trúng thưởng ngẫu nhiên trong 5 lần quay (1->5)
        List<TokenResourcePackage> tokenResourcePackages = new ArrayList<>();

        short errorCode = 0;
        errorCode = ServerConstant.ErrorCode.ERR_NOT_ENOUGH_TICKET;
        SendLuckyDrawItem objSend = new SendLuckyDrawItem();

        // kiem tra so luong ticket? Neu co thi -1 ticket va tao ket qua random
        if (numTicket <= 0 && objGet.typeSpin == 0) {
            SendLuckyDrawItem sendLuckyDrawItem = new SendLuckyDrawItem(errorCode);
            send(sendLuckyDrawItem, user);
        } else if (numTicketSuper <= 0 && objGet.typeSpin == 1) {
            SendLuckyDrawItem sendLuckyDrawItem = new SendLuckyDrawItem(errorCode);
            send(sendLuckyDrawItem, user);
        } else {
            // tru ticket vong quay thuong
            long value = -1;
            if (objGet.typeSpin == 0) {
                tokenResourcePackages.add(new TokenResourcePackage(ETokenBC.TICKET_SPIN.getId(), value));
            }

            // tru ticket vong quay dac biet
            if (objGet.typeSpin == 1) {
                tokenResourcePackages.add(new TokenResourcePackage(ETokenBC.TICKET_SPIN_SUPER.getId(), value));
            }

            // tổng vé đã quay hiện tại  (gtri từ 1 -> 5)
            long sumTicketLatest = sumTicket + 1;
            tokenResourcePackages.add(new TokenResourcePackage(ETokenBC.TICKET.getId(), 1L));
            String topUser = "";

            // kiem tra tien busd tối đa mà mỗi user nhan dc trong 1 tuan <= 10 busd
            if (LuckyDrawManager.getInstance().checkTotalAmountBUSDFollowSeason(user.getZone(), uid, objGet)) {

                if ((sumTicket == 0) && (turnTicket == 0)) { // lần đầu tiên sẽ cho trúng thưởng
                    if (objGet.typeSpin == 0) {
                        topUser = "3";
                    } else {
                        topUser = "6";
                    }
                    topUser = LuckyDrawManager.getInstance().checkAmountBUSD(topUser, objGet, user);

                } else if (sumTicketLatest == turnTicket) { // lượt trúng thưởng cố định
                    if (objGet.typeSpin == 0) {
                        topUser = "3";
                    } else {
                        topUser = "6";
                    }
                    topUser = LuckyDrawManager.getInstance().checkAmountBUSD(topUser, objGet, user);

                } else { // lượt trúng thưởng ngẫu nhiên
                    /*
                    Kiem tra user trong BD dang thuoc loai top nao?
                    topUser tu 1->6
                    + vong quay thuong 1,2,3    => mac dinh la 3 neu user k co trong bang xep hang dc luu trong BD (CAMPAIGN)
                    + vong quay dac biet 4,5,6  => mac dinh la 6 neu user k co trong bang xep hang dc luu trong BD  (BOSS)
                    */
                    topUser = LuckyDrawManager.getInstance().checkTypeTopUser(objGet, uid, user.getZone());

                    /*
                    Kiểm tra luong BUSD hien tai
                    topUser = {1,2,3,4,5,6}
                    Neu so tien hien tai trong hu < 100 thi tra ra config vong quay SOG
                    + topUser = 7 doi voi vong quay thuong
                    + topUser = 8 doi vong quay dac biet
                    */
                    topUser = LuckyDrawManager.getInstance().checkAmountBUSD(topUser, objGet, user);
                }
            } else {
                if (objGet.typeSpin == 0) {
                    topUser = "7";
                } else {
                    topUser = "8";
                }
            }

            /*
            kiểm tra nếu topUser = 1,2,3,4,5,6 => biến typeTokenSpin = "BUSD" hoặc "SOG"
            topUser = 7,8 =>  mặc định typeTokenSpin = "SOG"
            */
            String typeTokenSpin = LuckyDrawManager.getInstance().checkTypeTokenSpin(topUser, objGet, sumTicketLatest, turnTicket, sumTicket);

            String items = "";
            LuckyDrawVO vo = null;

            if ((sumTicketLatest == turnTicket) && (typeTokenSpin.equals("BUSD"))) {
                if (topUser.equals("3")) {
                    items = "1";
                    vo = LuckyDrawManager.getInstance().getConfigVo("1", topUser, typeTokenSpin, objGet.typeSpin);
                }
                if (topUser.equals("6")) {
                    items = "1";
                    vo = LuckyDrawManager.getInstance().getConfigVo("1", topUser, typeTokenSpin, objGet.typeSpin);
                }
            } else {
                items = LuckyDrawManager.getInstance().getLuckyNumResult(topUser, objGet.typeSpin, typeTokenSpin);
                vo = LuckyDrawManager.getInstance().getConfigVo(items, topUser, typeTokenSpin, objGet.typeSpin);
            }
            

            /*
              nếu ra phần thưởng là BUSD có gtri lớn thì cần xử lý:
               + hũ k dc nhỏ hơn 600 BUSD
               + số tiền hũ lớn hơn 10 lần
               + khi hết hũ BUSD chuyển sang phần thưởng SOG
               + mỗi user chỉ dc trúng tối đa 10 ~15 BUSD/ tuần
            */
            if (vo.reward.get(0).getId().equals("BUSD")) { // trúng busd
                // kiểm tra nếu hũ âm thì trả ra phần thưởng là SOG
                if (LuckyDrawManager.getInstance().checkNegative(topUser, user, Double.parseDouble(vo.reward.get(0).getValue().toString()))) {
                    // trả ra phần thưởng là SOG
                    items = "11";
                    vo = LuckyDrawManager.getInstance().getConfigVo(items, topUser, "SOG", objGet.typeSpin);
                    LuckyDrawManager.getInstance().updateAmountToken(vo, uid, topUser, user);
                    objSend.setItem_id(items);
                } else { // nếu hũ k âm
                    if (Double.parseDouble(vo.reward.get(0).getValue().toString()) > 4.5) { // trúng phần thưởng có trị giá > 4.5 busd
                        if (LuckyDrawManager.getInstance().checkBonusConditions(topUser, user)) {
                            LuckyDrawManager.getInstance().updateAmountToken(vo, uid, topUser, user);
                            objSend.setItem_id(items);
                        } else {
                            items = "1";
                            vo = LuckyDrawManager.getInstance().getConfigVo(items, topUser, typeTokenSpin, objGet.typeSpin);
                            LuckyDrawManager.getInstance().updateAmountToken(vo, uid, topUser, user);
                            objSend.setItem_id(items);
                        }
                    } else { // trúng phần thưởng có trị giá <4.5 busd
                        LuckyDrawManager.getInstance().updateAmountToken(vo, uid, topUser, user);
                        objSend.setItem_id(items);
                    }
                }
            } else { // trúng sog
                LuckyDrawManager.getInstance().updateAmountToken(vo, uid, topUser, user);
                objSend.setItem_id(items);
            }

            if (sumTicketLatest >= 5) {
                // reset gtri của sumTicketLatest về 0
                tokenResourcePackages.remove(1);
                tokenResourcePackages.add(new TokenResourcePackage(ETokenBC.TICKET.getId(), -sumTicket));

                // sinh random gtri của turnTicket từ 1->5
                long n = 0;
                if (turnTicket < 0) {
                    n = -turnTicket + 1;
                } else if (turnTicket == 0) {
                    int max = 5;
                    int min = 1;
                    n = (long) (Math.random() * (max - min + 1) + min);
                } else if (turnTicket == 1) {
                    int max = 4;
                    int min = 0;
                    n = (long) (Math.random() * (max - min + 1) + min);
                } else if (turnTicket == 2) {
                    int max = 3;
                    int min = -2;
                    n = (long) (Math.random() * (max - min + 1) + min);
                } else if (turnTicket == 3) {
                    int max = 2;
                    int min = -3;
                    n = (long) (Math.random() * (max - min + 1) + min);
                } else if (turnTicket == 4) {
                    int max = 1;
                    int min = -4;
                    n = (long) (Math.random() * (max - min + 1) + min);
                } else if (turnTicket == 5) {
                    int max = 0;
                    int min = -5;
                    n = (long) (Math.random() * (max - min + 1) + min);
                } else { // turnTiket > 5
                    n = -turnTicket + 1;
                }

                // rest gtri của turnTicket về 0
                tokenResourcePackages.add(new TokenResourcePackage(ETokenBC.TURN_TICKET.getId(), n));
            }

            send(objSend, user);
        }
        if (tokenResourcePackages.size() > 0) {
            BagManager.getInstance().addItemToDB(tokenResourcePackages, uid, user.getZone(), UserUtils.TransactionType.UPDATE_TICKET_LUCKY_DRAW);
        }
    }


    @Override
    protected void doServerEvent(SFSEventType type, ISFSEvent event) throws SFSException {
    }

    @Override
    protected void initHandlerClientRequest() {
        this.extension.addRequestHandler(Params.Module.MODULE_LUCKY_DRAW, this);
    }

    @Override
    protected void initHandlerServerEvent() {
        this.extension.addServerHandler(Params.Module.MODULE_LUCKY_DRAW, this);
    }
}
