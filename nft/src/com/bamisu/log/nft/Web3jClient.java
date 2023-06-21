package com.bamisu.log.nft;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.core.methods.response.EthBlockNumber;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.math.BigInteger;

/**
 * Created by Quach Thanh Phong
 * On 2/13/2022 - 8:43 PM
 */
public class Web3jClient {
    private String API_URL;
    private Admin web3j;


    private Web3jClient() {}
    private Web3jClient(String url) throws IOException {
        this.API_URL = url;
        HttpService http = new HttpService(API_URL);
        this.web3j = Admin.build(http);

        Web3ClientVersion web3ClientVersion = web3j.web3ClientVersion().send();
        System.out.println(web3ClientVersion.getWeb3ClientVersion());
        EthBlockNumber result = web3j.ethBlockNumber().send();
        System.out.println(" The Block Number is: " + result.getBlockNumber().toString());
    }

    public static Web3jClient build(String url) throws IOException {
        return new Web3jClient(url);
    }

    public String getAPI_URL() {
        return API_URL;
    }

    public Web3j getWeb3j() {
        return web3j;
    }

    public BigInteger getDecimal(String hexadecimal){
        String textAfterPrefix = hexadecimal;
        if(hexadecimal.startsWith("0x")) {
            textAfterPrefix = hexadecimal.substring(2);
        }

        int length = textAfterPrefix.length();
        BigInteger sum = BigInteger.ZERO;
        for (int i = 0; i < textAfterPrefix.length(); i++) {
            sum = sum.add(new BigInteger(Character.toString(textAfterPrefix.charAt(i)), 16).multiply(BigInteger.valueOf(16).pow(length - 1 - i)));
        }

        return sum;
    }

    public BigInteger toWei(long value) {
        return BigInteger.valueOf(value).multiply(BigInteger.TEN.pow(18));
    }
}
