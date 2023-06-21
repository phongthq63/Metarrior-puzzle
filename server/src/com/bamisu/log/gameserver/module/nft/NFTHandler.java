package com.bamisu.log.gameserver.module.nft;

import com.bamisu.gamelib.base.BaseExtension;
import com.bamisu.gamelib.base.ExtensionBaseClientRequestHandler;
import com.bamisu.gamelib.entities.*;
import com.bamisu.gamelib.item.ItemManager;
import com.bamisu.gamelib.item.define.ResourceType;
import com.bamisu.gamelib.item.entities.EquipDataVO;
import com.bamisu.gamelib.socialnetwork.ESocialNetwork;
import com.bamisu.gamelib.utils.UserUtils;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.gamelib.utils.extension.ExtensionUtility;
import com.bamisu.log.gameserver.datamodel.bag.UserBagModel;
import com.bamisu.log.gameserver.datamodel.hero.HeroSkillModel;
import com.bamisu.log.gameserver.datamodel.hero.entities.HeroModel;
import com.bamisu.log.gameserver.datamodel.nft.*;
import com.bamisu.log.gameserver.datamodel.nft.entities.HeroMintModel;
import com.bamisu.log.gameserver.datamodel.nft.entities.HeroUpstarBurn;
import com.bamisu.log.gameserver.datamodel.nft.entities.TokenTransactionModel;
import com.bamisu.log.gameserver.module.GameEvent.GameEventAPI;
import com.bamisu.log.gameserver.module.GameEvent.defind.EGameEvent;
import com.bamisu.log.gameserver.module.bag.BagManager;
import com.bamisu.log.gameserver.module.characters.CharactersConfigManager;
import com.bamisu.log.gameserver.module.characters.hero.entities.HeroVO;
import com.bamisu.log.gameserver.module.characters.summon.entities.HeroSummonVO;
import com.bamisu.log.gameserver.module.hero.HeroManager;
import com.bamisu.log.gameserver.module.hero.define.EHeroType;
import com.bamisu.log.gameserver.module.hero.define.ESummonID;
import com.bamisu.log.gameserver.module.hero.define.ESummonType;
import com.bamisu.log.gameserver.module.nft.cmd.rec.*;
import com.bamisu.log.gameserver.module.nft.cmd.send.*;
import com.bamisu.log.gameserver.module.nft.defind.ETokenBC;
import com.bamisu.log.gameserver.module.nft.entities.HeroMint;
import com.bamisu.log.gameserver.module.nft.exception.BreedException;
import com.bamisu.log.gameserver.module.sdkthriftclient.SDKGateAccount;
import com.bamisu.log.nft.entities.HeroToken;
import com.bamisu.log.nft.entities.WithdrawTransactionVO;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.exceptions.SFSException;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Quach Thanh Phong
 * On 2/12/2022 - 2:38 PM
 */
public class NFTHandler extends ExtensionBaseClientRequestHandler {
    Logger logger = Logger.getLogger(NFTHandler.class);
    public NFTHandler(BaseExtension extension) {
        super(extension);
        this.MODULE_ID = Params.Module.MODULE_NFT;
    }

    @WithSpan
    @Override
    protected void doClientRequest(int cmdId, User user, ISFSObject data) {
        switch (cmdId) {
            case CMD.CMD_GET_NFT_INFO:
                doGetNFTInfo(user, data);
                break;
            case CMD.CMD_VERIFY_MINT_HERO:
                doVerifyMindHero(user, data);
                break;
            case CMD.CMD_CLAIM_TOKEN_MINE:
                doClaimTokenMine(user, data);
                break;
            case CMD.CMD_GET_TOKEN_BLOCKCHAIN_INFO:
                doGetTokenBlockChainInfo(user, data);
                break;
            case CMD.CMD_REMOVE_MINT_NFT_HERO:
                doRemoveMintNFTHero(user, data);
                break;
            case CMD.CMD_VERIFY_CLAIM_TOKEN:
                doVerifyClaimToken(user, data);
                break;
            case CMD.CMD_VERIFY_BUY_TOKEN:
                doVerifyBuyToken(user, data);
                break;
            case CMD.CMD_RETURN_NFT_HERO_UP_STAR:
                doReturnNFTHeroUpStar(user, data);
                break;
            case CMD.CMD_VERIFY_UP_STAR_NFT_HERO:
                doVerifyUpStarNFTHero(user, data);
                break;
            case CMD.CMD_GET_LIST_HERO_BREED:
                this.getListHeroBreed(user);
                break;
            case CMD.CMD_GET_LIST_HERO_COUNTDOWN:
                this.getListHeroCountdown(user);
                break;
            case CMD.CMD_BREED:
                this.handleBreed(user, data);
                break;
            case CMD.CMD_RETURN_CLAIM_TOKEN:
                doReturnClaimToken(user, data);
                break;
            case CMD.CMD_GET_ASCEND_COUNTDOWN:
                doGetListHeroAscend(user);
                break;
        }
    }

    @WithSpan
    private void doGetListHeroAscend(User user) {
        long uid = extension.getUserManager().getUserModel(user).userID;
        SendListHeroAscendCountdown packet = this.doGetListHeroAscend(uid);
        send(packet, user);
    }

    @WithSpan
    private SendListHeroAscendCountdown doGetListHeroAscend(long uid) {
        Zone zone = getParentExtension().getParentZone();
        UserBurnHeroModel userBurnHeroModel = UserBurnHeroModel.copyFromDBtoObject(uid, zone);
        SendListHeroAscendCountdown packet = new SendListHeroAscendCountdown();
        packet.zone = zone;
        packet.model = userBurnHeroModel;
        return packet;
    }

    @WithSpan
    public SFSObject doGetListHeroAscend(ISFSObject data) {
        long uid = data.getLong(Params.UID);
        SendListHeroAscendCountdown packet = this.doGetListHeroAscend(uid);
        packet.isHttp = true;
        packet.packData();
        return (SFSObject) packet.getData();
    }

    @WithSpan
    public SFSObject getListHeroCountdown(ISFSObject data) {
        long uid = data.getLong("uid");
        Zone zone = extension.getParentZone();
        UserMintHeroModel userMintHeroModel = NFTManager.getInstance().getUserMintHeroModel(uid, getParentExtension().getParentZone());
        SendListHeroBreed packet = new SendListHeroBreed(CMD.CMD_GET_LIST_HERO_COUNTDOWN);
        List<HeroModel> heroModels = new ArrayList<>();
        Map<String, HeroModel> parentMap = new HashMap<>();
        for (HeroMintModel heroMintModel : userMintHeroModel.listHeroMint) {
            heroModels.addAll(
                    heroMintModel.listHeroMint.stream()
                            .filter(model -> model.timeClaim != null)
                            .collect(Collectors.toList())
            );
        }

        packet.models = heroModels;
        packet.uid = uid;
        packet.zone = zone;
        packet.packData();
        return (SFSObject) packet.getData();
    }

    @WithSpan
    public SFSObject handleBreed(ISFSObject data) {
        long uid = data.getLong("uid");
        RecBreed packet = new RecBreed(data);

        packet.zone = getParentExtension().getParentZone();
        packet.uid = uid;
        SendBreedHero sendCmd = new SendBreedHero();
        try {
            sendCmd.models = NFTManager.getInstance().doBreed(packet);
        } catch (BreedException e) {
            sendCmd.setErrorCode(e.error);
        }

        return (SFSObject) sendCmd.getData();
    }

    @WithSpan
    private void handleBreed(User user, ISFSObject data) {
        Long uid = extension.getUserManager().getUserModel(user).userID;

        RecBreed packet = new RecBreed(data);
        packet.uid = uid;
        packet.zone = getParentExtension().getParentZone();

//        //check hero use to energy
//        List<String> hashHeros = Arrays.asList(packet.fatherHash, packet.motherHash);
//        UserBagModel userBagModel = BagManager.getInstance().getUserBagModel(uid, getParentExtension().getParentZone());
//        if (userBagModel.energy.heros.stream().anyMatch(hashHeros::contains)) {
//            SendBreedHero sendCmd = new SendBreedHero(ServerConstant.ErrorCode.ERR_HERO_USING_CHANGE_ENERY);
//            send(sendCmd, user);
//            return;
//        }

        SendBreedHero sendCmd = new SendBreedHero();
        try {
            sendCmd.models = NFTManager.getInstance().doBreed(packet);
        } catch (BreedException e) {
            sendCmd.setErrorCode(e.error);
        }

        SendNotifyMintHero sendNotify = new SendNotifyMintHero(packet.uid);
        send(sendCmd, user);
        send(sendNotify, user);
    }

    @Override
    protected void doServerEvent(SFSEventType type, ISFSEvent event) throws SFSException {

    }

    @Override
    protected void initHandlerClientRequest() {
        this.extension.addRequestHandler(Params.Module.MODULE_NFT, this);
    }

    @Override
    protected void initHandlerServerEvent() {
        this.extension.addServerHandler(Params.Module.MODULE_NFT, this);
    }



    /*----------------------------------------------------------------------------------------------------------------*/
    /**
     *
     * @param user
     * @param data
     */
    @WithSpan
    private void doGetNFTInfo(User user, ISFSObject data) {
        long uid = extension.getUserManager().getUserModel(user).userID;
        UserTokenModel userTokenModel = NFTManager.getInstance().getUserMineTokenModel(uid, getParentExtension().getParentZone());
        UserMintHeroModel userMintHeroModel = NFTManager.getInstance().getUserMintHeroModel(uid, getParentExtension().getParentZone());

        SendGetNFTInfo objPut = new SendGetNFTInfo();
        objPut.mapToken = userTokenModel.getAllToken();
        objPut.countHeroMint = userMintHeroModel.listHeroMine.size();
        send(objPut, user);
    }

    /**
     *
     * @param user
     * @param data
     */
    @WithSpan
    private void doVerifyMindHero(User user, ISFSObject data) {
        long uid = extension.getUserManager().getUserModel(user).userID;
//        SendVerifyMindHero sendCmd = this.handleVerifySumHero(uid, data);
//        send(sendCmd, user);

        RecVerifyMintHero objGet = new RecVerifyMintHero(data);
        if (objGet.transactionHash == null || objGet.transactionHash.isEmpty() || objGet.tokenId.isEmpty()) {
            SendVerifyMindHero objPut = new SendVerifyMindHero(ServerConstant.ErrorCode.ERR_INVALID_VALUE);
            send(objPut, user);
            return;
        }

        // Get data from blockchain
        List<HeroToken> listHeroToken = NFTManager.getInstance().getHashHeroByTransactionHashMint(objGet.transactionHash, objGet.tokenId);
        if (listHeroToken.isEmpty()) {
            SendVerifyMindHero objPut = new SendVerifyMindHero(ServerConstant.ErrorCode.ERR_CALL_NFT);
            send(objPut, user);
            return;
        }

        // Tu contract lay hash hero
        if (listHeroToken.size() != objGet.tokenId.size()) {
            SendVerifyMindHero objPut = new SendVerifyMindHero(ServerConstant.ErrorCode.ERR_INVALID_TOKEN_MINT_HERO);
            send(objPut, user);
            return;
        }

        List<String> listHashHero = listHeroToken.stream().map(index -> index.hashHero).collect(Collectors.toList());
        UserMintHeroModel userMintHeroModel = NFTManager.getInstance().getUserMintHeroModel(uid, getParentExtension().getParentZone());
        HeroMintModel heroMintModel = NFTManager.getInstance().getHeroMintedModel(userMintHeroModel, listHashHero, getParentExtension().getParentZone());
        // Get hero mint in db
        List<HeroModel> listHeroModel = NFTManager.getInstance().getListHeroModelMinted(
                userMintHeroModel,
                listHashHero,
                getParentExtension().getParentZone());
        if(listHeroModel.size() != listHeroToken.size()) {
            SendVerifyMindHero objPut = new SendVerifyMindHero(ServerConstant.ErrorCode.ERR_INVALID_TRANSACTION_MINT_HERO);
            send(objPut, user);
            return;
        }

        //Add hero vao bag
        if(!HeroManager.getInstance().addUserAllHeroModel(
                uid,
                listHeroModel,
                getParentExtension().getParentZone(),
                false,
                null)) {
            SendVerifyMindHero objPut = new SendVerifyMindHero(ServerConstant.ErrorCode.ERR_SYS);
            send(objPut, user);
            return;
        }

        //Save info nft hero
        for (HeroToken heroToken : listHeroToken) {
            NFTManager.getInstance().createHeroTokenModel(heroToken.hashHero, objGet.transactionHash, heroToken.tokenId, getParentExtension().getParentZone());
        }

        //Tang diem tich luy summon Model
        if (heroMintModel.extraData != null) {
            HeroManager.SummonManager.getInstance().updateBonusUserSummonHeroModel(uid, heroMintModel.sum * CharactersConfigManager.getInstance().getBonusPointSummonConfig(heroMintModel.extraData), getParentExtension().getParentZone());
        }

        //Delete hero trong hang cho
        if (!NFTManager.getInstance().deleteHeroModelMinted(uid, listHashHero, getParentExtension().getParentZone())) {
            SendVerifyMindHero objPut = new SendVerifyMindHero(ServerConstant.ErrorCode.ERR_SYS);
            send(objPut, user);
            return;
        }

        try {
            Map<String,Object> extraData = Utils.fromJson(heroMintModel.extraData, HashMap.class);
            System.out.println("\n" + Utils.toJson(extraData) + "\n");
            if (extraData != null && extraData.get(Params.ACTION).toString().equals("Summon")) {
                System.out.println("\n" + Utils.toJson(extraData) + "\n");
                //Event
                Map<String, Object> dataEvent = new HashMap<>();
                dataEvent.put(Params.COUNT, extraData.get(Params.COUNT));
                dataEvent.put(Params.STAR, extraData.get(Params.STAR));
                GameEventAPI.ariseGameEvent(EGameEvent.SUMMON_TAVERN, uid, dataEvent, getParentExtension().getParentZone());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        SendVerifyMindHero objPut = new SendVerifyMindHero();
        objPut.listHeroToken = listHeroToken;
        send(objPut, user);
    }

    /**
     *
     * @param data
     */
    @WithSpan
    private SendClaimTokenMine doClaimTokenMine(long uid, ISFSObject data) {
        RecClaimTokenMine objGet = new RecClaimTokenMine(data);
        // chi cho rut busd
        ETokenBC token = ETokenBC.BUSD;
        if (!objGet.tokenName.equalsIgnoreCase(token.getId())) {
            return new SendClaimTokenMine(ServerConstant.ErrorCode.ERR_SYS);
        }

        Zone zone = getParentExtension().getParentZone();

        WithdrawFeeConfig config = ItemManager.getInstance().getWithdrawFeeConfig();
        FeeConfig feeConfig = null;
        for (FeeConfig cf : config.getConfig()) {
            if (cf.getToken().equalsIgnoreCase(objGet.tokenName)) {
                feeConfig = cf;
                break;
            }
        }

        // Khong co cau hinh
        if (feeConfig == null) {
            return new SendClaimTokenMine(ServerConstant.ErrorCode.ERR_SYS);
        }

        UserTokenModel userTokenModel = UserTokenModel.copyFromDBtoObject(uid, zone);
        double totalToken = (double) userTokenModel.readToken(objGet.tokenName);
        LastWithdrawModel withdrawModel = LastWithdrawModel.load(uid, zone);
        double totalWithdraw = withdrawModel.total + objGet.count;
        // khong du dieu kien de rut
        if (totalWithdraw > feeConfig.getMax() || objGet.count < feeConfig.getMin() || totalToken < objGet.count) {
            return new SendClaimTokenMine(ServerConstant.ErrorCode.ERR_SYS);
        }

        // tru tien user
        List<TokenResourcePackage> resourcePackages = Collections.singletonList(new TokenResourcePackage(objGet.tokenName, -objGet.count));
        if (!NFTManager.getInstance().updateToken(
                uid,
                resourcePackages,
                UserUtils.TransactionType.CLAIM_TOKEN_BC,
                zone).isSuccess()) {
            return new SendClaimTokenMine(ServerConstant.ErrorCode.ERR_SYS);
        }

        //Sign data
        double fee = objGet.count * feeConfig.getFee();
        double money = objGet.count - fee;
//        String signature = NFTManager.getInstance().signData(uid, token, money, zone);
//        if (signature.isEmpty()) {
//            return new SendClaimTokenMine(ServerConstant.ErrorCode.ERR_SIGN_DATA);
//        }

        String transactionId = NFTManager.getInstance().createTransaction();
        UserTokenClaimModel userTokenClaimModel = NFTManager.getInstance().getUserTokenClaimModel(uid, zone);

        List<TokenResourcePackage> tokenClaims = Collections.singletonList(new TokenResourcePackage(objGet.tokenName, objGet.count));
        if (!userTokenClaimModel.saveTransaction(transactionId, TokenTransactionModel.create("claim", transactionId, tokenClaims, fee), zone)) {
            return new SendClaimTokenMine(ServerConstant.ErrorCode.ERR_SYS);
        }

        WithdrawTransactionVO transactionVO = new WithdrawTransactionVO(token.getId(), transactionId, objGet.count);
        withdrawModel.transactions.add(transactionVO);
        withdrawModel.total += objGet.count;
        withdrawModel.saveToDB(zone);
        SendClaimTokenMine objPut = new SendClaimTokenMine();
//        objPut.signature = signature;
        objPut.token = objGet.tokenName;
        objPut.transaction = transactionId;
        objPut.count = money;
        return objPut;
    }

    @WithSpan
    private void doClaimTokenMine(User user, ISFSObject data) {
        long uid = extension.getUserManager().getUserModel(user).userID;
        SendClaimTokenMine packet = this.doClaimTokenMine(uid, data);
        send(packet, user);
    }

    @WithSpan
    public SFSObject doClaimTokenMine(ISFSObject data) {
        long uid = data.getLong(Params.UID);
        SendClaimTokenMine packet = this.doClaimTokenMine(uid, data);
        packet.packData();
        return (SFSObject) packet.getData();
    }

    /**
     *
     * @param user
     * @param data
     */
    @WithSpan
    private void doGetTokenBlockChainInfo(User user, ISFSObject data) {
        long uid = extension.getUserManager().getUserModel(user).userID;
        UserTokenModel userTokenModel = NFTManager.getInstance().getUserMineTokenModel(uid, getParentExtension().getParentZone());

        SendGetTokenBlockChainInfo objPut = new SendGetTokenBlockChainInfo();
        send(objPut, user);
    }

    /**
     *
     * @param user
     * @param data
     */
    @WithSpan
    private void doRemoveMintNFTHero(User user, ISFSObject data) {
        /**long uid = extension.getUserManager().getUserModel(user).userID;

        RecRemoveMintNFTHero objGet = new RecRemoveMintNFTHero(data);
        //Check exsist
        List<HeroModel> listHero = NFTManager.getInstance().getListHeroModelMinted(uid, objGet.listHashHero, getParentExtension().getParentZone());
        if (listHero.size() != objGet.listHashHero.size()) {
            SendRemoveMintNFTHero objPut = new SendRemoveMintNFTHero(ServerConstant.ErrorCode.ERR_NOT_EXSIST_CHARACTER);
            send(objPut, user);
            return;
        }

        //Remove
        List<ResourcePackage> resourceCreate = NFTManager.getInstance().removeHeroModelMinted(uid, objGet.listHashHero, getParentExtension().getParentZone());
        if (resourceCreate == null) {
            SendRemoveMintNFTHero objPut = new SendRemoveMintNFTHero(ServerConstant.ErrorCode.ERR_SYS);
            send(objPut, user);
            return;
        }

        // Tra lai item vao tui
        if (!BagManager.getInstance().addItemToDB(
                resourceCreate.stream().map(res -> new ResourcePackage(res.id, -res.amount)).collect(Collectors.toList()),
                uid,
                getParentExtension().getParentZone(),
                UserUtils.TransactionType.RETURN_MINT_HERO)) {
            SendRemoveMintNFTHero objPut = new SendRemoveMintNFTHero(ServerConstant.ErrorCode.ERR_SYS);
            send(objPut, user);
            return;
        }

        SendRemoveMintNFTHero objPut = new SendRemoveMintNFTHero();
        send(objPut, user);
         */
    }

    @WithSpan
    private SendVerifyClaimToken doVerifyClaimToken(long uid, ISFSObject data) {
        RecVerifyClaimToken objGet = new RecVerifyClaimToken(data);
        String txhash = objGet.transactionHash;
        String transactionId = objGet.id;
        //Check da tung su dung
        if (NFTManager.getInstance().isUsedTransactionHash(txhash)) {
            return new SendVerifyClaimToken(ServerConstant.ErrorCode.ERR_USED_TRANSACTION_TRANFER_TOKEN, txhash);
        }

        UserTokenClaimModel userTokenClaimModel = NFTManager.getInstance().getUserTokenClaimModel(uid, getParentExtension().getParentZone());
        if (!userTokenClaimModel.clearTransaction(objGet.id, getParentExtension().getParentZone())) {
            return new SendVerifyClaimToken(ServerConstant.ErrorCode.ERR_NOT_EXSIST_TRANSACTION_TRANFER_TOKEN, txhash);
        }

        //Save
        if (!NFTManager.getInstance().saveTransactionHash(uid, objGet.transactionHash, objGet.count)) {
            return new SendVerifyClaimToken(ServerConstant.ErrorCode.ERR_SYS, txhash);
        }

        LastWithdrawModel withdrawModel = LastWithdrawModel.load(uid, getParentExtension().getParentZone());
        boolean isChange = false;
        for (WithdrawTransactionVO vo : withdrawModel.transactions) {
            if (vo.getTransactionId().equalsIgnoreCase(transactionId)) {
                vo.setTxhash(txhash);
                vo.setSuccess(true);
                vo.updateUpdated();
                isChange = true;
                break;
            }
        }

        if (isChange) {
            withdrawModel.saveToDB(getParentExtension().getParentZone());
        }

        return new SendVerifyClaimToken(ServerConstant.ErrorCode.NONE, txhash);
    }
    /**
     *
     * @param user
     * @param data
     */
    @WithSpan
    private void doVerifyClaimToken(User user, ISFSObject data) {
        long uid = extension.getUserManager().getUserModel(user).userID;
        send(this.doVerifyClaimToken(uid, data), user);
    }

    @WithSpan
    public SFSObject doVerifyClaimToken(ISFSObject data) {
        long uid = data.getLong(Params.UID);
        SendVerifyClaimToken packet = this.doVerifyClaimToken(uid, data);
        packet.packData();
        return (SFSObject) packet.getData();
    }

    /**
     *
     * @param user
     * @param data
     */
    @WithSpan
    private void doVerifyBuyToken(User user, ISFSObject data) {
        long uid = extension.getUserManager().getUserModel(user).userID;
        short ec = this.depositToken(uid, data);
        SendVerifyBuyToken objPut = new SendVerifyBuyToken(ec);
        send(objPut, user);
    }

    /**
     * start reject ascend hero
     * @param data
     */
    @WithSpan
    private SendReturnNFTHeroUpStar doReturnNFTHeroUpStar(long uid, ISFSObject data) {
        RecReturnNFTHeroUpStar objGet = new RecReturnNFTHeroUpStar(data);

        HeroUpstarBurn heroUpstarBurn = NFTManager.getInstance().deleteHeroUpstar(uid, objGet.hashHero, getParentExtension().getParentZone());
        if (heroUpstarBurn == null) {
            return new SendReturnNFTHeroUpStar(ServerConstant.ErrorCode.ERR_NOT_EXSIST_CHARACTER);
        }

        List<HeroModel> listHeroModel = new ArrayList<>();
        listHeroModel.add(heroUpstarBurn.heroModel);
        listHeroModel.addAll(heroUpstarBurn.listFission);
        //Add hero vao bag
        if (!HeroManager.getInstance().addUserAllHeroModel(uid, listHeroModel, getParentExtension().getParentZone(), false, null)) {
            return new SendReturnNFTHeroUpStar(ServerConstant.ErrorCode.ERR_SYS);
        }
        //Tra lai tai nguyen
        if(!BagManager.getInstance().addItemToDB(heroUpstarBurn.listRes.stream().peek(obj -> obj.amount = -obj.amount).collect(Collectors.toList()), uid, getParentExtension().getParentZone(), UserUtils.TransactionType.UPGRADE_HERO)){
            return new SendReturnNFTHeroUpStar(ServerConstant.ErrorCode.ERR_SYS);

        }

        return new SendReturnNFTHeroUpStar();
    }

    @WithSpan
    private void doReturnNFTHeroUpStar(User user, ISFSObject data) {
        long uid = extension.getUserManager().getUserModel(user).userID;
        send(this.doReturnNFTHeroUpStar(uid, data), user);
    }

    @WithSpan
    public SFSObject doReturnNFTHeroUpStar(ISFSObject data) {
        long uid = data.getLong(Params.UID);
        SendReturnNFTHeroUpStar packet = this.doReturnNFTHeroUpStar(uid, data);
        packet.packData();
        return (SFSObject) packet.getData();
    }
    // End reject ascend

    /**
     *
        start verify ascend
     * @param data
     */
    @WithSpan
    private SendVerifyUpStarNFTHero doVerifyUpStarNFTHero(long uid, ISFSObject data) {
        RecVerifyUpStarNFTHero objGet = new RecVerifyUpStarNFTHero(data);
        if (objGet.hashHero == null || objGet.hashHero.isEmpty()) {
            return new SendVerifyUpStarNFTHero(ServerConstant.ErrorCode.ERR_INVALID_VALUE);
        }

        //Check NFT burn transaction
        if (!objGet.transactionHash.isEmpty()) {
            if (objGet.tokenId.size() > 0) {
                List<HeroToken> listHeroToken = NFTManager.getInstance().getHashHeroByTransactionHashBurn(objGet.transactionHash, objGet.tokenId);
                if (listHeroToken.size() != objGet.tokenId.size() || listHeroToken.isEmpty()) {
                    return new SendVerifyUpStarNFTHero(ServerConstant.ErrorCode.ERR_INVALID_TRANSACTION_BURN_HERO);
                }
            }

        }

        HeroUpstarBurn heroUpstarBurn = NFTManager.getInstance().getHeroUpstar(uid, objGet.hashHero, getParentExtension().getParentZone());
        if (heroUpstarBurn == null) {
            return new SendVerifyUpStarNFTHero(ServerConstant.ErrorCode.ERR_NOT_EXSIST_CHARACTER);
        }

        // Chưa đủ thời gian claim
        long now = Utils.getTimestampInSecond();
        if (now < heroUpstarBurn.timer) {
            return new SendVerifyUpStarNFTHero(ServerConstant.ErrorCode.ERR_COUNTDOWN);
        }

        //Add hero vao bag
        HeroModel heroModel = HeroModel.createByHeroModel(heroUpstarBurn.heroModel);
        if (!HeroManager.getInstance().addUserAllHeroModel(uid, Collections.singletonList(heroModel), getParentExtension().getParentZone(), false, null)) {
            return new SendVerifyUpStarNFTHero(ServerConstant.ErrorCode.ERR_SYS);
        }

        //Nang star hero
        if (!HeroManager.getInstance().upStarHeroModel(uid, heroUpstarBurn.heroModel.hash, getParentExtension().getParentZone())) {
            return new SendVerifyUpStarNFTHero(ServerConstant.ErrorCode.ERR_SYS);
        }

        //Tra lai nguyen lieu da nang cap hero
        List<ResourcePackage> listResource = new ArrayList<>();
        heroUpstarBurn.listFission.forEach(obj -> listResource.addAll(obj.readResourceResetHeroModel()));
        List<EquipDataVO> listEquipData = HeroManager.getInstance().getAllEquipmentHero(heroUpstarBurn.listFission);

        //Add vao bag
        if (!BagManager.getInstance().addNewWeapon(uid, getParentExtension().getParentZone(), listEquipData)) {
            return new SendVerifyUpStarNFTHero(ServerConstant.ErrorCode.ERR_SYS);
        }
        if(!BagManager.getInstance().addItemToDB(listResource, uid, getParentExtension().getParentZone(), UserUtils.TransactionType.UPGRADE_HERO)){
            return new SendVerifyUpStarNFTHero(ServerConstant.ErrorCode.ERR_SYS);
        }

        //Save transaction
        if (objGet.tokenId.size() > 0) {
            TransactionUpstarHeroModel.create(objGet.transactionHash, heroUpstarBurn.heroModel, heroUpstarBurn.listFission, getParentExtension().getParentZone());
        }

        NFTManager.getInstance().deleteHeroUpstar(uid, heroUpstarBurn.heroModel.hash, getParentExtension().getParentZone());
        SendNotifyMintHero packet = new SendNotifyMintHero(uid);
        User user = ExtensionUtility.getInstance().getUserById(uid);
        if (user != null) {
            send(packet, user);
        }
        SendVerifyUpStarNFTHero objPut = new SendVerifyUpStarNFTHero();
        objPut.listResource = listResource;
        objPut.listEquipData = listEquipData;
        return objPut;
    }

    @WithSpan
    private void doVerifyUpStarNFTHero(User user, ISFSObject data) {
        long uid = extension.getUserManager().getUserModel(user).userID;

        send(this.doVerifyUpStarNFTHero(uid, data), user);
    }

    @WithSpan
    public ISFSObject doVerifyUpStarNFTHero(ISFSObject req) {
        long uid = req.getLong(Params.UID);
        String txhash = req.getText(Params.TRANS_ID);
        ISFSArray heroes = req.getSFSArray(Params.LIST);
        ISFSObject res = new SFSObject();
        Zone zone = getParentExtension().getParentZone();
        res.putShort(Params.ERROR_CODE, ServerConstant.ErrorCode.NONE);
        List<String> listHash = new ArrayList<>();

        for (int i = 0; i < heroes.size(); i++) {
            ISFSObject hero = heroes.getSFSObject(i);
            String hash = hero.getText(Params.HASH);
            listHash.add(hash);
        }

        HeroUpstarBurn heroUpstarBurn = null;
        UserBurnHeroModel userBurnHeroModel = UserBurnHeroModel.copyFromDBtoObject(uid, zone);
        for (Map.Entry<String, HeroUpstarBurn> entry : userBurnHeroModel.mapUpstar.entrySet()) {
            for (HeroModel heroModelBurn : entry.getValue().listFission) {
                if (listHash.contains(heroModelBurn.hash)) {
                    heroUpstarBurn = entry.getValue();
                    break;
                }
            }
        }

        if (heroUpstarBurn == null) {
            return res;
        }

        // Chưa đủ thời gian claim
        long now = Utils.getTimestampInSecond();
        if (now < heroUpstarBurn.timer) {
            heroUpstarBurn.isRequestBurn = true;
            userBurnHeroModel.saveToDB(zone);
            return res;
        }

        //Add hero vao bag
        HeroModel heroModel = HeroModel.createByHeroModel(heroUpstarBurn.heroModel);
        if (!HeroManager.getInstance().addUserAllHeroModel(uid, Collections.singletonList(heroModel), zone, false, null)) {
            res.putShort(Params.ERROR_CODE, ServerConstant.ErrorCode.ERR_SYS);
            return res;
        }

        //Nang star hero
        if (!HeroManager.getInstance().upStarHeroModel(uid, heroUpstarBurn.heroModel.hash, zone)) {
            res.putShort(Params.ERROR_CODE, ServerConstant.ErrorCode.ERR_SYS);
            return res;
        }

        //Tra lai nguyen lieu da nang cap hero
        List<ResourcePackage> listResource = new ArrayList<>();
        heroUpstarBurn.listFission.forEach(obj -> listResource.addAll(obj.readResourceResetHeroModel()));
        List<EquipDataVO> listEquipData = HeroManager.getInstance().getAllEquipmentHero(heroUpstarBurn.listFission);

        //Add vao bag
        if (!BagManager.getInstance().addNewWeapon(uid, zone, listEquipData)) {
            res.putShort(Params.ERROR_CODE, ServerConstant.ErrorCode.ERR_SYS);
            return res;
        }
        if(!BagManager.getInstance().addItemToDB(listResource, uid, zone, UserUtils.TransactionType.UPGRADE_HERO)){
            res.putShort(Params.ERROR_CODE, ServerConstant.ErrorCode.ERR_SYS);
            return res;
        }

        //Save transaction
        TransactionUpstarHeroModel.create(txhash, heroUpstarBurn.heroModel, heroUpstarBurn.listFission, zone);

        NFTManager.getInstance().deleteHeroUpstar(uid, heroUpstarBurn.heroModel.hash, zone);
        SendNotifyMintHero packet = new SendNotifyMintHero(uid);
        User user = ExtensionUtility.getInstance().getUserById(uid);
        if (user != null) {
            send(packet, user);
        }

        HeroTokenModel heroTokenModel = HeroTokenModel.copyFromDBtoObject(heroUpstarBurn.heroModel.hash, zone);
        res.putText(Params.TO, heroTokenModel.tokenId);
        return res;
    }

    /**
     * list of hero can be breeding
     * @param user
     */
    @WithSpan
    private void getListHeroBreed(User user) {
        SendListHeroBreed packet = new SendListHeroBreed();
        long uid = extension.getUserManager().getUserModel(user).userID;
        List<HeroModel> listHeroModel = HeroManager.getInstance().getAllHeroModel(uid, getParentExtension().getParentZone());
        Zone zone = getParentExtension().getParentZone();
        packet.models = listHeroModel.stream().
                map(HeroModel::createByHeroModel).
                filter(model -> model.type == EHeroType.NFT.getId() && model.isBreeding == false && model.breed < model.maxBreed).
                collect(Collectors.toList());
        packet.zone = zone;
            send(packet, user);
    }

    @WithSpan
    public SFSObject getListHeroBreed(ISFSObject data) {
        long uid = data.getLong("uid");
        SendListHeroBreed packet = new SendListHeroBreed();
        List<HeroModel> listHeroModel = HeroManager.getInstance().getAllHeroModel(uid, getParentExtension().getParentZone());
        packet.zone = getParentExtension().getParentZone();
        packet.models = listHeroModel.stream().
                map(HeroModel::createByHeroModel).
                filter(model -> model.type == EHeroType.NFT.getId() && model.isBreeding == false && model.breed < model.maxBreed).
                collect(Collectors.toList());
        packet.packData();
        return (SFSObject) packet.getData();
    }

    /**
     * list of hero waiting for claim
     * @param user
     */
    @WithSpan
    private void getListHeroCountdown(User user) {
        long uid = extension.getUserManager().getUserModel(user).userID;
        Zone zone = getParentExtension().getParentZone();
        UserMintHeroModel userMintHeroModel = NFTManager.getInstance().getUserMintHeroModel(uid, getParentExtension().getParentZone());
        SendListHeroBreed packet = new SendListHeroBreed(CMD.CMD_GET_LIST_HERO_COUNTDOWN);
        List<HeroModel> heroModels = new ArrayList<>();
        for (HeroMintModel heroMintModel : userMintHeroModel.listHeroMint) {
            heroModels.addAll(
                    heroMintModel.listHeroMint.stream()
                            .filter(model -> model.timeClaim != null)
                            .collect(Collectors.toList())
            );
        }

        packet.models = heroModels;
        packet.uid = uid;
        packet.zone = zone;
        send(packet, user);

    }

    @WithSpan
    public SFSObject listHeroCountdown(ISFSObject data) {
        long uid = data.getLong(Params.UID);
        Zone zone = getParentExtension().getParentZone();
        UserMintHeroModel userMintHeroModel = NFTManager.getInstance().getUserMintHeroModel(uid, zone);
        SFSObject res = new SFSObject();
        SFSArray lst = new SFSArray();
        res.putInt(Params.ERROR_CODE, 0);
        res.putSFSArray(Params.LIST, lst);
        long now = System.currentTimeMillis() / 1000;
        for (HeroMintModel heroMintModel : userMintHeroModel.listHeroMint) {
            for (HeroModel heroModel : heroMintModel.listHeroMint) {
                if (heroModel.timeClaim == null) {
                    continue;
                }

                SFSObject hero = new SFSObject();
                String hash = "";
                if (now >= heroModel.timeClaim) {
                    hash = heroModel.hash;
                }

                hero.putText(Params.HASH, hash);
                hero.putLong(Params.TIME, heroModel.timeClaim);
                lst.addSFSObject(hero);
            }
        }

        return res;
    }

    @WithSpan
    private short depositToken(long uid, ISFSObject data) {
        RecVerifyBuyToken objGet = new RecVerifyBuyToken(data);
        //Check da tung su dung
        if (NFTManager.getInstance().isUsedTransactionHash(objGet.transactionHash)) {
            return ServerConstant.ErrorCode.ERR_USED_TRANSACTION_TRANFER_TOKEN;
        }

        ETokenBC token = ETokenBC.fromId(objGet.name);
        if (token == null) {
            return ServerConstant.ErrorCode.ERR_INVALID_TOKEN;
        }

        //Check in blockchain
        if (!NFTManager.getInstance().isValidTransactionBuyToken(uid, objGet.transactionHash, token, objGet.count, getParentExtension().getParentZone())) {
            return ServerConstant.ErrorCode.ERR_INVALID_TRANSACTION_TRANFER_TOKEN;
        }

        boolean isSuccess = NFTManager.getInstance().updateToken(
                uid,
                Collections.singletonList(new TokenResourcePackage(objGet.name, objGet.count)),
                UserUtils.TransactionType.BUY_TOKEN_BC,
                getParentExtension().getParentZone()).isSuccess();
        if (!isSuccess) {
            return ServerConstant.ErrorCode.ERR_SYS;
        }

        //Save
        if (!NFTManager.getInstance().saveTransactionHash(uid, objGet.transactionHash, objGet.count)) {
            return ServerConstant.ErrorCode.ERR_SYS;
        }

        return ServerConstant.ErrorCode.NONE;
    }

    @WithSpan
    private SendVerifyMindHero handleVerifySumHero(long uid, ISFSObject data) {

        RecVerifyMintHero objGet = new RecVerifyMintHero(data);
        if (objGet.transactionHash == null || objGet.transactionHash.isEmpty() || objGet.tokenId.isEmpty()) {
            return new SendVerifyMindHero(ServerConstant.ErrorCode.ERR_INVALID_VALUE);
        }

        // Get data from blockchain
        List<HeroToken> listHeroToken = NFTManager.getInstance().getHashHeroByTransactionHashMint(objGet.transactionHash, objGet.tokenId);
        if (listHeroToken.isEmpty()) {
            return new SendVerifyMindHero(ServerConstant.ErrorCode.ERR_CALL_NFT);
        }

        // Tu contract lay hash hero
        if (listHeroToken.size() != objGet.tokenId.size()) {
            return new SendVerifyMindHero(ServerConstant.ErrorCode.ERR_INVALID_TOKEN_MINT_HERO);
        }

        List<String> listHashHero = listHeroToken.stream().map(index -> index.hashHero).collect(Collectors.toList());
        UserMintHeroModel userMintHeroModel = NFTManager.getInstance().getUserMintHeroModel(uid, getParentExtension().getParentZone());
        HeroMintModel heroMintModel = NFTManager.getInstance().getHeroMintedModel(userMintHeroModel, listHashHero, getParentExtension().getParentZone());
        // Get hero mint in db
        List<HeroModel> listHeroModel = NFTManager.getInstance().getListHeroModelMinted(
                userMintHeroModel,
                listHashHero,
                getParentExtension().getParentZone());
        if(listHeroModel.size() != listHeroToken.size()) {
            return new SendVerifyMindHero(ServerConstant.ErrorCode.ERR_INVALID_TRANSACTION_MINT_HERO);
        }

        //Add hero vao bag
        if(!HeroManager.getInstance().addUserAllHeroModel(
                uid,
                listHeroModel,
                getParentExtension().getParentZone(),
                false,
                null)) {
            return new SendVerifyMindHero(ServerConstant.ErrorCode.ERR_SYS);
        }

        //Save info nft hero
        for (HeroToken heroToken : listHeroToken) {
            NFTManager.getInstance().createHeroTokenModel(heroToken.hashHero, objGet.transactionHash, heroToken.tokenId, getParentExtension().getParentZone());
        }

        //Tang diem tich luy summon Model
        if (heroMintModel.extraData != null) {
            HeroManager.SummonManager.getInstance().updateBonusUserSummonHeroModel(uid, heroMintModel.sum * CharactersConfigManager.getInstance().getBonusPointSummonConfig(heroMintModel.extraData), getParentExtension().getParentZone());
        }

        //Delete hero trong hang cho
        if (!NFTManager.getInstance().deleteHeroModelMinted(uid, listHashHero, getParentExtension().getParentZone())) {
            return new SendVerifyMindHero(ServerConstant.ErrorCode.ERR_SYS);
        }

        SendVerifyMindHero objPut = new SendVerifyMindHero();
        objPut.listHeroToken = listHeroToken;
        return objPut;
    }

    @WithSpan
    private SendReturnClaimToken doReturnClaimToken(long uid, ISFSObject data) {
        RecReturnClaimToken objGet = new RecReturnClaimToken(data);
        if (objGet.transactionId == null || objGet.transactionId.isEmpty()) {
            return new SendReturnClaimToken(ServerConstant.ErrorCode.ERR_INVALID_TRANSACTION_TRANFER_TOKEN);
        }

        UserTokenClaimModel userTokenClaimModel = NFTManager.getInstance().getUserTokenClaimModel(uid, getParentExtension().getParentZone());
        TokenTransactionModel tokenTransactionModel = userTokenClaimModel.getTokenTransactionModel(objGet.transactionId);
        if (tokenTransactionModel == null) {
            return new SendReturnClaimToken(ServerConstant.ErrorCode.ERR_NOT_EXSIST_TRANSACTION_TRANFER_TOKEN);
        }

        if (!userTokenClaimModel.clearTransaction(objGet.transactionId, getParentExtension().getParentZone())) {
            return new SendReturnClaimToken(ServerConstant.ErrorCode.ERR_SYS);
        }

        //Update db
        if (!NFTManager.getInstance().updateToken(
                uid,
                tokenTransactionModel.tokens,
                UserUtils.TransactionType.RETURN_CLAIM_TOKEN_BC,
                getParentExtension().getParentZone()).isSuccess()) {
            return new SendReturnClaimToken(ServerConstant.ErrorCode.ERR_SYS);
        }

        return new SendReturnClaimToken();
    }
    /**
     *
     * @param user
     * @param data
     */
    @WithSpan
    private void doReturnClaimToken(User user, ISFSObject data) {
        long uid = extension.getUserManager().getUserModel(user).userID;
        SendReturnClaimToken packet = this.doReturnClaimToken(uid, data);
        send(packet, user);
    }

    @WithSpan
    public SFSObject doReturnClaimToken(ISFSObject data) {
        long uid = data.getLong(Params.UID);
        SendReturnClaimToken packet = this.doReturnClaimToken(uid, data);
        packet.packData();
        return (SFSObject) packet.getData();
    }



    /*----------------------------------------------------------------------------------------------------------------*/
    /**
     *
     * @param rec
     * @return
     */
    @WithSpan
    public ISFSObject GetListNFTHeroInfo(ISFSObject rec) {
        long uid = rec.getLong(Params.UID);
        List<HeroModel> listHeroModel = HeroManager.getInstance().getAllHeroModel(uid, getParentExtension().getParentZone());
        listHeroModel.addAll(HeroManager.getInstance().getListBlockedHeroModel(uid, getParentExtension().getParentZone()));
        listHeroModel = listHeroModel.stream().
                filter(model -> model.type == EHeroType.NFT.getId()).
                map(HeroModel::createByHeroModel).
                collect(Collectors.toList());

        ISFSObject objPut = new SFSObject();
        objPut.putShort(Params.ERROR_CODE, ServerConstant.ErrorCode.NONE);
        ISFSArray arrayPack = new SFSArray();
        ISFSObject objPack;
        for (HeroModel heroModel : listHeroModel) {
            objPack = new SFSObject();
            objPack.putUtfString(Params.ModuleHero.HASH, heroModel.hash);
            objPack.putUtfString(Params.ID, heroModel.id);
            objPack.putShort(Params.LEVEL, heroModel.readLevel());
            objPack.putShort(Params.STAR, heroModel.star);
            objPack.putByte(Params.BREED, heroModel.breed);
            objPack.putByte(Params.MAX_BREED, heroModel.maxBreed);
            objPack.putSFSObject(Params.STATS, SFSObject.newFromJsonData(Utils.toJson(HeroManager.getInstance().getStatsHeroModel(heroModel, getParentExtension().getParentZone()))));
            objPack.putSFSArray(Params.SKILLS, SFSArray.newFromJsonData(Utils.toJson(HeroSkillModel.getFromDB(heroModel, getParentExtension().getParentZone()).skills)));
            objPack.putSFSObject(Params.NFT, SFSObject.newFromJsonData(Utils.toJson(HeroTokenModel.copyFromDBtoObject(heroModel.hash, getParentExtension().getParentZone()))));

            arrayPack.addSFSObject(objPack);
        }
        objPut.putSFSArray(Params.LIST, arrayPack);

        return objPut;
    }

    /**
     *
     * @return
     */
    @WithSpan
    public ISFSObject MintNFTHero(ISFSObject rec) {
        long uid = rec.getLong(Params.UID);
        List<HeroMint> heroMints = Utils.fromJsonList(rec.getUtfString(Params.DATA), HeroMint.class);

        ISFSObject objPut = new SFSObject();
        List<HeroSummonVO> listSummon = new ArrayList<>();
        for (HeroMint heroMint : heroMints) {
            if (heroMint.heroId != null && !heroMint.heroId.isEmpty()) {
                int star = 4;
                int count = 1;
                if (heroMint.star != null && heroMint.star > 0) {
                    star = heroMint.star;
                }

                if (heroMint.count != null && heroMint.count > 0) {
                    count = heroMint.count;
                }

                for (int i = 0; i < count; i++) {
                    listSummon.add(HeroSummonVO.create(heroMint.heroId, star));
                }

                continue;
            }

            if (heroMint.star == null && heroMint.count != null) {
                listSummon.addAll(HeroManager.SummonManager.getInstance().
                        summonUserHero(uid, ESummonID.BANNER_NORMAL.getId(), ESummonType.RANDOM, "", ResourceType.MONEY, heroMint.count, getParentExtension().getParentZone()));
            } else if (heroMint.star != null && heroMint.star >= 0 && heroMint.count != null) {

                for (int i = 0; i < heroMint.count; i++) {
                    System.out.println("mint hero " + heroMint.star);
                    HeroVO heroVO = CharactersConfigManager.getInstance().getRandomHeroConfig(heroMint.star, heroMint.kingdom, null, null);
                    System.out.println("mint hero success" + heroVO.star);
                    listSummon.add(HeroSummonVO.create(heroVO.id, heroMint.star));
                }
            }
        }

        if (listSummon.isEmpty()) {
            objPut.putShort(Params.ERROR_CODE, ServerConstant.ErrorCode.ERR_SYS);
            objPut.putUtfString(Params.MESS, "Error system.");
            return objPut;
        }

        List<HeroModel> listSummonModel = new ArrayList<>();
        for (HeroSummonVO summon : listSummon) {
            listSummonModel.add(HeroModel.createHeroModel(uid, summon.idHero, summon.star, EHeroType.NFT));
        }

        //Add hero vao hang cho
        if (!NFTManager.getInstance().mintHeroModel(uid, listSummonModel, null, null, getParentExtension().getParentZone())) {
            objPut.putShort(Params.ERROR_CODE, ServerConstant.ErrorCode.ERR_SYS);
            objPut.putUtfString(Params.MESS, "Error system.");
            return objPut;
        }

        objPut.putShort(Params.ERROR_CODE, ServerConstant.ErrorCode.NONE);
        objPut.putUtfString(Params.MESS, "");
        ISFSArray arrayPack = new SFSArray();
        ISFSObject objPack;
        for (HeroModel heroModel : listSummonModel) {
            objPack = new SFSObject();
            objPack.putUtfString(Params.ModuleHero.HASH, heroModel.hash);
            objPack.putUtfString(Params.ID, heroModel.id);
            objPack.putShort(Params.LEVEL, heroModel.readLevel());
            objPack.putShort(Params.STAR, heroModel.star);
            objPack.putSFSObject(Params.STATS, SFSObject.newFromJsonData(Utils.toJson(HeroManager.getInstance().getStatsHeroModel(heroModel, getParentExtension().getParentZone()))));
            objPack.putSFSArray(Params.SKILLS, SFSArray.newFromJsonData(Utils.toJson(HeroSkillModel.getFromDB(heroModel, getParentExtension().getParentZone()).skills)));

            arrayPack.addSFSObject(objPack);
        }
        objPut.putSFSArray(Params.LIST, arrayPack);

        return objPut;
    }

    /**
     *
     * @param rec
     * @return
     */
    @WithSpan
    public ISFSObject VerifyNFTHero(ISFSObject rec) {
        ISFSObject res = new SFSObject();
        Zone zone = getParentExtension().getParentZone();
        String txhash = rec.getText(Params.TRANS_ID);
        res.putShort(Params.ERROR_CODE, ServerConstant.ErrorCode.NONE);
        res.putSFSArray(Params.LIST, new SFSArray());
        long uid = rec.getLong(Params.UID);
        ISFSArray heroes = rec.getSFSArray(Params.LIST);
        List<HeroToken> listHeroTokens = new ArrayList<>();
        List<String> hashs = new ArrayList<>();
        Map<String, String> mapToken = new HashMap<>();

        for (int i = 0; i < heroes.size(); i++) {
            ISFSObject hero = heroes.getSFSObject(i);
            String hash = hero.getText(Params.HASH);
            String tokenId = hero.getText(Params.TOKEN);
            HeroToken heroToken = HeroToken.create(hash, tokenId);
            listHeroTokens.add(heroToken);
            hashs.add(hash);
            mapToken.put(hash, tokenId);
        }

        if (listHeroTokens.size() == 0 || hashs.size() == 0) {
            return res;
        }

        // Get hero mint in db
        List<HeroModel> listHeroModel = NFTManager.getInstance().getListHeroModelMinted(uid, hashs, zone);
        if (listHeroModel.size() == 0) {
            return res;
        }

        if(!HeroManager.getInstance().addUserAllHeroModel(uid, listHeroModel, zone, false, null)) {
            res.putShort(Params.ERROR_CODE, ServerConstant.ErrorCode.ERR_SYS);
            System.out.println("Verify addUserAllHeroModel uid: " + uid);
            System.out.println("Verify addUserAllHeroModel listHeroModel: " + listHeroModel.size());
            return res;
        }

        //Save info nft hero
        Map<String, Object> dataEvent = new HashMap<>();
        int count = 0;
        List<Short> stars = new ArrayList<>();
        ISFSArray arrayPack = new SFSArray();
        for (HeroModel heroModel : listHeroModel) {
            NFTManager.getInstance().createHeroTokenModel(heroModel.hash, txhash, mapToken.getOrDefault(heroModel.hash, ""), zone);
            ISFSObject objPack = new SFSObject();
            objPack.putText(Params.ModuleHero.HASH, heroModel.hash);
            objPack.putText(Params.HERO_ID, heroModel.id);
            objPack.putText(Params.TOKEN_ID, mapToken.getOrDefault(heroModel.hash, ""));
            objPack.putShort(Params.LEVEL, heroModel.readLevel());
            objPack.putShort(Params.STAR, heroModel.star);
            objPack.putByte(Params.BREED, (byte) 0);
            objPack.putByte(Params.MAX_BREED, HeroModel.getDefaultBreed(heroModel.id));
            objPack.putSFSObject(Params.STATS, SFSObject.newFromJsonData(Utils.toJson(HeroManager.getInstance().getStatsHeroModel(heroModel, getParentExtension().getParentZone()))));
            objPack.putSFSArray(Params.SKILLS, SFSArray.newFromJsonData(Utils.toJson(HeroSkillModel.getFromDB(heroModel, getParentExtension().getParentZone()).skills)));
//            objPack.putSFSObject(Params.NFT, SFSObject.newFromJsonData(Utils.toJson(HeroTokenModel.copyFromDBtoObject(heroModel.hash, getParentExtension().getParentZone()))));
            arrayPack.addSFSObject(objPack);

            if (!heroModel.fatherHash.isEmpty() || !heroModel.motherHash.isEmpty()) {
                count++;
                stars.add(heroModel.star);
            }
        }

        res.putSFSArray(Params.CONTENT, arrayPack);
        if (count > 0) {
            dataEvent.put(Params.COUNT, count);
            dataEvent.put(Params.STAR, stars);
            GameEventAPI.ariseGameEvent(EGameEvent.SUMMON_TAVERN, uid, dataEvent, zone);
        }


        //Delete hero trong hang cho
        if (!NFTManager.getInstance().deleteHeroModelMinted(
                uid,
                listHeroModel.stream().map(obj -> obj.hash).collect(Collectors.toList()),
                getParentExtension().getParentZone())) {
            System.out.println("Verify deleteHeroModelMinted ");
            res.putShort(Params.ERROR_CODE, ServerConstant.ErrorCode.ERR_SYS);
            return res;
        }

        return res;
    }

    /**
     *
     * @param rec
     * @return
     */
    @WithSpan
    public ISFSObject TranferNFTHero(ISFSObject rec) {
        Zone zone = getParentExtension().getParentZone();
        String toAddress = rec.getText(Params.TO);
        String serverId = rec.getText(Params.SERVER_ID);
        String hash = rec.getText(Params.HASH);
        long from = rec.getLong(Params.UID);
        long to = SDKGateAccount.getUserId(toAddress, ESocialNetwork.BLOCKCHAIN.getIntValue(), serverId);
        ISFSObject res = new SFSObject();
        if (to < 0) {
            res.putShort(Params.ERROR_CODE, ServerConstant.ErrorCode.ERR_USER_NOT_EXIST);
        }

        HeroModel heroModel = HeroManager.getInstance().getBlockedHeroModel(from, hash, zone);
        if (heroModel == null) {
            res.putShort(Params.ERROR_CODE, ServerConstant.ErrorCode.ERR_NOT_EXSIST_CHARACTER);
            return res;
        }

        heroModel.level = 1;
        if(HeroManager.getInstance().removeBlockHeroMode(from, heroModel.hash, zone) == null ||
                !HeroManager.getInstance().addUserAllHeroModel(to, Collections.singletonList(heroModel), zone, false, null)) {
            res.putShort(Params.ERROR_CODE, ServerConstant.ErrorCode.ERR_SYS);
            res.putText(Params.MESS, "Error system");
            return res;
        }

        SendNotifyMintHero packet;
        User user = ExtensionUtility.getInstance().getUserById(from);
        packet = new SendNotifyMintHero(from);
        if (user != null) {
            send(packet, user);
        }

        user = ExtensionUtility.getInstance().getUserById(to);
        packet = new SendNotifyMintHero(to);
        if (user != null) {
            send(packet, user);
        }

        res.putShort(Params.ERROR_CODE, ServerConstant.ErrorCode.NONE);
        return res;
    }

    @WithSpan
    public ISFSObject transferHeroFromWallet(ISFSObject rec) {
        Zone zone = getParentExtension().getParentZone();
        String toAddress = rec.getText(Params.TO);
        String serverId = rec.getText(Params.SERVER_ID);
        String hash = rec.getText(Params.HASH);
        long from = rec.getLong(Params.UID);
        ISFSObject res = new SFSObject();
        HeroModel heroModel = HeroManager.getInstance().removeHeroModel(from, hash, zone);
        if (heroModel == null) {
            res.putShort(Params.ERROR_CODE, ServerConstant.ErrorCode.ERR_NOT_EXSIST_CHARACTER);
            return res;
        }

        heroModel.level = 1;
        HeroManager.getInstance().updateStatsHeroModel(heroModel, zone);
        long to = SDKGateAccount.getUserId(toAddress, ESocialNetwork.BLOCKCHAIN.getIntValue(), serverId);
        res.putLong(Params.FROM, from);
        res.putLong(Params.TO, to);
        res.putText(Params.HASH, hash);
        if (to < 0) {
            this.logger.info("transferHeroFromWallet error to " + to);
            return res;
        }


        if(!HeroManager.getInstance().addUserAllHeroModel(to, Collections.singletonList(heroModel), zone, false, null)) {
            this.logger.info("transferHeroFromWallet error, cant add hero to wallet");
            res.putShort(Params.ERROR_CODE, ServerConstant.ErrorCode.ERR_SYS);
            res.putText(Params.MESS, "Error system");
            return res;
        }

        SendNotifyMintHero packet;
        User user = ExtensionUtility.getInstance().getUserById(from);
        packet = new SendNotifyMintHero(from);
        if (user != null) {
            send(packet, user);
        }

        user = ExtensionUtility.getInstance().getUserById(to);
        packet = new SendNotifyMintHero(to);
        if (user != null) {
            send(packet, user);
        }

        res.putShort(Params.ERROR_CODE, ServerConstant.ErrorCode.NONE);
        return res;
    }

    @WithSpan
    public void notifyChangeToken(ISFSObject rec) {
        ISFSArray arrayCurrent = rec.getSFSArray(Params.DATA);
        User user = ExtensionUtility.getInstance().getUserById(arrayCurrent.getSFSObject(0).getLong(Params.UID));
        if (user == null) {
            return;
        }
        SendNotifyChangeToken sendCmd = new SendNotifyChangeToken();
        sendCmd.arrayCurrent = arrayCurrent;
        send(sendCmd, user);
    }

    @WithSpan
    public ISFSObject depositToken(ISFSObject data) {
        ISFSObject res = new SFSObject();
        long uid = data.getLong(Params.UID);
        String txhash = data.getText(Params.TRANS_ID);
        res.putText(Params.TXHASH, txhash);
        String moneyType = data.getText(Params.MONEY_TYPE);
        double money = data.getDouble(Params.MONEY);
        //Check da tung su dung
        if (NFTManager.getInstance().isUsedTransactionHash(txhash)) {
            res.putShort(Params.ERROR_CODE, ServerConstant.ErrorCode.ERR_USED_TRANSACTION_TRANFER_TOKEN);
            return res;
        }

        ETokenBC token = ETokenBC.fromId(moneyType);
        if (token == null) {
            res.putShort(Params.ERROR_CODE, ServerConstant.ErrorCode.ERR_INVALID_TOKEN);
            return res;
        }

        boolean isSuccess = NFTManager.getInstance().updateToken(
                uid,
                Collections.singletonList(new TokenResourcePackage(moneyType, money)),
                UserUtils.TransactionType.BUY_TOKEN_BC,
                getParentExtension().getParentZone()).isSuccess();
        if (!isSuccess) {
            res.putShort(Params.ERROR_CODE, ServerConstant.ErrorCode.ERR_SYS);
            return res;
        }

        //Save
        if (!NFTManager.getInstance().saveTransactionHash(uid, txhash, money)) {
            res.putShort(Params.ERROR_CODE, ServerConstant.ErrorCode.ERR_SYS);
            return res;
        }

        res.putShort(Params.ERROR_CODE, ServerConstant.ErrorCode.NONE);
        return res;
    }

    @WithSpan
    public ISFSObject getListHeroOpenBox(ISFSObject data) {
        long uid = data.getLong(Params.UID);
        Zone zone = getParentExtension().getParentZone();
        UserMintHeroModel mintHeroModel = NFTManager.getInstance().getUserMintHeroModel(uid, zone);
        ISFSArray hash = new SFSArray();
        ISFSArray lst = new SFSArray();
        ISFSObject obj = new SFSObject();
        for (HeroMintModel heroMintModel : mintHeroModel.listHeroMint) {
            for (HeroModel heroModel : heroMintModel.listHeroMint) {
                if (heroModel.timeClaim == null) {
                    hash.addText(heroModel.hash);
                    obj.putText(Params.ID, heroModel.id);
                    obj.putText(Params.HASH, heroModel.hash);
                    obj.putShort(Params.LEVEL, heroModel.level);
                    obj.putInt(Params.STAR, heroModel.star);
                    lst.addSFSObject(obj);
                }
            }
        }
        ISFSObject res = new SFSObject();
        res.putShort(Params.ERROR_CODE, ServerConstant.ErrorCode.NONE);
        res.putSFSArray(Params.DATA, hash);
        res.putSFSArray(Params.LIST, lst);
        return res;
    }

}
