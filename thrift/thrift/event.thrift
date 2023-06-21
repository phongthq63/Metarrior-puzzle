namespace java com.bamisu.log.gamethrift.entities

typedef i16 short
typedef i32 int
typedef i64 long

struct TEventInfo {
    1:long eid,
    2:string title,
    3:string content,
    4:string url,
    5:int startTime,
    6:int endTime,
    7:int status,
    8:string img,
}