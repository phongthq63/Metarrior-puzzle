namespace java com.bamisu.log.gamethrift.entities

typedef i16 short
typedef i32 int
typedef i64 long

/**
    uid	    long	star
    us	    string	tên đăng nhập
    dn	    string	tên hiển thị
    em	    string
    ph	    string
    rt	    int	    thời gian đăng ký
    ll	    int	    thời gian đăng nhập gần nhất
    chip	long
    xu	    long
    item	long	tổng tiền vật phẩm
    im	    long	tổng tiền nạp
    om	    long	tổng tiền đổi
*/
struct TUserInfo {
    1:string uid,
    2:string userName,
    3:string displayName,
    4:string email,
    5:string phone,
    6:int registerTime,
    7:int loginTime,
    8:long chip,
    9:long xu,
    10:list<int> item,
    11:string avatarURL,
    12:long bankMoney,
    13:long gold,
    14:long diamond,
    15:long token,
    17:long voucherPhoenix,
    18:long voucherDragon
}

