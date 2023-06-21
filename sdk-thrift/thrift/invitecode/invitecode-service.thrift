namespace java com.bamisu.log.sdkthrift.service.invitecode

include "../entities/constant.thrift"
include "../exception/exception.thrift"

service InviteService {
    bool haveInputInviteCode(1:required string accountID) throws (1:exception.ThriftSVException e);
    bool haveExsistInviteCode(1:required string inviteCode) throws (1:exception.ThriftSVException e);
    string inputInviteCode(1:required string accountID, 2:required string inviteCode) throws (1:exception.ThriftSVException e);
	string getUserInviteModel(1:required string accountID) throws (1:exception.ThriftSVException e);
    bool canRewardInviteBonus(1:required string accountID, 2:required string idBonus, 3:required i32 point) throws (1:exception.ThriftSVException e);
    bool rewardInviteBonus(1:required string accountID, 2:required string idBonus, 3:required i32 point) throws (1:exception.ThriftSVException e);
    bool updateRewardInviteDetail(1:required string accountID, 2:required string jsonUpdate) throws (1:exception.ThriftSVException e);
}