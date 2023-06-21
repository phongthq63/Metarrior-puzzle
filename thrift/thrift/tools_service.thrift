namespace java com.bamisu.log.gamethrift.service.tools

include "exception.thrift"
include "constant.thrift"
include "noticonfig.thrift"
include "user.thrift"

service ToolsService {
    string addBot(1:string dname) throws (1:exception.ThriftSVException e);
    string createConfig(1:string botIds, 2:constant.int gameId, 3:string betArr, 4:string startTime, 5:string endTime, 6:constant.int rate) throws (1:exception.ThriftSVException e);
    list<user.TUserInfo> getListBot() throws (1:exception.ThriftSVException e);
    string deleteBot(1:constant.int botId) throws (1:exception.ThriftSVException e);
    list<noticonfig.TNotiConfig> getListNotiConfig() throws (1:exception.ThriftSVException e);
    string deleteNotiConfig(1:constant.int cId) throws (1:exception.ThriftSVException e);
    string setJackpotBalance(1:constant.int balance) throws (1:exception.ThriftSVException e);
    string getDayBonusInfo() throws (1:exception.ThriftSVException e);
    string setDayBonusInfo(1:constant.long maxMoney) throws (1:exception.ThriftSVException e);
    string getChat(1:constant.int gid) throws (1:exception.ThriftSVException e);
    string deleteChat(1:constant.int gid, 2:string hashCodeChat) throws (1:exception.ThriftSVException e);
    string banChat(1:constant.int gid, 2:constant.long uid, 3:constant.int bantime) throws (1:exception.ThriftSVException e);
    string unBanChat(1:constant.int gid, 2:constant.long uid) throws (1:exception.ThriftSVException e);
    string getListBan(1:constant.int gid) throws (1:exception.ThriftSVException e);
    string adminChat(1:constant.int gid, 2:string mess) throws (1:exception.ThriftSVException e);
    string getBanChatList(1:constant.int gid) throws (1:exception.ThriftSVException e);
}