namespace java com.bamisu.log.gamethrift.service.notify

include "exception.thrift"
include "constant.thrift"
include "admin_mail.thrift"

service NotifyService {
    void sendAdminNotify(1:string mess, 2:constant.int delay, 3:constant.int number, 4:constant.int period);
    void sendMaintenanceNotify(1:string content, 2:constant.int delay);
    void sendMail(1:string ids, 2:string title, 3:string content);
    void sendMobileNoti(1:string content, 2:constant.int delay);
    void sendAdminMail(1:list<admin_mail.TAdminMail> mails);
}