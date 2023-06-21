package com.bamisu.log.gameserver.module.nft;

import com.bamisu.gamelib.base.BaseExtension;
import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.entities.*;
import com.bamisu.gamelib.item.define.ResourceType;
import com.bamisu.gamelib.socialnetwork.ESocialNetwork;
import com.bamisu.gamelib.utils.UserUtils;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.RabbitMQHandler;
import com.bamisu.log.gameserver.datamodel.hero.UserAllHeroModel;
import com.bamisu.log.gameserver.datamodel.hero.entities.HeroModel;
import com.bamisu.log.gameserver.datamodel.nft.*;
import com.bamisu.log.gameserver.datamodel.nft.entities.HeroMintModel;
import com.bamisu.log.gameserver.datamodel.nft.entities.HeroUpstarBurn;
import com.bamisu.log.gameserver.module.characters.CharactersConfigManager;
import com.bamisu.log.gameserver.module.characters.summon.entities.HeroSummonVO;
import com.bamisu.log.gameserver.module.hero.HeroManager;
import com.bamisu.log.gameserver.module.hero.define.EHeroType;
import com.bamisu.log.gameserver.module.nft.cmd.rec.RecBreed;
import com.bamisu.log.gameserver.module.nft.defind.ETokenBC;
import com.bamisu.log.gameserver.module.nft.entities.ChangeTokenResult;
import com.bamisu.log.gameserver.module.nft.exception.BreedException;
import com.bamisu.log.gameserver.module.sdkthriftclient.SDKGateAccount;
import com.bamisu.log.gameserver.module.sdkthriftclient.SDKGateNFT;
import com.bamisu.log.nft.Web3jFactory;
import com.bamisu.log.nft.entities.HeroToken;
import com.bamisu.log.nft.entities.HeroTokenTranfer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;
import org.apache.thrift.TException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

/**
 * Created by Quach Thanh Phong
 * On 2/12/2022 - 2:39 PM
 */
public class NFTManager {
    private final static ObjectMapper mapper = new ObjectMapper();


    private static NFTManager ourInstance = new NFTManager();

    public static NFTManager getInstance() {
        return ourInstance;
    }

    private NFTManager() { }


    /*----------------------------------------------------------------------------------------------------------------*/
    public UserTokenModel getUserMineTokenModel(long uid, Zone zone) {
        UserTokenModel userTokenModel = UserTokenModel.copyFromDBtoObject(uid, zone);
        if (userTokenModel == null) {
            userTokenModel = UserTokenModel.createUserMineTokenModel(uid, zone);
        }
        return userTokenModel;
    }

    public UserMintHeroModel getUserMintHeroModel(long uid, Zone zone) {
        UserMintHeroModel userMintHeroModel = UserMintHeroModel.copyFromDBtoObject(uid, zone);
        if (userMintHeroModel == null) {
            userMintHeroModel = UserMintHeroModel.createUserMintHeroModel(uid, zone);
        }
        return userMintHeroModel;
    }

    public UserTokenClaimModel getUserTokenClaimModel(long uid, Zone zone) {
        UserTokenClaimModel userTokenClaimModel = UserTokenClaimModel.copyFromDBtoObject(uid, zone);
        if (userTokenClaimModel == null) {
            userTokenClaimModel = UserTokenClaimModel.createUseTokenTransactionModel(uid, zone);
        }
        return userTokenClaimModel;
    }

    public HeroModel getHeroModelMinted(long uid, String hashHero, Zone zone) {
        UserMintHeroModel userMintHeroModel = getUserMintHeroModel(uid, zone);
        for (HeroMintModel heroMintModel : userMintHeroModel.listHeroMint) {
            for (HeroModel heroModel : heroMintModel.listHeroMint) {
                if (heroModel.hash.equals(hashHero)) return heroModel;
            }
        }
        return null;
    }

    public List<HeroModel> getListHeroModelMinted(long uid, List<String> hashHero, Zone zone) {
        UserMintHeroModel userMintHeroModel = getUserMintHeroModel(uid, zone);
        List<HeroModel> listHero = new ArrayList<>();
        long now = System.currentTimeMillis() / 1000;
        for (HeroMintModel heroMintModel : userMintHeroModel.listHeroMint) {
            for (HeroModel heroModel : heroMintModel.listHeroMint) {
                boolean isPass = heroModel.timeClaim == null || now >= heroModel.timeClaim;
                if (hashHero.contains(heroModel.hash) && isPass) {
                    listHero.add(heroModel);
                }
            }
        }
        return listHero;
    }

    public List<HeroModel> getListHeroModelMinted(long uid, List<String> hashHero, Zone zone, boolean isMintBox) {
        UserMintHeroModel userMintHeroModel = getUserMintHeroModel(uid, zone);
        List<HeroModel> listHero = new ArrayList<>();
        long now = System.currentTimeMillis() / 1000;
        for (HeroMintModel heroMintModel : userMintHeroModel.listHeroMint) {
            for (HeroModel heroModel : heroMintModel.listHeroMint) {
                boolean isPassCondition;
                if (isMintBox) {
                    isPassCondition = heroModel.timeClaim == null;
                } else {
                    isPassCondition = heroModel.timeClaim != null && now >= heroModel.timeClaim;
                }
                if (hashHero.contains(heroModel.hash) && isPassCondition) {
                    listHero.add(heroModel);
                }
            }
        }
        return listHero;
    }

    public List<HeroModel> getListHeroModelMinted(UserMintHeroModel userMintHeroModel, List<String> hashHero, Zone zone) {
        List<HeroModel> listHero = new ArrayList<>();
        for (HeroMintModel heroMintModel : userMintHeroModel.listHeroMint) {
            for (HeroModel heroModel : heroMintModel.listHeroMint) {
                if (hashHero.contains(heroModel.hash)) {
                    listHero.add(heroModel);
                }
            }
        }
        return listHero;
    }

    public HeroMintModel getHeroMintedModel(UserMintHeroModel userMintHeroModel, List<String> hashHero, Zone zone) {
        for (HeroMintModel heroMintModel : userMintHeroModel.listHeroMint) {
            for (HeroModel heroModel : heroMintModel.listHeroMint) {
                if (hashHero.contains(heroModel.hash)) {
                    return heroMintModel;
                }
            }
        }
        return null;
    }

    public boolean deleteHeroModelMinted(long uid, List<String> hashHero, Zone zone) {
        UserMintHeroModel userMintHeroModel = getUserMintHeroModel(uid, zone);
        List<HeroMintModel> listHeroMint = new ArrayList<>();
        List<HeroModel> listHero;
        for (HeroMintModel heroMintModel : userMintHeroModel.listHeroMint) {
            listHero = new ArrayList<>();
            for (HeroModel heroModel : heroMintModel.listHeroMint) {
                if (hashHero.contains(heroModel.hash)) {
                    listHero.add(heroModel);
                }
            }
            heroMintModel.listHeroMint.removeAll(listHero);
            if (heroMintModel.listHeroMint.isEmpty()) {
                listHeroMint.add(heroMintModel);
            }
        }
        userMintHeroModel.listHeroMint.removeAll(listHeroMint);
        return userMintHeroModel.saveToDB(zone);
    }

    /**
     * Người chơi reject, không claim hero -> bỏ
     * @param uid
     * @param hashHero
     * @param zone
     * @return
     */
    public ResourcePackage removeHeroModelMinted(long uid, List<String> hashHero, Zone zone) {
        /**UserMintHeroModel userMintHeroModel = getUserMintHeroModel(uid, zone);
        List<HeroModel> listHero;
        for (HeroMintModel heroMintModel : userMintHeroModel.listHeroMint) {
            if (heroMintModel.listHeroMint.size() != heroMintModel.sum) continue;
            if (heroMintModel.listHeroMint.size() != hashHero.size()) continue;
            listHero = heroMintModel.listHeroMint.stream()
                    .map(HeroModel::createByHeroModel)
                    .collect(Collectors.toList());
            if (listHero.removeAll(listHero)) {
                if (listHero.isEmpty()) {
                    userMintHeroModel.listHeroMint.remove(userMintHeroModel.listHeroMint.indexOf(heroMintModel));
                }
                userMintHeroModel.saveToDB(zone);
                return heroMintModel.resourceCreate;
            } else {
                continue;
            }
        }
         */
        return null;
    }

    public ChangeTokenResult updateToken(long uid, List<TokenResourcePackage> resourcePackageList, TransactionDetail detail, Zone zone) {
        UserTokenModel userTokenModel = getUserMineTokenModel(uid, zone);

        ChangeTokenResult result = userTokenModel.changeToken(resourcePackageList, detail, zone);

        if (result.isSuccess()) {
            SFSArray arrayCurrent = new SFSArray();
            Map<String, Object> map = new HashMap<>();
            map.put(Params.UID, userTokenModel.uid);
            for (TokenResourcePackage vo : resourcePackageList) {
                SFSObject sfsObject = vo.toSFSObject(map);
                arrayCurrent.addSFSObject(sfsObject);
            }
            //send notify change resource
            UserUtils.changeToken(uid, arrayCurrent, detail, zone);
        }

        return result;
    }

    public boolean mintHeroModel(long uid, List<HeroModel> listHeroModel, List<TokenResourcePackage> resourceUse, String extraData, Zone zone) {
        if (listHeroModel == null || listHeroModel.isEmpty()) return false;
        if (resourceUse == null || resourceUse.size() == 0) {
            resourceUse = new ArrayList<>();
        }

        UserMintHeroModel userMintHeroModel = getUserMintHeroModel(uid, zone);
        userMintHeroModel.listHeroMint.add(HeroMintModel.create(listHeroModel, resourceUse, extraData));
        return userMintHeroModel.saveToDB(zone);
    }

    public boolean canClaimTokenMined(long uid, String token, int count, Zone zone) {
        UserTokenModel userTokenModel = getUserMineTokenModel(uid, zone);
        long result = userTokenModel.readToken(ETokenBC.fromId(token));
        return result >= 40 && result >= count;
    }

    public List<HeroToken> getHashHeroByTransactionHashMint(String transactionHash, List<String> tokenIds) {
        try {
            return Web3jFactory.getInstance().getHashHeroByTransactionHashMint(transactionHash, tokenIds);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public boolean createHeroTokenModel(String hashHero, String txHash, String tokenId, Zone zone) {
        HeroTokenModel heroTokenModel = HeroTokenModel.createHeroTokenModel(hashHero, txHash, tokenId, zone);
        return true;
    }

    public boolean isUsedTransactionHash(String transactionHash) {
        try {
            return SDKGateNFT.haveInstanceTranferToken(transactionHash);
        } catch (TException e) {
            e.printStackTrace();
            return true;
        }
    }

    public boolean saveTransactionHash(long uid, String transactionHash, double count) {
        try {
            return SDKGateNFT.saveTranferToken(transactionHash, count, uid);
        } catch (TException e) {
            e.printStackTrace();
            return true;
        }
    }

    public boolean isValidTransactionClaimToken(long uid, String transactionHash, int count, Zone zone) {
        try {
            UserModel userModel = ((BaseExtension)zone.getExtension()).getUserManager().getUserModel(uid);
            String addressWallet = SDKGateAccount.getUniqueIdSocialNetwork(userModel.accountID, ESocialNetwork.BLOCKCHAIN.getIntValue());
            return Web3jFactory.getInstance().isValidTransactionClaimToken(addressWallet, transactionHash, count);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isValidTransactionBuyToken(long uid, String transactionHash, ETokenBC token, int count, Zone zone) {
        try {
            UserModel userModel = ((BaseExtension)zone.getExtension()).getUserManager().getUserModel(uid);
            String addressWallet = SDKGateAccount.getUniqueIdSocialNetwork(userModel.accountID, ESocialNetwork.BLOCKCHAIN.getIntValue());
            return Web3jFactory.getInstance().isValidTransactionBuyToken(addressWallet, transactionHash, token.getId(), count);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void notifySyncHero(long uid) {
        /*try {
            Map<String,Object> mapRequest = new HashMap<>();
            mapRequest.put("userId", uid);
            String jsonData = Utils.toJson(mapRequest);
            URL url = new URL("http://10.0.15.226:3001/api/external/sync-hero");
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setRequestProperty("Authorization", "Bearer APA91bEpdBqshh1qkal1MjfObscPIDsKeBxG6XKxVIiMZfRA97DctxdLhhu");
            urlConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            urlConn.setUseCaches(false);
            urlConn.setConnectTimeout(5);
            urlConn.setDoInput(true);
            urlConn.setRequestMethod("POST");
            urlConn.setDoOutput(true);
            urlConn.setRequestProperty("Content-Type", "application/json");
            urlConn.setRequestProperty("Accept", "application/json");

            OutputStreamWriter osw = new OutputStreamWriter(urlConn.getOutputStream());
            osw.write(jsonData);
            osw.flush();
            osw.close();
            System.out.println(urlConn.getResponseMessage() + " " + urlConn.getResponseCode());
            urlConn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    public String signData(long uid, ETokenBC token, double count, Zone zone) {
        try {
            UserModel userModel = ((BaseExtension)zone.getExtension()).getUserManager().getUserModel(uid);
            String addressWallet = SDKGateAccount.getUniqueIdSocialNetwork(userModel.accountID, ESocialNetwork.BLOCKCHAIN.getIntValue());

            Map<String,Object> mapRequest = new HashMap<>();
            mapRequest.put("address", addressWallet);
            mapRequest.put("count", count);
            mapRequest.put("type", token.getIdSign());
            String jsonData = Utils.toJson(mapRequest);

            URL url = new URL("https://apimarket.metarrior.xyz/api/contract/sign");
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setRequestProperty("Authorization", "Bearer APA91bEpdBqshh1qkal1MjfObscPIDsKeBxG6XKxVIiMZfRA97DctxdLhhu");
            urlConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            urlConn.setUseCaches(false);
            urlConn.setDoInput(true);
            urlConn.setRequestMethod("POST");
            urlConn.setDoOutput(true);
            urlConn.setRequestProperty("Content-Type", "application/json");
            urlConn.setRequestProperty("Accept", "application/json");

            OutputStreamWriter osw = new OutputStreamWriter(urlConn.getOutputStream());
            osw.write(jsonData);
            osw.flush();
            osw.close();

            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line = "";
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            reader.close();
            urlConn.disconnect();
            String jsonResponse = builder.toString();

            System.out.println(jsonResponse);
            JsonNode jsonObj = mapper.readTree(jsonResponse);

            if (jsonObj.get("success").asBoolean()) {
                return jsonObj.get("data").get("sign").asText();
            } else {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public HeroTokenTranfer heroTokenTranferData(String transactionHash) {
        try {
            return Web3jFactory.getInstance().getHeroDataTranfer(transactionHash);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<HeroToken> getHashHeroByTransactionHashBurn(String transactionHash, List<String> tokenIds) {
        try {
            return Web3jFactory.getInstance().getHashHeroByTransactionHashBurn(transactionHash, tokenIds);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public boolean upstarHeroModel(long uid, HeroUpstarBurn heroUpstarBurn, Zone zone) {
        UserBurnHeroModel userBurnHeroModel = UserBurnHeroModel.copyFromDBtoObject(uid, zone);
        return userBurnHeroModel.saveHeroUpstar(heroUpstarBurn, zone);
    }

    public HeroUpstarBurn getHeroUpstar(long uid, String hashHero, Zone zone) {
        UserBurnHeroModel userBurnHeroModel = UserBurnHeroModel.copyFromDBtoObject(uid, zone);
        return userBurnHeroModel.mapUpstar.get(hashHero);
    }

    public HeroUpstarBurn deleteHeroUpstar(long uid, String hashHero, Zone zone) {
        UserBurnHeroModel userBurnHeroModel = UserBurnHeroModel.copyFromDBtoObject(uid, zone);
        HeroUpstarBurn heroUpstarBurn = userBurnHeroModel.mapUpstar.remove(hashHero);
        userBurnHeroModel.saveToDB(zone);
        return heroUpstarBurn;
    }

    /**
     * Sum hero
     */
    public List<HeroModel> doBreed(RecBreed packet) throws BreedException {
        long uid = packet.uid;
        String idSummon = String.valueOf(packet.bannerId);
        // so luong sum
        if (packet.count <= 0) {
            throw new BreedException(ServerConstant.ErrorCode.ERR_CHAR);
        }

        // config
        if (CharactersConfigManager.getInstance().getSummonConfig(idSummon) == null) {
            throw new BreedException(ServerConstant.ErrorCode.ERR_NOT_EXSIST_SUMMON_BANNER);
        }

        // kiem tra bag xem con slot hay k
        if (HeroManager.getInstance().isMaxSizeBagListHero(uid, packet.count, packet.zone)) {
            throw new BreedException(ServerConstant.ErrorCode.ERR_MAX_SIZE_BAG_HERO);
        }

        List<String> listHash = new ArrayList<>();
        listHash.add(packet.fatherHash);
        listHash.add(packet.motherHash);
        Collections.sort(listHash);

        UserAllHeroModel userAllHeroModel = HeroManager.getInstance().getUserAllHeroModel(uid, packet.zone);
        List<HeroModel> heroes = new ArrayList<>();
        for (String hash : listHash) {
            for (HeroModel heroModel : userAllHeroModel.listAllHeroModel) {
                if (heroModel.breed == -1) {
                    heroModel.breed = 0;
                }

                if (heroModel.maxBreed == -1) {
                    heroModel.maxBreed = HeroModel.getDefaultBreed(heroModel.id);
                }
                if (heroModel.hash.equalsIgnoreCase(hash)) {
                    heroes.add(heroModel);
                    break;
                }
            }
        }

        // Số lượng hash != số lượng hero
        if (heroes.size() < 2) {
            throw new BreedException(ServerConstant.ErrorCode.ERR_INVALID_HERO_HASH);
        }

        // hero type = nft
        HeroModel father = heroes.get(0);
        HeroModel mother = heroes.get(1);
        if (father.type != EHeroType.NFT.getId() || mother.type != EHeroType.NFT.getId()) {
            throw new BreedException(ServerConstant.ErrorCode.ERR_HERO_TYPE_NOT_THE_SAME);
        }

        if (father.isBreeding || mother.isBreeding) {
            throw new BreedException(ServerConstant.ErrorCode.ERR_HERO_BREEDING);
        }

        // kiểm tra số lượng breed
        if (father.breed + packet.count > father.maxBreed || mother.breed + packet.count > mother.maxBreed) {
            throw new BreedException(ServerConstant.ErrorCode.ERR_THE_BREED_NOT_ENOUGH);
        }

        // Tieu tai nguyen
        List<TokenResourcePackage> resourceUse = HeroManager
                .SummonManager
                .getInstance()
                .useResourceSummonUserHero(uid, idSummon, packet.count, packet.zone, packet.resource);
        if (resourceUse == null || resourceUse.size() == 0) {
            throw new BreedException(ServerConstant.ErrorCode.ERR_NOT_ENOUGH_RESOURCE);
        }

        // sum hero
        List<HeroSummonVO> listSummoned = HeroManager.SummonManager.getInstance().summonUserHero(uid, idSummon, null, null, ResourceType.MONEY, packet.count, packet.zone);
        if (listSummoned == null || listSummoned.size() <= 0) {
            throw new BreedException(ServerConstant.ErrorCode.ERR_SYS);
        }

        List<HeroModel> listSummonedModel = new ArrayList<>();
        long now = System.currentTimeMillis() / 1000L;
        int timer = 18000; // 4h/1 hero
//        int timer = 300; // 4h/1 hero
        for (HeroSummonVO summon : listSummoned) {
            now += timer;
            HeroModel heroModel = HeroModel.createHeroModel(uid, summon.idHero, summon.star, EHeroType.NFT, now);
            heroModel.motherHash = mother.hash;
            heroModel.fatherHash = father.hash;
            listSummonedModel.add(heroModel);
            father.children.add(heroModel.hash);
            mother.children.add(heroModel.hash);
        }

        //Add hero vao hang cho
        if (!NFTManager.getInstance().mintHeroModel(uid, listSummonedModel, resourceUse, idSummon, packet.zone)) {
            throw new BreedException(ServerConstant.ErrorCode.ERR_SYS);
        }

        // Cap nhat thong tin hero
        father.isBreeding = true;
        father.breed += packet.count;
        mother.isBreeding = true;
        mother.breed += packet.count;
        userAllHeroModel.saveToDB(packet.zone);
        ISFSObject res = new SFSObject();
        res.putText(Params.FROM, String.valueOf(uid));
        ISFSArray data = new SFSArray();
        ISFSObject objHero1 = new SFSObject();
        objHero1.putText("hashHero", father.hash);
        objHero1.putByte(Params.BREED, father.breed);
        ISFSObject objHero2 = new SFSObject();
        objHero1.putText("hashHero", mother.hash);
        objHero1.putByte(Params.BREED, mother.breed);
        data.addSFSObject(objHero1);
        data.addSFSObject(objHero2);
        res.putSFSArray(Params.CONTENT, data);
        RabbitMQHandler.getInstance().sendBreedHero(res);
        return listSummonedModel;
    }
//        return true;
    public String createTransaction() {
        return Utils.ranStr(5) + "-" + System.currentTimeMillis() % 100000 + "-" + Utils.ranStr(5);
    }
}
