namespace java com.bamisu.log.sdkthrift.service.multiserver

include "../entities/constant.thrift"
include "../exception/exception.thrift"

service MultiServerService {
	bool registerServer(1:required constant.int serverID, 2:required string serverName, 3:required string addr, 4:required constant.int port, 5:required string zone) throws (1:exception.ThriftSVException e);
	bool unRegisterServer(1:required constant.int id) throws (1:exception.ThriftSVException e);
	string getServerInfo(1:required constant.int serverID) throws (1:exception.ThriftSVException e);
	constant.int getServerCount() throws (1:exception.ThriftSVException e);
	string getJoinedServer(1:required string accountID) throws (1:exception.ThriftSVException e);
}