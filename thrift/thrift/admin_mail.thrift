namespace java com.bamisu.log.gamethrift.entities

typedef i16 short
typedef i32 int
typedef i64 long

struct TAdminMail {
    1:long uid,
    2:string title,
    3:string content,
}