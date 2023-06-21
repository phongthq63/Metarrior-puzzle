package com.bamisu.log.sdk.module.sdkthriftserver.handler.nft;

import com.bamisu.log.sdk.module.nft.NFTManager;
import com.bamisu.log.sdkthrift.exception.ThriftSVException;
import com.bamisu.log.sdkthrift.service.nft.NFTService;
import org.apache.thrift.TException;

/**
 * Created by Quach Thanh Phong
 * On 3/12/2022 - 12:06 AM
 */
public class TNFTHandler implements NFTService.Iface {
    @Override
    public boolean haveInstanceTranferToken(String transactionHash) throws ThriftSVException, TException {
        return NFTManager.getInstance().haveInstanceTranferToken(transactionHash);
    }

    @Override
    public boolean saveInstanceTranferToken(String transactionHash, String count, long uid) throws ThriftSVException, TException {
        try{
            NFTManager.getInstance().saveInstanceTranferToken(transactionHash, count, uid);
            return true;

        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
}
