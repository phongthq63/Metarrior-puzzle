package com.bamisu.log.nft.manager;

import com.bamisu.log.nft.contract.Metarrior;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.tx.gas.StaticGasProvider;

import java.math.BigInteger;

/**
 * Created by Quach Thanh Phong
 * On 2/16/2022 - 12:28 AM
 */
public class SoulgemContractManager {
    private static String addressContract = "0xd3565c75d175e8fcd832da71fa009601c8cc3741";
    private String privateKey = "9421684d165770c12d92b991e6218c153d39e33aae5c631cac42f01078232502";
    private Metarrior contract;

    public SoulgemContractManager(Web3j web3j) {
        this.contract = Metarrior.load(addressContract, web3j, Credentials.create(privateKey), new DefaultGasProvider());
    }

    public Metarrior getContract() {
        return contract;
    }

    public static String getAddressContract() {
        return addressContract;
    }
}
