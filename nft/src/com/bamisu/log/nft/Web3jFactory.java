package com.bamisu.log.nft;

import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.nft.contract.Hero;
import com.bamisu.log.nft.entities.HeroToken;
import com.bamisu.log.nft.entities.HeroTokenTranfer;
import com.bamisu.log.nft.manager.MetarriorContractManager;
import com.bamisu.log.nft.manager.HeroContractManager;
import com.bamisu.log.nft.manager.SoulgemContractManager;
import org.web3j.crypto.Keys;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Quach Thanh Phong
 * On 2/16/2022 - 12:42 AM
 */
public class Web3jFactory {
    private Web3jClient web3jClient;
    private Web3j web3j;
    private Hero heroContract;


    private static Web3jFactory ourInstance = new Web3jFactory();
    public static Web3jFactory getInstance() {
        return ourInstance;
    }
    private Web3jFactory() {
        try {
            this.web3jClient = Web3jClient.build("https://data-seed-prebsc-1-s1.binance.org:8545");
            this.web3j = this.web3jClient.getWeb3j();
            this.heroContract = new HeroContractManager(web3j).getContract();
            System.out.println(web3jClient + "@" + "https://data-seed-prebsc-1-s1.binance.org:8545");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public List<HeroToken> getHashHeroByTransactionHashMint(String transactionHash, List<String> tokenIds) throws Exception {
        EthGetTransactionReceipt ethGetTransactionReceipt = web3j.ethGetTransactionReceipt(transactionHash).sendAsync().get(1, TimeUnit.DAYS);
        System.out.println(Utils.toJson(ethGetTransactionReceipt));
        TransactionReceipt transactionReceipt = ethGetTransactionReceipt.getTransactionReceipt().orElse(null);
        List<HeroToken> listData = new ArrayList<>();
        BigInteger tokenId;
        for (Log log : transactionReceipt.getLogs()) {
            tokenId = web3jClient.getDecimal(log.getTopics().get(3));

            if (!tokenIds.contains(tokenId.toString())) continue;
            listData.add(HeroToken.create(this.heroContract.getHeroByTokenId(tokenId).send(), tokenId.toString()));
        }
        return listData;
    }

    public boolean isValidTransactionClaimToken(String from, String transactionHash, int count) throws IOException {
        EthGetTransactionReceipt ethGetTransactionReceipt = web3j.ethGetTransactionReceipt(transactionHash).send();
        System.out.println(Utils.toJson(ethGetTransactionReceipt));
        TransactionReceipt transactionReceipt = ethGetTransactionReceipt.getTransactionReceipt().orElse(null);
        return count == Convert.fromWei(new BigDecimal(web3jClient.getDecimal(transactionReceipt.getLogs().get(1).getData())), Convert.Unit.ETHER).intValue() &&
                transactionReceipt.getFrom().equals(from);
    }

    public boolean isValidTransactionBuyToken(String from, String transactionHash, String name, int count) throws IOException {
        EthGetTransactionReceipt ethGetTransactionReceipt = web3j.ethGetTransactionReceipt(transactionHash).send();
        System.out.println(Utils.toJson(ethGetTransactionReceipt));
        TransactionReceipt transactionReceipt = ethGetTransactionReceipt.getTransactionReceipt().orElse(null);
        String checksumTo = Keys.toChecksumAddress(transactionReceipt.getTo());
        String checksumContract = "";
        switch (name) {
            case "MEWA":
                checksumContract = Keys.toChecksumAddress(MetarriorContractManager.getAddressContract());
                break;
            case "SOG":
                checksumContract = Keys.toChecksumAddress(SoulgemContractManager.getAddressContract());
                break;
        }
        return checksumContract.equals(checksumTo) &&
                count == Convert.fromWei(new BigDecimal(web3jClient.getDecimal(transactionReceipt.getLogs().get(0).getData())), Convert.Unit.ETHER).intValue() &&
                transactionReceipt.getFrom().equals(from);
    }

    public HeroTokenTranfer getHeroDataTranfer(String transactionHash) throws Exception {
        EthGetTransactionReceipt ethGetTransactionReceipt = web3j.ethGetTransactionReceipt(transactionHash).send();
        System.out.println(Utils.toJson(ethGetTransactionReceipt));
        TransactionReceipt transactionReceipt = ethGetTransactionReceipt.getTransactionReceipt().orElse(null);

        String addressFrom = transactionReceipt.getLogs().get(2).getTopics().get(2);
        HeroTokenTranfer heroTokenTranfer = new HeroTokenTranfer();
        heroTokenTranfer.from = "0x".concat(addressFrom.substring(addressFrom.length() - 40));
        heroTokenTranfer.to = transactionReceipt.getFrom();
        heroTokenTranfer.hashHero = this.heroContract.getHeroByTokenId(web3jClient.getDecimal(transactionReceipt.getLogs().get(4).getTopics().get(3))).send();
        return heroTokenTranfer;
    }

    public List<HeroToken> getHashHeroByTransactionHashBurn(String transactionHash, List<String> tokenIds) throws Exception {
        EthGetTransactionReceipt ethGetTransactionReceipt = web3j.ethGetTransactionReceipt(transactionHash).sendAsync().get(1, TimeUnit.DAYS);
        System.out.println(Utils.toJson(ethGetTransactionReceipt));
        TransactionReceipt transactionReceipt = ethGetTransactionReceipt.getTransactionReceipt().orElse(null);
        List<HeroToken> listData = new ArrayList<>();
        BigInteger tokenId;
        for (int i = 0; i < transactionReceipt.getLogs().size(); i++) {
            if (i % 3 != 0) continue;

            tokenId = web3jClient.getDecimal(transactionReceipt.getLogs().get(i).getTopics().get(3));
            if (!tokenIds.contains(tokenId.toString())) continue;
            listData.add(HeroToken.create(this.heroContract.getHeroByTokenId(tokenId).send(), tokenId.toString()));
        }
        return listData;
    }




    public static void main(String[] args) throws Exception {
        System.out.println(Web3jFactory.getInstance().getHashHeroByTransactionHashMint("0xb4c94e328c3a18a51c7a86151125bfe7b8c315ce5bba354a1fd2308cbc8a44dd", Collections.singletonList("19")));
    }
}
