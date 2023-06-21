namespace java com.bamisu.log.gamethrift.service.mail

include "exception.thrift"

service MailService {
    string sendToPlayer(1:required string zoneName, 2:required list<i64> uids, 3:required string title, 4:required string content, 5:required string gift) throws (1:exception.ThriftSVException e);
}