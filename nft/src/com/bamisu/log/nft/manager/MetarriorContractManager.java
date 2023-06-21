package com.bamisu.log.nft.manager;

import com.bamisu.log.nft.contract.Metarrior;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.tx.gas.StaticGasProvider;

import java.math.BigInteger;

/**
 * Created by Quach Thanh Phong
 * On 2/16/2022 - 12:28 AM
 */
public class MetarriorContractManager {
    private final BigInteger GAS_LIMIT = BigInteger.valueOf(20000000L);
    private final BigInteger GAS_PRICE = BigInteger.valueOf(10000000000L);
    private static String addressContract = "0xf6de04BA8b9d531A110285921adFbB3CC3C84635";
    private String privateKey = "9421684d165770c12d92b991e6218c153d39e33aae5c631cac42f01078232502";
    private Metarrior contract;

    public MetarriorContractManager(Web3j web3j) {
        this.contract = Metarrior.load(addressContract, web3j, Credentials.create(privateKey), new StaticGasProvider(GAS_PRICE, GAS_LIMIT));
    }

    public Metarrior getContract() {
        return contract;
    }

    public static String getAddressContract() {
        return addressContract;
    }
}
