namespace java com.bamisu.log.gamethrift.service.user

include "user.thrift"
include "exception.thrift"
include "constant.thrift"
include "change_user_money.thrift"


service UserService {

	user.TUserInfo getUserInfo(1:required string key, 2:required constant.short type) throws (1:exception.ThriftSVException e);

	string resetPassword(1:required string key, 2:required constant.short type) throws (1:exception.ThriftSVException e);

    constant.int resetSDT(1:required string key, 2:required constant.short type, 3:required string sdt) throws (1:exception.ThriftSVException e);

    string banUsers(1:string uIds, 2:string reason);

    string unbanUsers(1:string uIds, 2:string reason);

    list<user.TUserInfo> getTop(1:required string timeStamp, 2:required constant.int topType);

    list<user.TUserInfo> getSeedingList();

    constant.int changeUserType(1:required string key, 2:required constant.short type, 3:required constant.short userType) throws (1:exception.ThriftSVException e);

    string addUserMoney(1:required string uIds, 2:required constant.long money, 3:required constant.int mType, 4:required string title, 5:required string content, 6:required constant.int reason);

    string addUserItem(1:required string uIds, 2:required constant.int itemId, 3:required constant.int count, 4:required string title, 5:required string content);

    string resetBankPassword(1:required string key, 2:required constant.short type) throws (1:exception.ThriftSVException e);

    list<change_user_money.TChangeUserMoneyObj> changeUserMoney(1:list<change_user_money.TChangeUserMoneyObj> changeUserMoneyObjs);

}

