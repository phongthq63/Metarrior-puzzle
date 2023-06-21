namespace java com.bamisu.log.gamethrift.service.statistical

include "exception.thrift"
include "constant.thrift"
include "statistical.thrift"
include "user.thrift"
include "itemtrans.thrift"
include "rankinfo.thrift"
include "register_info.thrift"
include "login_info.thrift"
include "user_giftcode_info.thrift"

service StatisticalService {
    list<statistical.CCUInfo> getCCU(1:constant.int type);
    list<statistical.DNewObj> getDNew(1:constant.int fr, 2:constant.int to);
    list<statistical.MoneyPerDayObj> getMoneyIn(1:constant.int fr, 2:constant.int to);
    list<statistical.MoneyPerDayObj> getMoneyOut(1:constant.int fr, 2:constant.int to);
    list<statistical.FreeChipObj> getFreeChip(1:constant.int fr, 2:constant.int to);
    list<itemtrans.TItemTransInfo> getItemTrans(1:constant.int fr, 2:constant.int to, 3:constant.long uid, 4:constant.int record);
    list<rankinfo.TRankInfo> getRank(1:constant.int type);
    list<register_info.TRegisterInfo> getAccRegisterOnDevice(1:string did, 2:constant.int type);
    list<login_info.TLoginInfo> getAccLoginOnDevice(1:string did, 2:constant.int type);
    list<user_giftcode_info.TUserGiftcodeInfo> GetGiftcodeInfo(1:string code);
    list<statistical.TGamefeeInfo> getGamefee(1:constant.int gid, 2:constant.int fr, 3:constant.int to);
    string getAllTransactionType();
}