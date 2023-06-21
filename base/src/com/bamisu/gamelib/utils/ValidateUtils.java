package com.bamisu.gamelib.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Popeye on 6/22/2017.
 */
public class ValidateUtils {
    public static void main(String[] args){

    }

    public static boolean isDisplayName(String dName) {
//        if(dName.contains("admin") || dName.contains("chopchop")) return false;
//        if (dName.indexOf("guest") == 0 || dName.indexOf("Guest") == 0) return false;
//        if (Utils.isID(dName)) return false;
//        return dName.matches("^[ 0-9a-zA-z_ÀÁÂÃÈÉÊÌÍÒÓÔÕÙÚĂĐĨŨƠàáâãèéêìíòóôõùúăđĩũơƯĂẠẢẤẦẨẪẬẮẰẲẴẶẸẺẼẾỀỂưăạảấầẩẫậắằẳẵặẹẻẽếềểỄỆỈỊỌỎỐỒỔỖỘỚỜỞỠỢỤỦỨỪễệỉịọỏốồổỗộớờởỡợụủứừỬỮỰỲỴÝỶỸửữựỳỵỷỹ]{6,15}$");
//        if(!dName.matches("^[ 0-9a-zA-z]{4,14}$")) return false;
        if(dName.length() < 2 || dName.length() > 20) return false;
        if(dName.charAt(0) == ' ' || dName.charAt(dName.length() - 1) == ' ') return false;

        Pattern regex = Pattern.compile("[$&+,:;=\\\\?@#|/'<>.^*()%!-]");
        if(regex.matcher(dName).find()) return false;
        //chỉ chứa 1 dấu cách
//        int count = 0;
//        for(int i = 0; i < dName.length(); i++){
//            if(dName.charAt(i) == ' '){
//                count ++;
//            }
//        }
//        if(count > 1) return false;

        return true;
    }

    public static boolean isPhoneNumber(String phone) {
        return phone.matches("^[0-9\\-\\+]{9,15}$");
    }

    public static boolean isStatusText(String statusText) {
        return statusText.length() < 100;
    }

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static boolean isEmail(String email) {
        email.toLowerCase();
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        return matcher.find();
    }

    public static boolean isSex(short sex) {
        return (sex >= 0 && sex <= 2);
    }

    public static boolean isMailTitle(String title) {
        return title.length() >= 6 && title.length() <= 50;
    }

    public static boolean isMailContent(String content) {
        return content.length() >= 6 && content.length() <= 200;
    }

    public static boolean isBankPassword(String pw) {
//        if(!pw.matches("^[0-9a-zA-z]{6,20}$")) return false;
//        return true;
        return pw.length() >= 6 && pw.length() <= 20;
    }

    public static boolean isLanguageID(String languageID) {
        return true;
    }

    public static boolean isGiftcode(String code) {
        return code.matches("^[0-9a-zA-z]{1,20}$");
    }
}
