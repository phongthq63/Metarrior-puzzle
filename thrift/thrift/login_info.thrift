namespace java com.bamisu.log.gamethrift.entities

typedef i16 short
typedef i32 int
typedef i64 long

struct TLoginInfo {
    1:long uid,
    2:string uname,
    3:string dname,
    4:string did,
    5:int lastlogin,
    6:int created,
    7:long chip,
}