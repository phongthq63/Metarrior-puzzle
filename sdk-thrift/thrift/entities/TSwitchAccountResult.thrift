namespace java com.bamisu.log.sdkthrift.entities

typedef i16 short
typedef i32 int
typedef i64 long

struct TSwitchAccountResult {
    1:string loginKey,
    2:string addr,
    3:int port,
    4:string zone,
    5:int serverID
}