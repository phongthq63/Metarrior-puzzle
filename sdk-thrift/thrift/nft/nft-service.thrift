namespace java com.bamisu.log.sdkthrift.service.nft

include "../entities/constant.thrift"
include "../exception/exception.thrift"

service NFTService {
    bool haveInstanceTranferToken(1:required string transactionHash) throws (1:exception.ThriftSVException e);
    bool saveInstanceTranferToken(1:required string transactionHash, 2:required double count, 3:required i64 uid) throws (1:exception.ThriftSVException e);
}