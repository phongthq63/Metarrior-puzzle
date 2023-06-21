package com.bamisu.gamelib.http.entities;

/**
 * Create by Popeye on 12:33 PM, 10/12/2019
 */
public class HttpConstant {
    public final static class RespError{
        public static HttpError ERROR_SYS = new HttpError(1, "Lỗi hệ thống");
        public static HttpError ERROR_INVALID_VALUE = new HttpError(2, "Giá trị không hợp lệ");

        public static HttpError ERROR_CHAR = new HttpError(1300, "Lỗi hệ thống");
        public static HttpError ERROR_MAX_SIZE_CONFIG = new HttpError(1301, "Đã tới giới hạn lưu trữ");
        public static HttpError ERROR_EXSIST_KINGDOM = new HttpError(1302, "Lỗi! Kingdom đã tồn tại");
        public static HttpError ERROR_NOT_EXSIST_KINGDOM = new HttpError(1303, "Lỗi! Kingdom không tồn tại");
        public static HttpError ERROR_EXSIST_CLASS = new HttpError(1304, "Lỗi! Class đã tồn tại");
        public static HttpError ERROR_NOT_EXSIST_CLASS = new HttpError(1305, "Lỗi! Class không tồn tại");
        public static HttpError ERROR_EXSIST_ELEMENT = new HttpError(1306, "Lỗi! Element đã tồn tại");
        public static HttpError ERROR_NOT_EXSIST_ELEMENT = new HttpError(1307, "Lỗi! Element không tồn tại");
        public static HttpError ERROR_EXSIST_ROLE = new HttpError(1308, "Lỗi! Role đã tồn tại");
        public static HttpError ERROR_NOT_EXSIST_ROLE = new HttpError(1309, "Lỗi! Role không tồn tại");
        public static HttpError ERROR_EXSIST_CHARACTER = new HttpError(1310, "Lỗi! Character đã tồn tại");
        public static HttpError ERROR_NOT_EXSIST_CHARACTER = new HttpError(1311, "Lỗi! Character không tồn tại");
        public static HttpError ERROR_NOT_EXSIST_LEVEL = new HttpError(1312, "Lỗi! Level không tồn tại");

        public static HttpError ERROR_ITEM = new HttpError(1400, "Lỗi hệ thống");
        public static HttpError ERROR_EXSIST_AVATAR = new HttpError(1402, "Lỗi! Avatar đã tồn tại");
        public static HttpError ERROR_NOT_EXSIST_AVATAR = new HttpError(1402, "Lỗi! Avatar đã tồn tại");
    }
}
