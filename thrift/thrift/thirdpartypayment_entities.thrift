namespace java com.bamisu.log.gamethrift.entities

typedef i16 short
typedef i32 int
typedef i64 long

struct TUserInfo3rd {
    1:string uid,
    2:string userName,
    3:string displayName,
    4:string email,
    5:string phone,
    8:long chip,
    9:long xu,
    10:string avatarURL
}

struct TAgency {
    1:string name,
    2:long star,
    3:string displayName,
    4:string phone,
    5:string address,
    9:string fb,
}
