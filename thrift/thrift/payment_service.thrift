namespace java com.bamisu.log.gamethrift.service.payment

include "exception.thrift"
include "constant.thrift"

service PaymentService {
    string verifyCard(1:string data);

    string rejectCashOut(1:string data, 2:string reason);

    bool resendCardInfo(1:string transid);
}