package com.bamisu.log.nft.manager;

import com.bamisu.log.nft.contract.Hero;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.tx.gas.DefaultGasProvider;

/**
 * Created by Quach Thanh Phong
 * On 2/15/2022 - 11:45 PM
 */
public class HeroContractManager {
    private String addressContract = "0xac34e5056a31FFa05eff682C49Bf11635a0eFB38";
    private String privateKey = "9421684d165770c12d92b991e6218c153d39e33aae5c631cac42f01078232502";
    private Hero contract;

    public HeroContractManager(Web3j web3j) {
//        this.contract = NFT.load(addressContract, web3j, Credentials.create(privateKey), new DefaultGasProvider());
        this.contract = Hero.load(addressContract, web3j, Credentials.create(privateKey), new DefaultGasProvider());
    }

    public Hero getContract() {
        return contract;
    }
}
