package com.bamisu.gamelib.utils;

import com.bamisu.gamelib.entities.ServerConstant;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.bamisu.gamelib.entities.LIZRandom;
import com.bamisu.gamelib.entities.RandomObj;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.time.DateUtils;
import org.jboss.netty.util.CharsetUtil;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Utils {
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATE_FORMAT2 = "yyyyMMdd";
    private static JsonFactory jsonFactory = new JsonFactory();
    private static ObjectMapper jsonMapper = new ObjectMapper(jsonFactory).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private static ScriptEngineManager mgr = new ScriptEngineManager();
    private static ScriptEngine engine = mgr.getEngineByName("JavaScript");

    public static String toJson(Object obj) {
        try {
            return jsonMapper.writeValueAsString(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T fromJson(String json, Class<T> type) {
        try {
            if (json == null || json.isEmpty())
                return null;
            else
                return jsonMapper.readValue(json, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T fromJson(String json, TypeReference<T> typeRef) {
        try {
            if (json == null)
                return null;
            else
                return jsonMapper.readValue(json, typeRef);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T fromJson(File file, TypeReference<T> typeRef) {
        try {
            return jsonMapper.readValue(file, typeRef);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> List<T> fromJsonList(String json, Class<T> type) {
        try {
            if (json == null || json.isEmpty())
                return null;
            else
                return jsonMapper.readValue(json, jsonMapper.getTypeFactory().constructCollectionType(ArrayList.class, type));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T convert(Map<String, Object> map, Class<T> type) {
        return jsonMapper.convertValue(map, type);
    }

    public static <T> T convertByObject(Object object, Class<T> type) {
        return jsonMapper.convertValue(object, type);
    }

    public static long getY(double a, double b, double c, int x) {
        return (long) Math.ceil(a * x * x + b * x + c);
    }

    public static double futureValueFormula(double base, double a, double x) {
        if (base == 0) {
            return 0;
        } else {
            return x * (2 * Math.log(x) - 2 + base) * a;
        }

    }

    public static int randRange(int Min, int Max) {
        int value = Min + (int) (Math.random() * ((Max - Min) + 1));
        return value;
    }

    public static String md5(String msg) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        byte[] byteOfMsg = msg.getBytes(CharsetUtil.UTF_8);
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] retByte = md.digest(byteOfMsg);
        String result = new String(Hex.encodeHex(retByte));
        return result;
    }

    public static int randomInRange(int min, int max) {
        return min + (int) (Math.random() * (max - min + 1));
    }

    public static long randomInRange(long min, long max) {
        return min + (long) (Math.random() * (max - min + 1));
    }

    public static double randomInRange(double min, double max) {
        return min + (Math.random() * (max - min));
    }

    private static Random random;

    public static void shuffle(int[] array) {
        if (random == null) random = ThreadLocalRandom.current();
        int count = array.length;
        for (int i = count; i > 1; i--) {
            swapInt(array, i - 1, random.nextInt(i));
        }
    }

    public static void shuffle(long[] array) {
        if (random == null) random = ThreadLocalRandom.current();
        int count = array.length;
        for (int i = count; i > 1; i--) {
            swapLong(array, i - 1, random.nextInt(i));
        }
    }

    public static void swapInt(int[] array, int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    public static void swapLong(long[] array, int i, int j) {
        long temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }


    public static int randByRate(float rates[], int maxRate) {
        int ret = -1;
        int leng = rates.length;
        if (leng == 0) {
            return ret;
        }
        double[] tmp = new double[leng];

        int i = 0;
        while (i < leng) {
            double b = (i == 0) ? 0 : tmp[i - 1];
            tmp[i] = rates[i] / 100 * maxRate + b;
            i++;
        }
        long rand = randomInRange(0, (int) tmp[leng - 1]);
        i = 0;
        while (i < leng) {
            if (rand < tmp[i]) {
                return i;
            }
            i++;
        }
        return ret;
    }

    /**
     * random theo ty le %
     *
     * @param rates --> mảng tỷ lệ cần tính -- tổng các phần tử trong mảng cần <=100
     * @return trả vê vị trí của tỷ lệ trong mảng truyền vào, nếu mảng truyền vào rỗng  --> trả về -1
     * nếu tổng các phần tử trong mảng = a != 100 --> trả về -1 khi rơi vào phần (100 - a)
     */
    public static int randByPercent(int rates[]) {

        if (rates.length == 0)
            return -1;

        int[] tmp = new int[rates.length];
        tmp[0] = rates[0];
        for (int i = 1; i < rates.length; i++) {
            tmp[i] = tmp[i - 1] + rates[i];
        }

        /*if (tmp[tmp.length - 1] > 100)
        {
            return -1;
        }*/

        int rand = randRange(0, 100);

        for (int i = 0; i < tmp.length; i++) {
            if (rand <= tmp[i])
                return i;
        }

        return -1;
    }

    /**
     * Trả về vị trí ngẫu nhiên trong mảng pos mà phần tử tại vị trí đó bằng 0.
     *
     * @param pos
     * @return
     */
    public static int randNotIn(int[] pos) {
        ArrayList<Integer> posZero = new ArrayList<Integer>();
        for (int i = 0; i < pos.length; i++) {
            if (pos[i] == 0)
                posZero.add(i);
        }
        if (posZero.size() == 0)
            return -1;
        else {
            int p = randRange(0, posZero.size() - 1);
            return posZero.get(p);
        }
    }

    public static double round(double number, int digit) {
        if (digit > 0) {
            int temp = 1, i;
            for (i = 0; i < digit; i++)
                temp = temp * 10;
            number = number * temp;
            number = Math.round(number);
            number = number / temp;
            return number;
        } else
            return 0.0;
    }

    public static boolean validNextTime(String endHour) throws ParseException {
        boolean result = false;
        SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm:s");
        Date end = hourFormat.parse(endHour);

        Date now = new Date(System.currentTimeMillis());
        String nowHourStr = hourFormat.format(now.getTime());

        try {
            Date nowHour = hourFormat.parse(nowHourStr);
            if (nowHour.before(end)) {
                result = true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }

    public static String timeNowString() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT));
    }

    public static String timeNowString(String format) {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(format));
    }

    public static String dateNowString() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT));
    }

    public static String dateNowToLong(){
       return  LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT2));
    }

    public static String dateToString(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_TIME_FORMAT);
        return simpleDateFormat.format(date);
    }
    public static String dateStringToLong(String date){
        LocalDateTime dateTime2 = LocalDateTime.parse(date,DateTimeFormatter.ofPattern(DATE_FORMAT2));
        return  dateTime2.format(DateTimeFormatter.ofPattern(DATE_FORMAT2));
    }
    public static int hourToS(int sessionLiveTime) {
        return sessionLiveTime * 3600;
    }

//    /**
//     * tính số giây từ thời điểm hiện tại đến hết ngày
//     *
//     * @return
//     */
//    public static long getDeltaSecondsToEndDayServer() {
//        //Lay trong config thoi gian reset ngay
//        String[] a = ConfigHandle.instance().get("d_time_reset").split(" ", 2);
//        String time = a[0];     //Phan thoi gian reset
//        String zone = a[1];     //Mui gio reset
//        ZonedDateTime now = ZonedDateTime.now();    //Time tren local
//        //Time tren mui gio reset config
//        ZonedDateTime nowInZone = now.withZoneSameInstant(ZoneId.of(zone, ZoneId.SHORT_IDS));
//
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIME_FORMAT);
//        LocalTime timeReset = LocalTime.parse(time, formatter);
//        ZonedDateTime startNextDay = nowInZone.with(LocalTime.of(timeReset.getHour(), timeReset.getMinute(), timeReset.getSecond()));
//
////        System.out.println(now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssZ")));
////        System.out.println(nowInZone.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssZ")));
////        System.out.println(startNextDay.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssZ")));
////        System.out.println(nowInZone.until(startNextDay, ChronoUnit.SECONDS));
//
//        long timeNextDay = nowInZone.until(startNextDay, ChronoUnit.SECONDS);
//        return (timeNextDay > 0) ? timeNextDay : nowInZone.until(startNextDay.plusDays(1), ChronoUnit.SECONDS);
//    }

//    public static long getDeltaSecondsToEndWeekServer() {
//        //Lay trong config thoi gian reset ngay
//        String[] a = ConfigHandle.instance().get("d_time_reset").split(" ", 2);
//        String time = a[0];     //Phan thoi gian reset
//        String zone = a[1];     //Mui gio reset
//        ZonedDateTime now = ZonedDateTime.now();    //Time tren local
//        //Time tren mui gio reset config
//        ZonedDateTime nowInZone = now.withZoneSameInstant(ZoneId.of(zone, ZoneId.SHORT_IDS));
//
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIME_FORMAT);
//        LocalTime timeReset = LocalTime.parse(time, formatter);
//        ZonedDateTime startNextWeek = nowInZone.with(LocalTime.of(timeReset.getHour(), timeReset.getMinute(), timeReset.getSecond()));
//        if (nowInZone.until(startNextWeek, ChronoUnit.SECONDS) < 0) {
//            startNextWeek.plusDays(1);
//        }
//        startNextWeek = startNextWeek.plusWeeks(1);
//        startNextWeek = startNextWeek.plusDays(-startNextWeek.getDayOfWeek().getValue());
//
////        System.out.println(now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssZ")));
////        System.out.println(nowInZone.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssZ")));
////        System.out.println(startNextWeek.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssZ")));
////        System.out.println(nowInZone.until(startNextWeek, ChronoUnit.SECONDS));
//
//        return nowInZone.until(startNextWeek, ChronoUnit.SECONDS);
//    }

    /**
     * tính số giây từ thời điểm hiện tại đến hết ngày
     *
     * @return
     */
    public static long getDeltaSecondsToEndDay() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startNextDay = now.plusDays(1);
        startNextDay = startNextDay.plusDays(-1);
        startNextDay = startNextDay.withHour(23);
        startNextDay = startNextDay.withMinute(59);
        startNextDay = startNextDay.withSecond(59);

//        System.out.println(now.format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)));
//        System.out.println(startNextMonth.format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)));
//        System.out.println(now.until(startNextMonth, ChronoUnit.SECONDS));

        return now.until(startNextDay, ChronoUnit.SECONDS);
    }

    /**
     * tính số giây từ thời điểm hiện tại đến hết tuan
     *
     * @return
     */
    public static long getDeltaSecondsToEndWeek() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startNextWeek = now.plusDays(7 - (now.getDayOfWeek().getValue() - 1));
        startNextWeek = startNextWeek.plusDays(-1);
        startNextWeek = startNextWeek.withHour(23);
        startNextWeek = startNextWeek.withMinute(59);
        startNextWeek = startNextWeek.withSecond(59);

//        System.out.println(now.format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)));
//        System.out.println(startNextWeek.format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)));
//        System.out.println(now.until(startNextWeek, ChronoUnit.SECONDS));

        return now.until(startNextWeek, ChronoUnit.SECONDS);
    }

    /**
     * tính số giây từ thời điểm hiện tại đến delta tuan
     *
     * @return
     */
    public static long getDeltaSecondsToEndWeek(int deltaWeek) {
        if(deltaWeek < 0) return 0;

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startNextWeek = now.plusDays(7 - (now.getDayOfWeek().getValue() - 1) + (deltaWeek - 1) * 7);
        startNextWeek = startNextWeek.plusDays(-1);
        startNextWeek = startNextWeek.withHour(23);
        startNextWeek = startNextWeek.withMinute(59);
        startNextWeek = startNextWeek.withSecond(59);

//        System.out.println(now.format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)));
//        System.out.println(startNextWeek.format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)));
//        System.out.println(now.until(startNextWeek, ChronoUnit.SECONDS));

        return now.until(startNextWeek, ChronoUnit.SECONDS);
    }

    /**
     * tính số giây từ thời điểm hiện tại đến hết tháng
     *
     * @return
     */
    public static long getDeltaSecondsToEndMonth() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startNextMonth = now.plusMonths(1);
        startNextMonth = startNextMonth.withDayOfMonth(1);
        startNextMonth = startNextMonth.plusDays(-1);
        startNextMonth = startNextMonth.withHour(23);
        startNextMonth = startNextMonth.withMinute(59);
        startNextMonth = startNextMonth.withSecond(59);

//        System.out.println(now.format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)));
//        System.out.println(startNextMonth.format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)));
//        System.out.println(now.until(startNextMonth, ChronoUnit.SECONDS));

        return now.until(startNextMonth, ChronoUnit.SECONDS);
    }

    public static String genGiftCode() {
        return ranStr(12, 13, true);
    }

    public static String genInviteCode() {
        return ranStr(3, 2, false) + ranStr(5, 3, false);
    }

    public static boolean isDefaultDisplayName(String name) {
        return name.isEmpty();
    }

    public static String genToken() {
        return Utils.ranStr(5) + "-" + Utils.ranStr(5) + System.currentTimeMillis() + Utils.ranStr(5);
    }

    public static String genIngameActorID() {
        return UUID.randomUUID().toString();
    }

    public static double byteToKByte(int length) {
        return length * 1.0 / 1024;
    }

    /**
     * lấy delta day giữa 2 thời điểm
     * @param day1  seconds
     * @param day2  seconds
     * @return
     */
    public static long deltaDay(int day1, int day2) {
        LocalDateTime date1 = LocalDateTime.ofInstant(Instant.ofEpochSecond(day1), ZoneId.systemDefault()).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime date2 = LocalDateTime.ofInstant(Instant.ofEpochSecond(day2), ZoneId.systemDefault()).withHour(0).withMinute(0).withSecond(0);
        long deltaDay = ChronoUnit.DAYS.between(date1, date2);
        return deltaDay;
    }

    public static String genDarkRealmEventHash() {
        return UUID.randomUUID().toString();
    }

    public static String genEndlessNightEventHash() {
        return UUID.randomUUID().toString();
    }

    public static String genMatchID() {
        return "match" + UUID.randomUUID().toString();
    }


    /**
     * Một lớp tiện ích, sử dụng để hỗ trợ các thao tác tìm kiếm, sắp xếp.
     * Cách dùng: Từ một mảng (hoặc danh sách) các đối tượng gốc, ta chuyển
     * sang dạng một mảng (hoặc danh sách) các đối tượng pos_val, lưu trữ thông
     * tin và giá trị của các đối tượng. Sau đó thực hiện một số thao tác trên
     * danh sách pos_val này, chẳng hạn: tìm kiếm, sắp xếp, đưa ra danh sách n
     * phần tử lớn nhất, nhỏ nhất, ...
     *
     * @author tuantri
     */
    public static class Pos_Val {
        public String key;
        public int value;

        public Pos_Val(String position, int number) {
            this.key = position;
            this.value = number;
        }
    }

    /**
     * Đưa ra danh sách n phần tử lớn nhất
     *
     * @param list
     * @param n
     * @return
     */
    public static LinkedList<Pos_Val> getTopMaxPosNum(LinkedList<Pos_Val> list, int n) {
        if (list.size() < n) {
//            return null;
            n = list.size();
        }
        LinkedList<Pos_Val> result = new LinkedList<Pos_Val>();

        for (int i = 0; i < n; i++) {
            Pos_Val p = getMaxPosNum(list);
            list.remove(p);
            result.add(p);
        }
        return result;
    }

    /**
     * Trả về phần tử có giá trị lớn nhất
     *
     * @param list
     * @return
     */
    public static Pos_Val getMaxPosNum(LinkedList<Pos_Val> list) {
        if (list == null) return null;
        if (list.size() == 0) return null;
        Pos_Val max = list.get(0);

        for (Pos_Val pos_Num : list) {
            if (max.value < pos_Num.value) {
                max = pos_Num;
            }
        }

        return max;
    }

    /**
     * Đưa ra danh sách n phần tử nhỏ nhất
     *
     * @param list
     * @param n
     * @return
     */
    public static LinkedList<Pos_Val> getTopMinPosNum(LinkedList<Pos_Val> list, int n) {
        if (list.size() < n)
            return null;
        LinkedList<Pos_Val> result = new LinkedList<Pos_Val>();

        for (int i = 0; i < n; i++) {
            Pos_Val p = getMinPosNum(list);
            list.remove(p);
            result.add(p);
        }
        return result;
    }

    /**
     * Trả về phần tử có giá trị nhỏ nhất
     *
     * @param list
     * @return
     */
    public static Pos_Val getMinPosNum(LinkedList<Pos_Val> list) {
        if (list == null) return null;
        if (list.size() == 0) return null;
        Pos_Val min = list.get(0);

        for (Pos_Val pos_Num : list) {
            if (min.value > pos_Num.value) {
                min = pos_Num;
            }
        }

        return min;
    }


    /**
     * Lấy timestamp kiểu INT, tính bằng giây
     */
    public static int getTimestampInSecond() {
        return Long.valueOf(System.currentTimeMillis() / 1000).intValue();
    }


    /**
     * trả về 1 list random các phần tử trong list đã cho
     */
    public static <T> List<T> randomSubList(List<T> l, int size) {
        List<T> result;

        if (l != null) {
            if (l.size() <= size) {
                result = new ArrayList<>(l);
                int seed = getTimestampInSecond();
                Collections.shuffle(result, new Random(seed));
                return result;
            }

            List<T> tempList1 = new LinkedList<>(l);
            List<T> tempList2 = new LinkedList<>();

            int num = size < (l.size() / 2) ? size : l.size() - size;

            int pos;
            T obj;
            for (int i = 0; i < num; i++) {
                pos = randRange(0, tempList1.size() - 1);
                obj = tempList1.get(pos);
                tempList1.remove(pos);
                tempList2.add(obj);
            }

            if (num < size) {
                result = new ArrayList<>(tempList1);
                int seed = getTimestampInSecond();
                Collections.shuffle(result, new Random(seed));
            } else {
                result = new ArrayList<>(tempList2);
            }
        } else {
            result = new ArrayList<>();
        }

        return result;
    }

    //chuỗi random
    public static String ranStr(int length, int option) {
        String str1 = "qwertyuiopasdfghjklzxcvbnm";
        String str2 = "QWERTYUIOPASDFGHJKLZXCVBNM";
        String str3 = "12345678901234567890";
        char[] chars = "".toCharArray();
        switch (option) {
            case 1:
                chars = (str1).toCharArray();
                break;
            case 2:
                chars = (str2).toCharArray();
                break;
            case 3:
                chars = (str3).toCharArray();
                break;
            case 12:
                chars = (str1 + str2).toCharArray();
                break;
            case 13:
                chars = (str1 + str3).toCharArray();
                break;
            case 23:
                chars = (str2 + str3).toCharArray();
                break;
            case 123:
                chars = (str1 + str2 + str3).toCharArray();
                break;
        }

        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        return sb.toString();
    }

    //chuỗi random
    public static String ranStr(int length, int option, boolean noZero) {
        String str1 = "qwertyuiopasdfghjklzxcvbnm";
        String str2 = "QWERTYUIPASDFGHJKLZXCVBNM";
        String str3 = "1234567890123456789";
        char[] chars = "".toCharArray();
        switch (option) {
            case 1:
                chars = (str1).toCharArray();
                break;
            case 2:
                chars = (str2).toCharArray();
                break;
            case 3:
                chars = (str3).toCharArray();
                break;
            case 12:
                chars = (str1 + str2).toCharArray();
                break;
            case 13:
                chars = (str1 + str3).toCharArray();
                break;
            case 23:
                chars = (str2 + str3).toCharArray();
                break;
            case 123:
                chars = (str1 + str2 + str3).toCharArray();
                break;
        }

        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        return sb.toString();
    }

    public static String ranStr(int length) {
        String str1 = "qwertyuiopasdfghjklzxcvbnm";
        String str2 = "QWERTYUIOPASDFGHJKLZXCVBNM";
        String str3 = "1234567890";
        char[] chars = (str1 + str2 + str3).toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        return sb.toString();
    }

    public static String moneyFormat(long money) {
        if (money >= 999) {
            StringBuilder sb = new StringBuilder("");
            String mMoney = String.valueOf(money);
            for (int i = 0; i < mMoney.length(); i++) {
                int j = mMoney.length() - i - 1;
                sb.insert(0, mMoney.charAt(j));
                if (i % 3 == 2 && i != mMoney.length() - 1) {
                    sb.insert(0, ".");
                }
            }
            return sb.toString();
        } else {
            return String.valueOf(money);
        }
    }

    public static String loadConfig(String fileName) {
        String result = "";
        FileInputStream inputStream = null;
        try {
             inputStream = new FileInputStream(System.getProperty("user.dir") + "/conf/" + fileName);
            result = IOUtils.toString(inputStream);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static String loadFile(String fileName) {
        String result = "";
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(fileName);
            result = IOUtils.toString(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static boolean saveConfig(String content, String fileName) {
        //System.out.println(System.getProperty("user.dir") + "/conf/" + fileName);
        return writeToFile(content, System.getProperty("user.dir") + "/conf/" + fileName);
    }

    public static boolean saveToFile(String content, String fileName) {
        return writeToFile(content, fileName);
    }

    public static boolean rate(int rate) {
        if (rate <= 0) return false;
        if (rate >= 100) return true;
        Random random = new Random();
        random.nextInt(100);
        if (random.nextInt(100) < rate) return true;
        return false;
    }

    public static boolean rate(double rate) {
        double _rate = round(rate, 5);
        LIZRandom lizRandom = new LIZRandom();
        lizRandom.push(new RandomObj(true, _rate));
        lizRandom.push(new RandomObj(false, 100d - _rate));
        return (boolean) lizRandom.next().value;
    }

    public static boolean rate(int i1, int i2) {
        Random random = new Random();
        return random.nextInt(i2) <= i1 - 1;
    }

    public static String listToString(List param) {
        StringBuilder str = new StringBuilder();
        for (Object id : param) {
            str.append(id).append(",");
        }
        if (str.length() > 0) str.deleteCharAt(str.length() - 1);

        return str.toString();
    }

    public static String uppercaseFirst(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static boolean isID(String key) {
        try {
            long i = Long.parseLong(key);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String viewTimeFromSecond(long timestamp) {
        Date date = new Date(timestamp * 1000);
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy-HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone(ServerConstant.TIME_ZONE));
        return formatter.format(date);
    }

    public static String viewTimeFromSecond(long timestamp, String patten, String zone) {
        Date date = new Date(timestamp * 1000);
        DateFormat formatter = new SimpleDateFormat(patten);
        formatter.setTimeZone(TimeZone.getTimeZone(ServerConstant.TIME_ZONE));
        return formatter.format(date);
    }

    public static int getTimeSecondFromString(String pattern, String time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        Date parsedDate = null;
        try {
            parsedDate = dateFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return (int) (parsedDate.getTime() / 1000);
    }

    public static String fixUTFString(String str) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (!Character.isHighSurrogate(ch) && !Character.isLowSurrogate(ch)) {
                sb.append(ch);
            }
        }
        return sb.toString();
    }

    public static boolean isSameDay(int timestamp1, int timestamp2) {
        return DateUtils.isSameDay(new Date(timestamp1 * 1000), new Date(timestamp2 * 1000));
    }

    public static boolean isNewDay(int timestamp) {
        if (DateUtils.isSameDay(new Date(System.currentTimeMillis()), new Date(Long.valueOf(timestamp) * 1000)))
            return false;
        return true;
    }

    public static boolean isNewWeek(int timestamp) {
        // Calendar java : start week is Sunday -> p tru ngay di 1 ngay khi tinh
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(new Date(System.currentTimeMillis() - 86400000));
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(new Date(Long.valueOf(timestamp) * 1000 - 86400000));

        return !(cal1.get(0) == cal2.get(0) && cal1.get(1) == cal2.get(1) && cal1.get(3) == cal2.get(3));
    }

    public static boolean isNewMonth(int timestamp) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(new Date(System.currentTimeMillis()));
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(new Date(Long.valueOf(timestamp) * 1000));

        return !(cal1.get(0) == cal2.get(0) && cal1.get(1) == cal2.get(1) && cal1.get(2) == cal2.get(2));
    }

    public static boolean writeToFile(String content, String fileName) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(fileName));
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static int getDayOfWeek() {
        return LocalDateTime.now(ServerConstant.TIME_ZONE_ID).getDayOfWeek().getValue() + 1;
    }

    /**
     * t1 -> t2
     *
     * @param d1
     * @param t1 seconds of day
     * @param d2
     * @param t2 seconds of day
     * @return
     */
    public static int getDeltaSecondsOfDay(int d1, int t1, int d2, int t2) {
        int secondsDay = 24 * 60 * 60;
        int s1 = d1 * secondsDay + t1;
        int s2 = d2 * secondsDay + t2;
        if (s2 >= s1) {
            return s2 - s1;
        } else {
            return s1 - s2;
        }
    }

    /**
     * từ t1 -> t2
     *
     * @param d1
     * @param t1 seconds of day
     * @param d2
     * @param t2 seconds of day
     * @return
     */
    public static int getDeltaSecondsOfWeek(int d1, int t1, int d2, int t2) {
        int secondsDay = 24 * 60 * 60;
        int s1 = (d1 - 2) * secondsDay + t1;
        int s2 = (d2 - 2) * secondsDay + t2;
        if (s2 >= s1) {
            return s2 - s1;
        } else {
            return (7 * secondsDay) - (s1 - s2);
        }
    }

    public static int getDeltaSecondsOfWeek(int d1, String strt1, int d2, String strt2) {
        int t1 = LocalTime.parse(strt1, DateTimeFormatter.ofPattern("HH:mm:ss")).toSecondOfDay();
        int t2 = LocalTime.parse(strt2, DateTimeFormatter.ofPattern("HH:mm:ss")).toSecondOfDay();
        int secondsDay = 24 * 60 * 60;
        int s1 = (d1 - 2) * secondsDay + t1;
        int s2 = (d2 - 2) * secondsDay + t2;
        if (s2 >= s1) {
            return s2 - s1;
        } else {
            return (7 * secondsDay) - (s1 - s2);
        }
    }

    public static void main(String[] args) {
        System.out.println(deltaDay(Utils.getTimestampInSecond(), Utils.getTimestampInSecond() + 60 * 60 * 13));
    }

    public static String genHeroHash() {
        return "hero" + System.currentTimeMillis() + "_" + Utils.ranStr(5) + "-" + Utils.ranStr(5);
    }

    public static String genItemHash() {
        return "item" + System.currentTimeMillis() + "_" + Utils.ranStr(5) + "-" + Utils.ranStr(5);
    }

    public static String genStoneHash() {
        return "stone" + System.currentTimeMillis() + "_" + Utils.ranStr(5) + "-" + Utils.ranStr(5);
    }

    public static String genMissiongHash() {
        return "mission" + System.currentTimeMillis() + "_" + Utils.ranStr(5) + "-" + Utils.ranStr(5);
    }

    public static String genMailHash() {
        return "mail" + System.currentTimeMillis() + "_" + Utils.ranStr(5) + "-" + Utils.ranStr(5);
    }

    public static String genGiftGuildHash() {
        return "gift_guild" + System.currentTimeMillis() + "_" + Utils.ranStr(5) + "-" + Utils.ranStr(5);
    }

    public static Object calculationFormula(String formula) {
        formula = formula.replace("%", "/100");
        try {
            return engine.eval(formula);
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * làm tròn sau dấu phảy
     */
    public static double roundDouble(double d, int var1) {
        double a = 1.0;
        for (int i = 0; i < var1; i++) {
            a = a * 10;
        }
        return Math.round(d * a) / a;
    }

    public static <T extends Exception> String exceptionToString(T e){
        String s = "error: " + e.getLocalizedMessage() + " \n";
        s+= e.toString() + "\n";
        for(StackTraceElement stackTraceElement : e.getStackTrace()){
            s += "\t at " + stackTraceElement + "\n";
        }
        return s;
    }
    public static long randomUid(){
        long val = -1;
        do {
            val = UUID.randomUUID().getMostSignificantBits();
        } while (val < 0);
        return val;
    }
}
