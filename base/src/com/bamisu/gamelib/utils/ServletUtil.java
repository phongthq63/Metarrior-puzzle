package com.bamisu.gamelib.utils;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class ServletUtil {
    public static int getIntParameter(HttpServletRequest request,
                                      String paramName) {
        return getIntParameter(request, paramName, 0);
    }

    public static int getIntParameter(HttpServletRequest request,
                                      String paramName, int defaultValue) {
        String paramString = getStringParameter(request, paramName);
        if (paramString == null) {
            return defaultValue;
        }
        int paramValue;
        try {
            paramValue = Integer.parseInt(paramString);
        } catch (NumberFormatException nfe) { // Handles null and bad format
            paramValue = defaultValue;
        }
        return (paramValue);
    }

    public static Long getLongParameter(HttpServletRequest request,
                                        String paramName) {
        return getLongParameter(request, paramName, 0L);
    }

    public static Long getLongParameter(HttpServletRequest request,
                                        String paramName, Long defaultValue) {
        String paramString = getStringParameter(request, paramName);
        if (paramString == null) {
            return defaultValue;
        }
        Long paramValue = null;
        try {
            paramValue = Long.parseLong(paramString);
        } catch (NumberFormatException nfe) { // Handles null and bad format
            paramValue = defaultValue;
        }
        return (paramValue);
    }

    public static Double getDoblieParameter(HttpServletRequest request,
                                            String paramName) {
        return getDoubleParameter(request, paramName, 0D);
    }

    public static Double getDoubleParameter(HttpServletRequest request,
                                            String paramName, Double defaultValue) {
        String paramString = getStringParameter(request, paramName);
        if (paramString == null) {
            return defaultValue;
        }
        Double paramValue = null;
        try {
            paramValue = Double.parseDouble(paramString);
        } catch (NumberFormatException nfe) { // Handles null and bad format
            paramValue = defaultValue;
        }
        return (paramValue);
    }

    public static List<Integer> getListIntParameter(HttpServletRequest request,
                                                    String paramName) {
        String separate = ",";
        return getListIntParameter(request, paramName, separate);
    }

    public static List<Integer> getListIntParameter(HttpServletRequest request,
                                                    String paramName, String separate) {
        String paramString = getStringParameter(request, paramName);
        if (paramString == null) {
            return null;
        }
        if (paramString == null || paramString.isEmpty()) {
            return null;
        }
        String[] str_list = paramString.split(separate);
        if (str_list.length == 0) {
            return null;
        }
        List<Integer> list = new ArrayList<Integer>();
        for (String item : str_list) {
            try {
                int int_item = Integer.parseInt(item);
                list.add(int_item);
            } catch (Exception e) {
            }
        }
        if (list.size() == 0) {
            return null;
        }
        return list;
    }

    public static String getStringParameter(HttpServletRequest request,
                                            String paramName) {
        return getStringParameter(request, paramName, "");
    }

    public static String getStringParameter(HttpServletRequest request,
                                            String paramName, String defaultValue) {
        String paramString = getParameter(request, paramName);
        if (paramString == null) {
            return defaultValue;
        }
        return paramString;

    }

    public static List<String> getListStringParameter(
            HttpServletRequest request, String paramName, String separate) {
        String paramString = getStringParameter(request, paramName);
        if (paramString == null) {
            return null;
        }
        if (paramString == null || paramString.isEmpty()) {
            return null;
        }
        String[] str_list = paramString.split(separate);
        if (str_list.length == 0) {
            return null;
        }
        List<String> list = new ArrayList<String>();
        for (String txt : str_list) {
            list.add(txt);
        }

        return list;
    }

    public static String getParameter(HttpServletRequest request,
                                      String paramName) {
        String value = request.getParameter(paramName);
        if (value == null) {
            Enumeration<String> requestParam = request.getParameterNames();
            while (requestParam.hasMoreElements()) {
                String rep = requestParam.nextElement();
                if (rep.equalsIgnoreCase(paramName)) {
                    return request.getParameter(rep);
                }
            }
        }
        return value;
    }

    public static List<Long> getListLongParameter(HttpServletRequest request,
                                                  String paramName, String separate) {
        String paramString = getStringParameter(request, paramName);
        if (paramString == null) {
            return null;
        }
        if (paramString == null || paramString.isEmpty()) {
            return null;
        }
        String[] str_list = paramString.split(separate);
        if (str_list.length == 0) {
            return null;
        }
        List<Long> list = new ArrayList<Long>();
        for (String item : str_list) {
            try {
                long int_item = Long.parseLong(item);
                list.add(int_item);
            } catch (Exception e) {
            }
        }
        if (list.size() == 0) {
            return null;
        }
        return list;
    }

    public static String getBody(HttpServletRequest request) {
        StringBuilder builder = new StringBuilder();
        try {
            BufferedReader br = request.getReader();
            String line;
            while((line = br.readLine()) != null){
                builder.append(line);
            }
            return builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
