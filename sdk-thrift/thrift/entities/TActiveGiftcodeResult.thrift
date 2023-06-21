namespace java com.bamisu.log.sdkthrift.entities

typedef i16 short
typedef i32 int
typedef i64 long

struct TActiveGiftcodeResult {
    1:int serverID,
    2:string userID,
    3:string accountID,
    4:string gifts
}