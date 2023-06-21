package com.bamisu.gamelib.utils;


import com.bamisu.gamelib.utils.business.Debug;
import com.bamisu.gamelib.utils.business.Debug;
import org.apache.log4j.Logger;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author tungvc
 */
public class HttpRequestUtils {
    private static Logger logger = Logger.getLogger(HttpRequestUtils.class);

    public static String buildCurrentUri(HttpServletRequest req) {
        return "http://" + req.getServerName() + req.getRequestURI() + "?" + req.getQueryString();
    }

    public static void redirect(HttpServletResponse resp, String urlRedirect) {
        try {
            resp.sendRedirect(urlRedirect);
        } catch (Exception ex) {
        }
    }

    public static void setCookie(String cookieName, String value, int expire, boolean httponly, String path, String domain,
                                 HttpServletRequest req, HttpServletResponse resp) {
        try {
            String strExpire = "";
            if (expire == 0) { // expire now
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.YEAR, -1);
                String ex = formatDate("EEE, dd-MMM-yyyy HH:mm:ss zzz", cal.getTime());
                strExpire = ";Expires=" + ex;
            } else if (expire > 0) {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.MILLISECOND, expire * 1000);
                String ex = formatDate("EEE, dd-MMM-yyyy HH:mm:ss zzz", cal.getTime());
                strExpire = ";Expires=" + ex;
            }
            // else expire < -1: expires after browser is closed.

            String strHttponly = "";
            if (httponly == true) {
                strHttponly = ";HttpOnly";
            }

            String headerValue = cookieName + "=" + value + ";Path=" + path + ";Domain=" + domain + strExpire + strHttponly;
            resp.setHeader("P3P", "CP=\"NOI ADM DEV PSAi COM NAV OUR OTRo STP IND DEM\"");
            resp.addHeader("Set-Cookie", headerValue);
        } catch (Exception e) {
        }
    }

    public static String getCookie(HttpServletRequest req, String name) {
        Map<String, String> ret = getCookieMap(req);
        if (ret == null) {
            return null;
        }
        return ret.get(name);
    }

    public static Map<String, String> getCookieMap(HttpServletRequest req) {
        @SuppressWarnings("unchecked")
        Map<String, String> ret = (Map<String, String>) req.getAttribute("zme.cookies");
        if (ret != null) {
            return ret;
        }
        ret = new HashMap<String, String>();

        try {
            Cookie[] cookies = req.getCookies();

            if (cookies != null) {
                for (int i = 0; i < cookies.length; i++) {
                    try {
                        // String name = cookies[i].getName();
                        String name = URLDecoder.decode(cookies[i].getName(), "UTF-8");
                        String value = cookies[i].getValue();
                        if (value.toLowerCase().equals("deleted")) {
                            continue;
                        }

                        ret.put(name, value);
                        if (i > 50) {
                            break;
                        }
                    } catch (Exception ex) {
                    }
                }
            }
        } catch (Exception ex) {
        }
        req.setAttribute("zme.cookies", ret);
        return ret;
    }

    public static String formatDate(String format, Date d) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(format);
            return formatter.format(d);
        } catch (Exception e) {
            return null;
        }
    }

    public static String buildParamRequestString(HashMap<String, String> mapParam) {
        String str = "";
        for (Map.Entry<String, String> entry : mapParam.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (str.equals("")) {
                str += key + "=" + value;
            } else {
                str += "&" + key + "=" + value;
            }
        }
        return str;
    }

    // public static String buildParamRequestString(HashMap<String, String>
    // params) {
    // try {
    // StringBuffer requestParams = new StringBuffer();
    // if (params != null && params.size() > 0) {
    // Iterator<String> paramIterator = params.keySet().iterator();
    // while (paramIterator.hasNext()) {
    // String key = paramIterator.next();
    // String value = params.get(key);
    // requestParams.append(URLEncoder.encode(key, "UTF-8"));
    // requestParams.append("=").append(URLEncoder.encode(value, "UTF-8"));
    // requestParams.append("&");
    // }
    // return requestParams.toString();
    // }
    // } catch (Exception e) {
    // }
    // return "";
    // }

    /**
     * @param requestUrl
     * @param method     POST or GET
     * @param params
     * @return
     * @throws IOException
     */
//    public static String[] sendHttpRequest(String requestUrl, String method, HashMap<String, String> params) throws IOException {
//        List<String> response = new ArrayList<String>();
//        String requestParam = buildParamRequestString(params);
//
//        Debug.trace("request:" + requestUrl + "?" + requestParam);
//        logger.trace("request:" + requestUrl + "?" + requestParam);
//
//        URL url = new URL(requestUrl);
//        URLConnection urlConn = url.openConnection();
//        urlConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
//        urlConn.setUseCaches(false);
//
//        // the request will return a response
//        urlConn.setDoInput(true);
//
//        if ("POST".equals(method)) {
//            // set request method to POST
//            urlConn.setDoOutput(true);
//        } else {
//            // set request method to GET
//            urlConn.setDoOutput(false);
//        }
//
//        if ("POST".equals(method) && params != null && params.size() > 0) {
//            OutputStreamWriter writer = new OutputStreamWriter(urlConn.getOutputStream());
//            writer.write(requestParam);
//            writer.flush();
//        }
//
//        // reads response, store line by line in an array of Strings
//        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
//
//        String line = "";
//        while ((line = reader.readLine()) != null) {
//            response.add(line);
//        }
//
//        reader.close();
//        return response.toArray(new String[0]);
//    }
    public static String[] sendHttpRequest(String requestUrl, String method, HashMap<String, String> params) throws IOException {
        List<String> response = new ArrayList();
        String requestParam = buildParamRequestString(params);
        Debug.trace("request:" + requestUrl + "?" + requestParam);
        logger.trace("request:" + requestUrl + "?" + requestParam);
        URL url = new URL(requestUrl);
        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
        urlConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
        urlConn.setUseCaches(false);
        urlConn.setDoInput(true);
        if ("POST".equals(method)) {
            urlConn.setRequestMethod("POST");
            urlConn.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
            urlConn.setRequestProperty("Accept", "*/*");
            urlConn.setDoOutput(true);
        } else {
            urlConn.setDoOutput(false);
        }

        if ("POST".equals(method) && params != null && params.size() > 0) {
            urlConn.getOutputStream().write(requestParam.getBytes("UTF-8"));
            urlConn.getOutputStream().flush();
            urlConn.getOutputStream().close();
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
        String line = "";

        while ((line = reader.readLine()) != null) {
            response.add(line);
        }

        reader.close();
        urlConn.disconnect();
        return (String[]) response.toArray(new String[0]);
    }

    public static String sendHttpRequest(String requestUrl, String method, String jsonData) throws IOException {
        URL url = new URL(requestUrl);
        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
        urlConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
        urlConn.setUseCaches(false);
        urlConn.setDoInput(true);
        if("POST".equals(method)){
            urlConn.setRequestMethod("POST");
            urlConn.setDoOutput(true);
            urlConn.setRequestProperty("Content-Type", "application/json");
            urlConn.setRequestProperty("Accept", "application/json");
        }else {
            urlConn.setDoOutput(false);
        }

        if ("POST".equals(method) && jsonData != null && !jsonData.isEmpty()) {
            OutputStreamWriter osw = new OutputStreamWriter(urlConn.getOutputStream());
            osw.write(jsonData);
            osw.flush();
            osw.close();
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
        StringBuilder builder = new StringBuilder();
        String line = "";

        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }

        reader.close();
        urlConn.disconnect();

        return builder.toString();
    }

    public static String sendHttpRequestToFB(String requestUrl, String method, HashMap<String, String> params) throws IOException {
        List<String> response = new ArrayList();
        String requestParam = buildParamRequestString(params);
        Debug.trace("request:" + requestUrl + "?" + requestParam);
        logger.trace("request:" + requestUrl + "?" + requestParam);
        URL url = new URL(requestUrl);
        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
        urlConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
        urlConn.setUseCaches(false);
        urlConn.setDoInput(true);
        if ("POST".equals(method)) {
            urlConn.setRequestMethod("POST");
            urlConn.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
            urlConn.setRequestProperty("Accept", "*/*");
            urlConn.setDoOutput(true);
        } else {
            urlConn.setDoOutput(false);
        }

        if ("POST".equals(method) && params != null && params.size() > 0) {
            urlConn.getOutputStream().write(requestParam.getBytes("UTF-8"));
            urlConn.getOutputStream().flush();
            urlConn.getOutputStream().close();
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
        String message = org.apache.commons.io.IOUtils.toString(reader);
        reader.close();
        urlConn.disconnect();
        return message;
    }

    public static String sendHttpRequest2(String requestUrl, String method, HashMap<String, String> params) throws IOException {
        String[] arrResponse = sendHttpRequest(requestUrl, method, params);
        if (arrResponse != null && arrResponse.length > 0) {
            return arrResponse[0];
        }
        return "";
    }

    public static String post(String requestUrl, HashMap<String, String> params) throws IOException {
        String[] arrResponse = sendHttpRequest(requestUrl, "POST", params);
        if (arrResponse != null && arrResponse.length > 0) {
            return arrResponse[0];
        }
        return "";
    }

    public static String sendHttpRequestFB(String requestUrl, String method, HashMap<String, String> params) throws IOException {
        String res = sendHttpRequestToFB(requestUrl, method, params);
        return res;
    }

    /**
     * get array respone
     *
     * @param requestUrl
     * @param method
     * @param params
     * @return
     * @throws IOException
     */
    public static String sendHttpRequest3(String requestUrl, String method, HashMap<String, String> params) throws IOException {
        String[] arrResponse = sendHttpRequest(requestUrl, method, params);
        if (arrResponse != null && arrResponse.length > 0) {
            String ret = "";
            for (int i = 0; i < arrResponse.length; ++i) {
                ret += arrResponse[i];

            }
            return ret;
        }
        return "";
    }

    public static String getClientIP(HttpServletRequest request) {
        String clientIp = request.getHeader("X-FORWARDED-FOR");
        if (clientIp == null || clientIp.length() == 0) {
            clientIp = request.getHeader("X-Forwarded-For");
        }
        if (clientIp == null || clientIp.length() == 0) {
            clientIp = request.getHeader("x-forwarded-for");
        }
        if (clientIp == null || clientIp.length() == 0) {
            clientIp = request.getRemoteAddr();
        }
        return clientIp;
    }

    public static String sendHttpsRequest2(String requestUrl, String method, HashMap<String, String> params) throws IOException {
        String[] arrResponse = sendHttpsrequest(requestUrl, method, params);
        if (arrResponse != null && arrResponse.length > 0) {
            return arrResponse[0];
        }
        return "";
    }

    public static String[] sendHttpsrequest(String requestUrl, String method, HashMap<String, String> params) throws IOException {
        List<String> response = new ArrayList<String>();
        String requestParam = buildParamRequestString(params);

        URL url = new URL(requestUrl);
        HttpsURLConnection urlConn = (HttpsURLConnection) url.openConnection();
        urlConn.setUseCaches(false);

        // the request will return a response
        urlConn.setDoInput(true);

        if ("POST".equals(method)) {
            // set request method to POST
            urlConn.setDoOutput(true);
        } else {
            // set request method to GET
            urlConn.setDoOutput(false);
        }

        if ("POST".equals(method) && params != null && params.size() > 0) {
            OutputStreamWriter writer = new OutputStreamWriter(urlConn.getOutputStream());
            writer.write(requestParam);
            writer.flush();
        }

        // reads response, store line by line in an array of Strings
        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));

        String line = "";
        while ((line = reader.readLine()) != null) {
            response.add(line);
        }

        reader.close();
        return response.toArray(new String[0]);
    }

    /*
     * public static void main(String[] args) { String requestUrl =
     * "http://dev.mapi2.me.zing.vn/frs/mapi2/friend"; String method = "GET";
     * HashMap<String, String> params = new HashMap<String, String>();
     * params.put("method", "abc"); try { String[] response =
     * sendHttpRequest(requestUrl, method, params);
     * System.out.println(response.length); if (response != null &&
     * response.length > 0) { System.out.println("RESPONSE FROM: " +
     * requestUrl); for (String line : response) { System.out.println(line);
     * System.out.println("###############"); } }
     *
     * } catch (IOException ex) { System.out.println("ERROR: " +
     * ex.getMessage()); } }
     */


    public static void getHTML(String urlToRead) throws IOException {
        URL url = new URL(urlToRead);
        URLConnection conn = url.openConnection();
        conn.setDoOutput(true);
        OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
        writer.flush();
    }

    public static String get(String requestUrl) {
        String data = "";
        String[] arrResponse = null;
        try {
            arrResponse = sendHttpRequest(requestUrl, "GET", new HashMap<>());
            if (arrResponse != null && arrResponse.length > 0) {
                for(String s : arrResponse){
                    data += s;
                }
                return data;
            }
            return "";
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}
