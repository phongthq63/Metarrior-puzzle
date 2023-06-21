namespace java com.bamisu.log.gamethrift.entities

typedef i16 short
typedef i32 int
typedef i64 long

struct TUserGiftcodeInfo {
    1:int time,
    2:long uid,
    3:string dname,
    4:long chip,
    5:long xu,
}