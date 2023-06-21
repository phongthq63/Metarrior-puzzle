namespace java com.bamisu.log.sdkthrift.service.iap

include "../entities/constant.thrift"
include "../exception/exception.thrift"

service IAPService {
    bool haveInstanceBuyIAP(1:required string accountID, 2:required string purchaseToken) throws (1:exception.ThriftSVException e);
    bool saveInstanceBuyIAP(1:required string accountID, 2:required string purchaseToken) throws (1:exception.ThriftSVException e);
}