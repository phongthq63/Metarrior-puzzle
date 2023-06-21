package com.bamisu.log.gameserver.datamodel.nft.entities;

import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.gamelib.entities.TokenResourcePackage;

import java.util.List;

/**
 * Created by Quach Thanh Phong
 * On 5/3/2022 - 2:11 PM
 */
public class TokenTransactionModel {
    public String transactionId;
    public List<TokenResourcePackage> tokens;
    public String action;
    public double fee = 0;


    public static TokenTransactionModel create(String action, String transactionId, List<TokenResourcePackage> tokens) {
        TokenTransactionModel tokenTransactionModel = new TokenTransactionModel();
        tokenTransactionModel.action = action;
        tokenTransactionModel.transactionId = transactionId;
        tokenTransactionModel.tokens = tokens;

        return tokenTransactionModel;
    }

    public static TokenTransactionModel create(String action, String transactionId, List<TokenResourcePackage> tokens, double fee) {
        TokenTransactionModel tokenTransactionModel = new TokenTransactionModel();
        tokenTransactionModel.action = action;
        tokenTransactionModel.transactionId = transactionId;
        tokenTransactionModel.tokens = tokens;
        tokenTransactionModel.fee = fee;

        return tokenTransactionModel;
    }
}
