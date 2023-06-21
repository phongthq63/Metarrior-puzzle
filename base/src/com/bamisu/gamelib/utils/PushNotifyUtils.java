package com.bamisu.gamelib.utils;

import com.bamisu.gamelib.base.config.ConfigHandle;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Scanner;

/**
 * Create by Popeye on 12:09 PM, 12/28/2020
 */
public class PushNotifyUtils {
    public static String app_id = ConfigHandle.instance().get("onesignal_app_id");
    public static String key = ConfigHandle.instance().get("onesignal_key");

    //live
//    public static String app_id = "395ed6ca-bba6-4e51-b8e9-4f9029e03eb9";
//    public static String key = "MzU1OWM0NTQtYTBjMS00MDM1LWE4Y2MtNjIxNGNjMzNiMDc5";

    public static void main(String[] args) {
        pushAll("Global Release Reward Pack \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89", "Use gift code RETURN2021 for 10 new heroes and 3000 diamonds!!!");
    }

    public static void pushAll(String title, String content) {
        try {
            String jsonResponse;

            URL url = new URL("https://onesignal.com/api/v1/notifications");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setUseCaches(false);
            con.setDoOutput(true);
            con.setDoInput(true);

            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setRequestProperty("Authorization", "Basic " + key);
            con.setRequestMethod("POST");

            String strJsonBody = "{"
                    + "\"app_id\": \"" + app_id + "\","
                    + "\"included_segments\": [\"Subscribed Users\"],"
                    + "\"data\": {\"foo\": \"bar\"},"
                    + "\"headings\": {\"en\": \"" + title + "\"},"
                    + "\"contents\": {\"en\": \"" + content + "\"}"
                    + "}";


            System.out.println("strJsonBody:\n" + strJsonBody);

            byte[] sendBytes = strJsonBody.getBytes("UTF-8");
            con.setFixedLengthStreamingMode(sendBytes.length);

            OutputStream outputStream = con.getOutputStream();
            outputStream.write(sendBytes);

            int httpResponse = con.getResponseCode();
            System.out.println("httpResponse: " + httpResponse);

            if (httpResponse >= HttpURLConnection.HTTP_OK
                    && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                scanner.close();
            } else {
                Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                scanner.close();
            }
            System.out.println("jsonResponse:\n" + jsonResponse);

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static void push(List<String> ids, String contentID) {
//        System.out.println("++++++++++++++++++++++");
//        System.out.println("Push " + ids.toString() + " " + contentID);
//        System.out.println("++++++++++++++++++++++");
        String title = "";
        String content = "";
        switch (contentID) {
            case "pn001":
                title = "AFK Rewards Full  \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89";
                content = "Your AFK Rewards are full. Collect it now!";
                break;
            case "pn002":
                title = "Check your message! \uD83D\uDECE";
                content = "You have a new unread message! \uD83D\uDCEB\uD83D\uDCEB\uD83D\uDCEB";
                break;
            case "pn003":
                title = "New Alliance Gift! \uD83C\uDF81\uD83C\uDF81\uD83C\uDF81";
                content = "Log in to claim your gift now! \uD83D\uDC4D\uD83D\uDC4D\uD83D\uDC4D";
                break;
            case "pn004":
                title = "Enery Misson Full! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89";
                content = "Let's play \uD83D\uDC4D\uD83D\uDC4D\uD83D\uDC4D";
                break;
            case "pn005":
                title = "Get ticket! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89";
                content = "Let's play \uD83D\uDC4D\uD83D\uDC4D\uD83D\uDC4D";
                break;
        }
        try {
            String jsonResponse;

            URL url = new URL("https://onesignal.com/api/v1/notifications");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setUseCaches(false);
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setRequestProperty("Authorization", "Basic " + key);
            con.setRequestMethod("POST");

            String strJsonBody = "{"
                    + "\"app_id\": \"" + app_id + "\","
                    + "\"include_external_user_ids\": " + Utils.toJson(ids) + ","
                    + "\"channel_for_external_user_ids\": \"push\","
                    + "\"data\": {\"foo\": \"bar\"},"
                    + "\"headings\": {\"en\": \"" + title + "\"},"
                    + "\"contents\": {\"en\": \"" + content + "\"}"
                    + "}";


            System.out.println("strJsonBody:\n" + strJsonBody);

            byte[] sendBytes = strJsonBody.getBytes("UTF-8");
            con.setFixedLengthStreamingMode(sendBytes.length);

            OutputStream outputStream = con.getOutputStream();
            outputStream.write(sendBytes);

            int httpResponse = con.getResponseCode();
            System.out.println("httpResponse: " + httpResponse);

            if (httpResponse >= HttpURLConnection.HTTP_OK && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                scanner.close();
            } else {
                Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                scanner.close();
            }
            System.out.println("jsonResponse:\n" + jsonResponse);

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
