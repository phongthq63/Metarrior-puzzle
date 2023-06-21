namespace java com.bamisu.log.sdkthrift.service.account

include "../entities/TLoginResult.thrift"
include "../entities/TLinkAccountResult.thrift"
include "../entities/TLinkedAccount.thrift"
include "../entities/TSwitchAccountResult.thrift"
include "../entities/TUserInfo.thrift"
include "../entities/constant.thrift"
include "../exception/exception.thrift"


service AccountService {

	TLoginResult.TLoginResult loginGame(1:required string token, 2:required constant.int serverID, 3:required string clientIP, 4:required constant.int os, 5:required string did) throws (1:exception.ThriftSVException e);

	TLinkAccountResult.TLinkAccountResult linkAccount(1: required string accountID, 2:required constant.long userID, 3:required constant.int serverID, 4:required constant.int socialNetwork, 5:required string socialNetworkToken) throws (1:exception.ThriftSVException e);

	TSwitchAccountResult.TSwitchAccountResult switchAccount(1:required constant.int socialNetwork, 2:required string socialNetworkToken) throws (1:exception.ThriftSVException e);

	void updateLevel(1:required string accountID, 2:required constant.int serverID, 3:required constant.int level) throws (1:exception.ThriftSVException e);

	void updateAvatar(1:required string accountID, 2:required constant.int serverID, 3:required string avatar, 4:required constant.int frame) throws (1:exception.ThriftSVException e);

	void updateDisplayName(1:required string accountID, 2:required constant.int serverID, 3:required string displayName) throws (1:exception.ThriftSVException e);

	void joinServer(1:required string accountID, 2:required constant.int serverID, 3:required constant.long userID, 4:required string displayName, 5:required string avatar, 6:required constant.int avatarFrame, 7:required constant.int level) throws (1:exception.ThriftSVException e);

    void logIAP(1:required string accountID, 2:required constant.int serverId, 3:required constant.long userID, 4:required constant.int gate, 5:required string idAPI, 6:required string purchaseToken, 7:required string transactionId);

    string getUniqueIdSocialNetwork(1:required string accountID, 2:required constant.int socialNetworkID) throws (1:exception.ThriftSVException e);

    i64 getUserId(1:required string socialNetworkKey, 2:required constant.int socialNetworkID, 3:required string serverId) throws (1:exception.ThriftSVException e);

    TLoginResult.TLoginResult usernameLoginGame(1:required string username, 2:required string password, 3:required constant.int serverID, 4:required string clientIP, 5:required constant.int os, 6:required string did) throws (1:exception.ThriftSVException e);

    bool updateUsernameAndPassword(1: required string accountId, 2: required string username, 3: required string password, 4: required string email, 5: required string code)  throws (1:exception.ThriftSVException e);

    bool changePassword(1: required string accountId, 2: required string password) throws (1:exception.ThriftSVException e);

    string getUsername(1: required string accountId) throws (1:exception.ThriftSVException e);

    string getWalletByAccountId(1: required string accountId);

    constant.long linkWallet(1: required string accountId, 2: required string address, 3: required string username, 4: required string password, 5: required constant.int zoneId) throws (1:exception.ThriftSVException e);

    TLinkedAccount.TLinkedAccount getLinkedAccount(1: required string accountId);

    TUserInfo.TUserInfo getUserInfo(1: string walletAddress);
}