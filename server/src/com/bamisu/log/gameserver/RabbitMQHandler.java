package com.bamisu.log.gameserver;

import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.ServerConstant;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.module.sdkthriftclient.SDKGateAccount;
import com.bamisu.log.rabbitmq.RabbitMQManager;
import com.bamisu.log.rabbitmq.entities.*;
import com.bamisu.log.sdkthrift.entities.TUserInfo;
import com.smartfoxserver.v2.SmartFoxServer;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;
import io.opentelemetry.instrumentation.annotations.WithSpan;

import java.nio.charset.StandardCharsets;

public class RabbitMQHandler {
    private static final RabbitMQHandler instance = new RabbitMQHandler();
    private RabbitMQHandler() {}
    public static RabbitMQHandler getInstance() {
        return instance;
    }

    @WithSpan
    public void onMessage(String message, String cmdId) {
        System.out.println("Cmd2 " + cmdId);
        System.out.println("Message2 " + message);
        MarketDto marketDto;
        HeroDto heroDto;
        TokenDto tokenDto;
        switch (cmdId) {
            case Configs.BUY_NFT:
            case Configs.TRANSFER_HERO:
                marketDto = Utils.fromJson(message, MarketDto.class);
                this.handleTransferHero(marketDto);
                break;
            case Configs.SALE_NFT:
                marketDto = Utils.fromJson(message, MarketDto.class);
                this.handleSaleNft(marketDto);
                break;
            case Configs.UN_SALE_NFT:
                marketDto = Utils.fromJson(message, MarketDto.class);
                this.handleUnSaleNft(marketDto);
                break;
            case Configs.DEPOSIT_TOKEN:
                tokenDto = Utils.fromJson(message, TokenDto.class);
                this.handleDepositToken(tokenDto);
                break;
            case Configs.WITHDRAW_TOKEN:
                tokenDto = Utils.fromJson(message, TokenDto.class);
                this.handleWithdrawToken(tokenDto);
                break;
            case Configs.CLAIM_HERO:
                heroDto = Utils.fromJson(message, HeroDto.class);
                this.handleClaimHero(heroDto);
                break;
            case Configs.BURN_HERO:
                heroDto = Utils.fromJson(message, HeroDto.class);
                this.handeAscendHero(heroDto);
                break;
        }
    }

    @WithSpan
    public void sendBreedHero(ISFSObject res) {
        if (res == null) {
            return;
        }

        this.handleSendToQueue(res, Configs.BREED_HERO);
    }

    @WithSpan
    public void sendAscendHero(ISFSObject res) {
        if (res == null) {
            return;
        }

        this.handleSendToQueue(res, Configs.ASCEND_HERO);
    }

    @WithSpan
    private void handleDepositToken(TokenDto dto) {
        ISFSObject res = this.tokenAction(dto, CMD.InternalMessage.DEPOSIT_TOKEN);
        this.handleSendToQueue(res, Configs.DEPOSIT_TOKEN);
    }

    @WithSpan
    private void handleWithdrawToken(TokenDto dto) {
        ISFSObject res = this.tokenAction(dto, CMD.InternalMessage.WITHDRAW_TOKEN);
        this.handleSendToQueue(res, Configs.WITHDRAW_TOKEN);
    }

    @WithSpan
    private void handeAscendHero(HeroDto dto) {
        ISFSObject res = this.updateHeroInfo(dto, CMD.HttpCMD.CONFIRM_ASCEND_HERO);
        ISFSArray content = SFSArray.newFromJsonData(Utils.toJson(dto.getHeroes()));
        res.putSFSArray(Params.CONTENT, content);
        res.putText(Params.TXHASH, dto.getTxhash());
        this.handleSendToQueue(res, Configs.BURN_HERO);
    }

    @WithSpan
    private void handleClaimHero(HeroDto dto) {
        ISFSObject res = this.updateHeroInfo(dto, CMD.InternalMessage.VERIFY_MINT_NFT_HERO);
        this.handleSendToQueue(res, Configs.CLAIM_HERO);
    }

    @WithSpan
    private void handleSaleNft(MarketDto dto) {
        ISFSObject res = this.updateStatus(dto, 1);
        this.handleSendToQueue(res, Configs.SALE_NFT);
    }

    @WithSpan
    private void handleUnSaleNft(MarketDto dto) {
        ISFSObject res = this.updateStatus(dto, 0);
        this.handleSendToQueue(res, Configs.UN_SALE_NFT);
    }

    @WithSpan
    private void handleTransferHero(MarketDto dto) {
        try {
            TUserInfo userInfo = SDKGateAccount.getUserInfo(dto.getFrom());
            if (userInfo == null) {
                return;
            }

            String cmd = CMD.InternalMessage.TRANFER_NFT_HERO;
            String queue = Configs.BUY_NFT;
            if (dto.getPrice() <= -1) {
                cmd = CMD.InternalMessage.TRANSFER_HERO_FROM_WALLET;
                queue = Configs.TRANSFER_HERO;
            }
            ISFSObject params = new SFSObject();
            params.putLong(Params.UID, Long.parseLong(userInfo.userId));
            params.putText(Params.HASH, dto.getHash());
            params.putText(Params.TO, dto.getTo());
            params.putText(Params.SERVER_ID, userInfo.serverId);

            ISFSObject res = this.send(userInfo.serverId, cmd, params);
            ISFSObject data = new SFSObject();
            data.putText(Params.TOKEN_ID, dto.getTokenId());
            data.putText(Params.HASH, dto.getHash());
            data.putDouble(Params.PRICE, dto.getPrice());
            data.putText(Params.FROM, dto.getFrom());
            data.putText(Params.TO, dto.getTo());
            res.putSFSObject(Params.DATA, data);
            res.putText(Params.TXHASH, dto.getTxhash());
            this.handleSendToQueue(res, queue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @WithSpan
    private ISFSObject updateHeroInfo(HeroDto dto, String cmdId) {
        try {
            TUserInfo userInfo = SDKGateAccount.getUserInfo(dto.getWallet());
            if (userInfo == null) {
                return null;
            }

            ISFSObject params = new SFSObject();
            ISFSArray heroes = new SFSArray();
            params.putLong(Params.UID, Long.parseLong(userInfo.userId));
            params.putText(Params.TRANS_ID, dto.getTxhash());
            for (HeroInfo heroInfo : dto.getHeroes()) {
                ISFSObject hero = new SFSObject();
                hero.putText(Params.HASH, heroInfo.getHeroHash());
                hero.putText(Params.TOKEN, heroInfo.getTokenId());
                heroes.addSFSObject(hero);
            }

            params.putSFSArray(Params.LIST, heroes);
            ISFSObject res = this.send(userInfo.serverId, cmdId, params);
            System.out.println(cmdId + ": " + res.toJson());
            assert res != null;
            res.putText(Params.TXHASH, dto.getTxhash());
            res.putText(Params.FROM, dto.getWallet());
            return res;
        } catch (Exception e) {
            System.out.println(cmdId + " error: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    @WithSpan
    private ISFSObject tokenAction(TokenDto dto, String cmdId) {
        try {
            TUserInfo userInfo = SDKGateAccount.getUserInfo(dto.getWallet());
            if (userInfo == null) {
                return null;
            }

            System.out.println(dto.toString());
            ISFSObject params = new SFSObject();
            params.putLong(Params.UID, Long.parseLong(userInfo.userId));
            params.putText(Params.TRANS_ID, dto.getTxhash());
            params.putText(Params.MONEY_TYPE, dto.getMoneyType());
            params.putDouble(Params.MONEY, dto.getAmount());
            System.out.println("tokenAction " + params.toJson() );
            return this.send(userInfo.serverId, cmdId, params);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @WithSpan
    private ISFSObject updateStatus(MarketDto dto, int status) {
        ISFSObject res = new SFSObject();
        res.putShort(Params.ERROR_CODE, ServerConstant.ErrorCode.ERR_SYS);
        try {
            TUserInfo userInfo = SDKGateAccount.getUserInfo(dto.getFrom());
            if (userInfo == null) {
                return res;
            }

            ISFSObject params = new SFSObject();
            params.putLong(Params.UID, Long.parseLong(userInfo.userId));
            params.putText(Params.HASH, dto.getHash());
            String cmd = "";
            if (status == 0) {
                cmd = CMD.InternalMessage.UNLOCK_HERO;
            } else if (status == 1) {
                cmd = CMD.InternalMessage.LOCK_HERO;
            }

            if (cmd.isEmpty()) {
                return  res;
            }

            res = this.send(userInfo.serverId, cmd, params);
            ISFSObject data = new SFSObject();
            data.putText(Params.TOKEN_ID, dto.getTokenId());
            data.putText(Params.HASH, dto.getHash());
            data.putDouble(Params.PRICE, dto.getPrice());
            data.putText(Params.FROM, dto.getFrom());
            data.putText(Params.TO, dto.getTo());
            res.putSFSObject(Params.DATA, data);
            res.putText(Params.TXHASH, dto.getTxhash());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return res;
    }

    @WithSpan
    private ISFSObject send(String serverId, String cmd, ISFSObject params) {
        try {
            Zone zone = SmartFoxServer.getInstance()
                    .getZoneManager().getZoneByName(serverId);
            if (zone == null) {
                return null;
            }

            return (ISFSObject) zone
                    .getExtension()
                    .handleInternalMessage(cmd, params);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @WithSpan
    private void handleSendToQueue(ISFSObject res, String cmd) {
        if (res == null) {
            return;
        }

        res.putText(Params.CMD, cmd);

        String json = res.toJson();
        try {
            RabbitMQManager.getInstance().getChannel().basicPublish(Configs.EXCHANGE_NAME, Configs.MARKET_ROUTING_KEY, false, null, json.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
