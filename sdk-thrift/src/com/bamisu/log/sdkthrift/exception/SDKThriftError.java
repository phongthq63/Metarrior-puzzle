package com.bamisu.log.sdkthrift.exception;

public class SDKThriftError {
    public static final ThriftSVException SYSTEM_ERROR = new ThriftSVException(1, "Lỗi hệ thống");

    //auth
    public static final ThriftSVException ACCOUNT_HAVE_LINKED = new ThriftSVException(2200, "ACCOUNT_HAVE_LINKED");
    public static final ThriftSVException SOCIAL_ACCOUNT_HAVE_LINKED = new ThriftSVException(2201, "SOCIAL_ACCOUNT_HAVE_LINKED");
    public static final ThriftSVException WRONG_USERNAME_OR_PASSWORD = new ThriftSVException(2202, "WRONG_USERNAME_OR_PASSWORD");
    public static final ThriftSVException INVALID_USERNAME_OR_PASSWORD = new ThriftSVException(2203, "WRONG_USERNAME_OR_PASSWORD");

    //giftcode
    public static final ThriftSVException GIFTCODE_NOT_FOUND = new ThriftSVException(3000, "GIFTCODE_NOT_FOUND");
    public static final ThriftSVException GIFTCODE_EXPIRED = new ThriftSVException(3001, "GIFTCODE_EXPIRED");
    public static final ThriftSVException GIFTCODE_GONE = new ThriftSVException(3002, "GIFTCODE_GONE");
    public static final ThriftSVException HAVE_USED = new ThriftSVException(3003, "HAVE_USED");

    //invitecode
    public static final ThriftSVException HAVE_INPUT_INVITE_CODE = new ThriftSVException(4000, "HAVE_INPUT_INVITE_CODE");
}
