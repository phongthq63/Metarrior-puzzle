namespace java com.bamisu.log.gamethrift.entities

typedef i16 short
typedef i32 int
typedef i64 long

struct TNotiConfig {
    1:int star,
    2:int gameId,
    3:string startTime,
    4:string endTime,
    5:int rate,
    6:string botIds
    7:string bets
}