package com.bamisu.log.nft;

import com.bamisu.log.nft.contract.Hero;
import com.bamisu.log.nft.entities.HeroToken;
import com.bamisu.log.nft.entities.HeroTokenTranfer;
//import org.apache.commons.codec.binary.Hex;
//import org.apache.commons.codec.binary.StringUtils;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Keys;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.web3j.utils.Numeric.hexStringToByteArray;

/**
 * Created by Quach Thanh Phong
 * On 2/19/2022 - 2:13 PM
 */
public class Test {
    public static Web3jClient web3jClient;
    public static Admin web3j;

    static {
        try {
            web3jClient = Web3jClient.build("https://data-seed-prebsc-1-s1.binance.org:8545");
            web3j = (Admin) web3jClient.getWeb3j();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final BigInteger GAS_LIMIT = BigInteger.valueOf(20000000L);
    private static final BigInteger GAS_PRICE = BigInteger.valueOf(10000000000L);

    public static void main (String[] args) throws Exception {

//        Transaction transaction = web3j.ethGetTransactionByHash("0x4a1d6f4ffb5d530df276bef24a859218e24c4d7b0d27e4cd0b005a535de81a2d").send().getTransaction().get();
        TransactionReceipt transactionReceipt = web3j.ethGetTransactionReceipt("0x5f7f546636efce76692d41545a6128b749928708e7b63d63fb4b6d11d0f62b70").send().getTransactionReceipt().get();


        String data = "0x000000000000000000000000000000000000000000000000000000000000024f0000000000000000000000000000000000000000000000000000000000000040000000000000000000000000000000000000000000000000000000000000001d6865726f313634383031313034333038335f50474d6d382d454a796844000000";
        System.out.println(data.length());
        System.out.println(hexToAscii("6865726f313634383031313034333038335f50474d6d382d454a796844000000"));
//
//
//
//
//        List<HeroToken> a = Web3jFactory.getInstance().getHashHeroByTransactionHashMint("0x229eb3caa1208451d6de79dce29b1cfddf6e347cb190e6d95e6efd32898fd62d", Arrays.asList("606"));
//        List<HeroToken> b = Web3jFactory.getInstance().getHashHeroByTransactionHashBurn("0x5f7f546636efce76692d41545a6128b749928708e7b63d63fb4b6d11d0f62b70", Arrays.asList("591", "602"));
//
//
//
//
//
//        System.out.println(Convert.fromWei(new BigDecimal(web3jClient.getDecimal(transactionReceipt.getLogs().get(0).getData())), Convert.Unit.ETHER));

//        EthGetTransactionReceipt ethGetTransactionReceipt = web3j.ethGetTransactionReceipt("0x602fded944cd9c3867c6796a09d8530ec797a593f087183cd08ebf5ff8940c41").sendAsync().get(1, TimeUnit.DAYS);
//        TransactionReceipt transactionReceipt = ethGetTransactionReceipt.getTransactionReceipt().orElse(null);
//
//        System.out.println(new NFTContractManager(web3j).getContract().getHeroByTokenId(web3jClient.getDecimal(transactionReceipt.getLogs().get(0).getTopics().get(3))).send());

    }

    private static String hexToAscii(String hexStr) {
        StringBuilder output = new StringBuilder("");

        for (int i = 0; i < hexStr.length(); i += 2) {
            String str = hexStr.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }

        return output.toString();
    }
}
