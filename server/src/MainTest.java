
//import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
//import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
//import com.google.api.client.http.javanet.NetHttpTransport;
//import com.google.api.client.json.jackson2.JacksonFactory;

import com.bamisu.gamelib.base.datacontroller.couchbase.CouchbaseDataController;
import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.encryption.Encrypter;
import com.bamisu.gamelib.entities.LIZRandom;
import com.bamisu.gamelib.entities.RandomObj;
import com.bamisu.gamelib.utils.HttpRequestUtils;
import com.bamisu.log.gameserver.datamodel.campaign.UserCampaignDetailModel;
import com.bamisu.log.gameserver.datamodel.tower.UserTowerModel;
import com.bamisu.log.gameserver.module.campaign.entities.TeamUtils;
import com.bamisu.log.gameserver.module.characters.kingdom.Kingdom;
import com.bamisu.log.gameserver.module.skill.Skill;
import com.bamisu.gamelib.skill.config.entities.BaseSkillInfo;
import com.restfb.*;

import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;
import javax.mail.*;

import javax.mail.internet.*;

import com.restfb.types.User;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

public class MainTest {

    public static List<User> findFacebookFriendsUsingRest(String facebookAccessToken) {
        List<User> myFacebookFriendList = new ArrayList();
        final FacebookClient facebookClient;
        facebookClient = new DefaultFacebookClient(facebookAccessToken, Version.LATEST);
        User user = facebookClient.fetchObject("me", User.class);
        String userName = user.getFirstName();
        if (userName == null) {
            userName = user.getLastName();
        }
        String userEmail = user.getEmail();
        Connection<User> myFriends = facebookClient.fetchConnection("me/friends", User.class);
        //System.out.println("Count of my friends: " + myFriends.getData().size());
        for (User friend : myFriends.getData()) {
            //System.out.println("Friends star and name: " + friend.getId() + " , " + friend.getName());
            myFacebookFriendList.add(friend);
        }
        //System.out.println("All Friends : " + myFacebookFriendList);

        return myFacebookFriendList;
    }

    public static void sendEmail(Session session, String toEmail, String subject, String body) {
        try {
            MimeMessage msg = new MimeMessage(session);
            //set message headers
            msg.addHeader("Content-type", "content/HTML; charset=UTF-8");
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");

            msg.setFrom(new InternetAddress("popeyesvn02@gmail.com", "NoReply-JD"));

            msg.setReplyTo(InternetAddress.parse("khanhherovp@gmail.com", false));

            msg.setSubject(subject, "UTF-8");

            msg.setText(body, "UTF-8");

            msg.setSentDate(new Date());

            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
            //System.out.println("Message is ready");
            Transport.send(msg);

            //System.out.println("EMail Sent Successfully!!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class TestObj {
        public String name;
        public int value;
        public TestObj testObj = null;

        public TestObj(String name, int value) {
            this.name = name;
            this.value = value;
        }

        public boolean is() {
            double a = 1000000 / 123123;
            a = 1000000 / 123123;
            a = 1000000 / 123123;
            a = 1000000 / 123123;
            a = 1000000 / 123123;
            a = 1000000 / 123123;
            a = 1000000 / 123123;
            a = 1000000 / 123123;
            a = 1000000 / 123123;
            a = 1000000 / 123123;
            return value % 2 == 0;
        }
    }

    public static void testFunc(TestObj testObj) {
        TestObj testObj1 = new TestObj("", 1);
        testObj1.testObj = testObj;
    }

    static enum Pie {
        RONG(0, 0),
        X2(12, 2),
        X3(13, 3),
        X4(14, 4),
        MINUS(2, -1);

        public int id;
        public int value;

        Pie(int i, int value) {
            this.id = i;
            this.value = value;
        }

        public int getId() {
            return id;
        }

        public int getValue() {
            return value;
        }

        public static Pie fromID(int id) {
            for (Pie pie : Pie.values()) {
                if (pie.id == id) return pie;
            }

            return null;
        }
    }

    static class Block implements Serializable {
        public List<Integer> pieDatas = new ArrayList<>();
    }

    static class Level implements Serializable {
        public int id;
        public String name;
        public int enemies;
        public float size = 5f;
        public float gap = 10f;
        public List<Block> blocks = new ArrayList<>();

        public Level(int id) {
            String s = "0000";
            this.id = id;
            String idStr = String.valueOf(id);
            this.name = "level_" + s.substring(0, 4 - idStr.length()) + idStr;
        }
    }

    public static void main(String[] args) {
//        LIZRandom lizRandom1 = new LIZRandom();
//        lizRandom1.push(new RandomObj(Pie.RONG.getId(),40));
//        lizRandom1.push(new RandomObj(Pie.X2.getId(),15));
//        lizRandom1.push(new RandomObj(Pie.X3.getId(),8));
//        lizRandom1.push(new RandomObj(Pie.X4.getId(),3));
//        lizRandom1.push(new RandomObj(Pie.MINUS.getId(),20));
//
//        LIZRandom lizRandom2 = new LIZRandom();
//        lizRandom2.push(new RandomObj(Pie.RONG.getId(),40));
//        lizRandom2.push(new RandomObj(Pie.X2.getId(),15));
//        lizRandom2.push(new RandomObj(Pie.X3.getId(),8));
//        lizRandom2.push(new RandomObj(Pie.X4.getId(),3));
//        lizRandom2.push(new RandomObj(Pie.MINUS.getId(),20));
//
//        LIZRandom lizRandom3 = new LIZRandom();
//        lizRandom3.push(new RandomObj(Pie.RONG.getId(),40));
//        lizRandom3.push(new RandomObj(Pie.X2.getId(),15));
//        lizRandom3.push(new RandomObj(Pie.X3.getId(),8));
//        lizRandom3.push(new RandomObj(Pie.X4.getId(),3));
//        lizRandom3.push(new RandomObj(Pie.MINUS.getId(),20));
//
//        LIZRandom lizRandom4 = new LIZRandom();
//        lizRandom4.push(new RandomObj(Pie.RONG.getId(),40));
//        lizRandom4.push(new RandomObj(Pie.X2.getId(),15));
//        lizRandom4.push(new RandomObj(Pie.X3.getId(),8));
//        lizRandom4.push(new RandomObj(Pie.X4.getId(),3));
//        lizRandom4.push(new RandomObj(Pie.MINUS.getId(),20));
//
//        LIZRandom lizRandom = null;
//        int de = 0;
//        List<Level> levels = new ArrayList<>();
//        for(int i = 1; i <= 100; i++){
//            if(i > 0) {
//                lizRandom = lizRandom1;
//                de = 15;
//            }
//            if(i > 10){
//                lizRandom = lizRandom2;
//                de = 20;
//            }
//            if(i > 20){
//                lizRandom = lizRandom3;
//                de = 30;
//            }
//            if(i > 30){
//                lizRandom = lizRandom4;
//                de = 40;
//            }
//
//            Level level = new Level(i);
//            level.enemies = 1;
//            int b = 1;
//            while (b <= 7){
//                Block block = new Block();
//                int maxValue = -1;
//                int countVatCan = 0;
//                int counRong = 0;
//                int counX = 0;
//                for(int p = 0; p < 6; p++){
//                    Pie pie = Pie.fromID((Integer) lizRandom.next().value);
//                    if(pie == Pie.MINUS) countVatCan++;
//                    if(pie == Pie.RONG) counRong++;
//                    if(pie == Pie.X2 || pie == Pie.X3 || pie == Pie.X4) counX++;
//                    block.pieDatas.add(pie.getId());
//                    if(maxValue < pie.getValue()) maxValue = pie.getValue();
//                }
//                if(maxValue == -1 || countVatCan > 4 || counRong >= 6 || counX == 0){
//
//                }else {
//                    if(maxValue > 0){
//                        level.enemies *= maxValue;
//                    }
//                    level.blocks.add(block);
//                    b++;
//                }
//            }
//
//            if(level.enemies > 500){
//                i--;
//                continue;
//            }
////            level.enemies = (int) Math.floor(level.enemies / de);
//            if(level.enemies < de * 2) level.enemies /= 3; else level.enemies = Utils.randomInRange(de, de * 2);
//            levels.add(level);
//        }
//
//        System.out.println(Utils.toJson(levels));

//        SFSArray sfsArray = new SFSArray();
//        CouchbaseDataController couchbaseDataController = new CouchbaseDataController("http://159.89.52.115:8091/pools", "2", "2", "rQCT8h\"8*EGW?g<rwwq/weAHveA!}'s7");
//        long uid = 20006854;
//        int countNull = 0;
//        int countDName = 0;
//        do {
//            System.out.println();
//            try {
//                Object oum = couchbaseDataController.getClient().get("UserCampaignDetailModel_" + uid);
//                if(oum == null){
////                    System.out.println(uid);
////                    countNull++;
////                    if(countNull > 10) {
////                        System.out.println(sfsArray.toJson());
////                        return;
////                    }
//                    uid -= 2;
//                    continue;
//                }
////                countNull = 0;
//                String s = String.valueOf(oum);
//                UserCampaignDetailModel userTowerModel = Utils.fromJson(s, UserCampaignDetailModel.class);
//                UserModel userModel = Utils.fromJson(String.valueOf(couchbaseDataController.getClient().get("UserModel_" + uid)), UserModel.class);
//                if(userModel.displayName.isEmpty()){
//                    countDName++;
//                    System.out.println("dname empty");
//                }
////                System.out.println(Utils.toJson(userTowerModel.userMainCampaignDetail));
//                System.out.println(uid);
//                if(Integer.valueOf(userTowerModel.userMainCampaignDetail.nextStation.split(",")[0]) >= 3){
//                    String s1 = HttpRequestUtils.get("http://ip-api.com/json/" + userModel.ip);
//                    System.out.println(SFSObject.newFromJsonData(s1).getUtfString("regionName") + "," + SFSObject.newFromJsonData(s1).getUtfString("country"));
//                }
//                sfsArray.addSFSObject(SFSObject.newFromJsonData(Utils.toJson(userTowerModel.userMainCampaignDetail)));
//                uid -=2;
//            }catch (Exception e){
//                e.printStackTrace();
////                System.out.println(uid);
//            }finally {
//            }
//        } while (uid > 20002156);
//
//        System.out.println(sfsArray.toJson());

        //phan tich data campaign
        SFSArray sfsArray = SFSArray.newFromJsonData(Utils.loadFile("C:\\Users\\user\\OneDrive\\Desktop\\userMainCampaignDetail.json"));
        List<CampaignRateObj> list = null;
        Map<String, CampaignRateObj> countMap = new HashMap<>();
        for (int i = 0; i < sfsArray.size(); i++){
            ISFSObject sfsObject = sfsArray.getSFSObject(i);
            if(!countMap.containsKey(sfsObject.getUtfString("nextStation"))){
                countMap.put(sfsObject.getUtfString("nextStation"), new CampaignRateObj(sfsObject.getUtfString("nextStation"), 0));
            }
            countMap.put(sfsObject.getUtfString("nextStation"), countMap.get(sfsObject.getUtfString("nextStation")).count());
        }
        list = new ArrayList<>(countMap.values());
        Collections.sort(list);
        for(CampaignRateObj campaignRateObj : list){
            System.out.println("cửa " + (Integer.valueOf(campaignRateObj.state.split(",")[0]) + 1) + "-" + (Integer.valueOf(campaignRateObj.state.split(",")[1]) + 1)  + " : " + campaignRateObj.count);
        }

        //lay data tower
//        SFSArray sfsArray = new SFSArray();
//        CouchbaseDataController couchbaseDataController = new CouchbaseDataController("http://159.89.52.115:8091/pools", "2", "2", "rQCT8h\"8*EGW?g<rwwq/weAHveA!}'s7");
//        long uid = 20004046;
//        int countNull = 0;
//        do {
//            try {
//                Object oum = couchbaseDataController.getClient().get("UserTowerModel_" + uid);
//                if(oum == null){
////                    System.out.println(uid);
////                    countNull++;
////                    if(countNull > 10) {
////                        System.out.println(sfsArray.toJson());
////                        return;
////                    }
//                    uid -= 2;
//                    continue;
//                }
////                countNull = 0;
//                String s = String.valueOf(oum);
//                UserTowerModel userTowerModel = Utils.fromJson(s, UserTowerModel.class);
//                System.out.println(Utils.toJson(userTowerModel));
//                sfsArray.addSFSObject(SFSObject.newFromJsonData(Utils.toJson(userTowerModel)));
//                uid -=2;
//            }catch (Exception e){
//                e.printStackTrace();
//                System.out.println(uid);
//            }finally {
//            }
//        } while (uid > 20002156);
//
//        System.out.println(sfsArray.toJson());

        //phan tich data tower
//        List<TowerRateObj> list = null;
//        Map<Integer, TowerRateObj> countMap = new HashMap<>();
//        SFSArray sfsArray = SFSArray.newFromJsonData(Utils.loadFile("C:\\Users\\user\\OneDrive\\Desktop\\tower.json"));
//        for (int i = 0; i < sfsArray.size(); i++){
//            ISFSObject sfsObject = sfsArray.getSFSObject(i);
//            if(!countMap.containsKey(sfsObject.getInt("floor"))){
//                countMap.put(sfsObject.getInt("floor"), new TowerRateObj(sfsObject.getInt("floor"), 0));
//            }
//            countMap.put(sfsObject.getInt("floor"), countMap.get(sfsObject.getInt("floor")).count());
//        }
//        list = new ArrayList<>(countMap.values());
//        Collections.sort(list);
//        for(TowerRateObj towerRateObj : list){
//            System.out.println("tầng " + towerRateObj.floor + " : " + towerRateObj.count);
//        }
    }

    static class TowerRateObj implements Comparable{
        public int floor;
        public int count;

        public TowerRateObj(int floor, int count) {
            this.floor = floor;
            this.count = count;
        }

        public TowerRateObj count() {
            count ++;
            return this;
        }

        @Override
        public int compareTo(Object o) {
            if(count > ((TowerRateObj) o).count) return -1;
            if(count < ((TowerRateObj) o).count) return 1;
            return 0;
        }
    }

    static class CampaignRateObj implements Comparable{
        public String state;
        public int count;

        public CampaignRateObj(String state, int count) {
            this.state = state;
            this.count = count;
        }

        public CampaignRateObj count() {
            count ++;
            return this;
        }

        @Override
        public int compareTo(Object o) {
            if(count > ((CampaignRateObj) o).count) return -1;
            if(count < ((CampaignRateObj) o).count) return 1;
            return 0;
        }
    }
}
