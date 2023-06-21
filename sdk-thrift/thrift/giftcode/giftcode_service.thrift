namespace java com.bamisu.log.sdkthrift.service.giftcode

include "../entities/constant.thrift"
include "../exception/exception.thrift"
include "../entities/TActiveGiftcodeResult.thrift"

service GiftcodeService {
	TActiveGiftcodeResult.TActiveGiftcodeResult activeGiftcode(1:required string code, 2:required constant.int serverID, 3:required string userID, 4:required string accountID ) throws (1:exception.ThriftSVException e);
}