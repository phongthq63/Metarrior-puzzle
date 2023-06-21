namespace java com.bamisu.log.sdkthrift.entities

typedef i16 short
typedef i32 int
typedef i64 long

struct TLinkAccountResult {
    1:long userID,
    2:int serverID,
    3:int socialNetwork,
}