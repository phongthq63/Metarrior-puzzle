namespace java com.bamisu.log.sdkthrift.entities

typedef i16 short
typedef i32 int
typedef i64 long

struct TLoginResult {
    1:string accountID,
    2:string addr,
    3:int port,
    4:string zone,
    5:int serverID,
    6:string token,
    7:string socialNetworkLinked
}