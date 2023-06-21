namespace java com.bamisu.log.gamethrift.service.thirdpartypayment

include "exception.thrift"
include "constant.thrift"
include "thirdpartypayment_entities.thrift"
include "user.thrift"

service ThirdPartyPaymentService {
    thirdpartypayment_entities.TUserInfo3rd getUserInfo(1:required constant.short type, 2:required string uname, 3:required string password) throws (1:exception.ThriftSVException e);
    string registerFromWeb(1:required constant.short type, 2:required string uname, 3:required string password) throws (1:exception.ThriftSVException e);
    constant.long recharge(1:string transid, 2:constant.int rtype, 3:constant.int uid, 4:constant.int amount) throws (1:exception.ThriftSVException e);
    constant.int cashout(1:constant.int uid, 2:string otp, 3:constant.int aid, 4:constant.int ct) throws (1:exception.ThriftSVException e);
    string updateOTP(1:constant.int uid) throws (1:exception.ThriftSVException e);
    constant.int transMoney(1:string otp, 2:constant.long sendid, 3:constant.long recid, 4:constant.long mn, 5:string re) throws (1:exception.ThriftSVException e);
    user.TUserInfo getPlayerInfo(1:constant.long uid) throws (1:exception.ThriftSVException e);
    list<thirdpartypayment_entities.TAgency> getAgencyList() throws (1:exception.ThriftSVException e);
    bool checkBankPass(1:constant.int uid, 2:string pw);
    bool subUserMoney(1:constant.int uid, 2:constant.long money);
}