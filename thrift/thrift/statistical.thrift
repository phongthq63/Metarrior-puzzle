namespace java com.bamisu.log.gamethrift.entities
include "constant.thrift"

struct CCUInfo {
    1:constant.int gid,
    2:constant.int all,
    3:constant.int android,
    4:constant.int ios,
    5:constant.int web,
    6:string gname,
    7:constant.int ingame
}

struct DNewObj {
    1:constant.int count,
    2:string day
}

struct MoneyPerDayObj {
    1:constant.long chip,
    2:constant.int day
}


struct FreeChipObj {
    1:constant.long chip,
    2:constant.int day,
    3:constant.int unum
}

struct TGamefeeInfo {
    1:constant.int day,
    2:constant.long chip,
    3:string gname,
}



