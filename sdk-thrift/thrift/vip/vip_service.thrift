namespace java com.bamisu.log.sdkthrift.service.vip

include "../entities/constant.thrift"
include "../exception/exception.thrift"

service VipService {
	string getVip(1:required string accountID) throws (1:exception.ThriftSVException e);
	string addVip(1:required string accountID, 2:required string jsonDataListVipData) throws (1:exception.ThriftSVException e);
	bool canTakeFeeVip(1:required string accountID) throws (1:exception.ThriftSVException e);
}