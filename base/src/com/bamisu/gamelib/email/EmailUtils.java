package com.bamisu.gamelib.email;

import com.bamisu.gamelib.base.config.ConfigHandle;
import com.bamisu.gamelib.email.exception.SendingEmailTooFastException;
import com.bamisu.gamelib.utils.Utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Create by Popeye on 9:21 AM, 4/29/2020
 */
public class EmailUtils {
    public static EmailUtils instance;

    public static EmailUtils getInstance(){
        if(instance == null){
            instance = new EmailUtils();
        }
        return instance;
    }

    public static void main(String[] args){

    }

    public EmailUtils() {
        mapTimeSendEmail = new HashMap<>();
        suportEmaillReceiver = ConfigHandle.instance().get("email_suport_receiver");
        suportEmail = new Email(
                ConfigHandle.instance().get("email_suport"),
                ConfigHandle.instance().get("email_suport_pass")
        );
//        delayTimeToSendMailSuport = Integer.parseInt(ConfigHandle.instance().get("email_suport_delay"));
        delayTimeToSendMailSuport = 30;
    }

    public Map<String, Integer> mapTimeSendEmail;

    public String suportEmaillReceiver;
    public Email suportEmail;
    public int delayTimeToSendMailSuport;

    /**
     *
     * @param userID   để tránh việc người chơi gửi quá nhiều email
     * @param userEmail
     * @param subTitle
     * @param content
     */
    public boolean sendSubportMail(int serverID, long userID, String userEmail, String subTitle, String content) throws SendingEmailTooFastException {
        String key = userID + "_" + userID;
        if(mapTimeSendEmail.containsKey(key)){
            if(Utils.getTimestampInSecond() - mapTimeSendEmail.get(key) < delayTimeToSendMailSuport){
                throw new SendingEmailTooFastException();
            }
        }

        String mContent = "User email: " + userEmail + "" +
                "\n" +
                "Subject:" + subTitle +
                "\n" +
                "Content: " + content;
        if(suportEmail.sendMail(suportEmaillReceiver, "Requires support of the PUZZLE game player", mContent)){
            mapTimeSendEmail.put(key, Utils.getTimestampInSecond());
            return true;
        }

        return false;
    }
}
