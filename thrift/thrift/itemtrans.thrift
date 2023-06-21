namespace java com.bamisu.log.gamethrift.entities

typedef i16 short
typedef i32 int
typedef i64 long

struct TItemTransInfo {
    1:string act,
    2:string sender,
    3:string reciver,
    4:int time,
    5:string des,
    6:int item,
    7:int qtt,
    8:string iname,
}