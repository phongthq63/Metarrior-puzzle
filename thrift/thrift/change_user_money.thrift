namespace java com.bamisu.log.gamethrift.entities

typedef i16 short
typedef i32 int
typedef i64 long

struct TChangeUserMoneyObj {
    1:long uid,
    2:long money,
    3:int mtype
    4:short re,
}