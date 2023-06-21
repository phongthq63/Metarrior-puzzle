package com.bamisu.log.gameserver.module.IAP;

import com.bamisu.log.gameserver.module.IAP.defind.ETimeType;
import com.bamisu.gamelib.utils.Utils;

public class TimeUtils {
    /**
     * lay delta time theo type
     * @param type
     * @param timeStamp tinh = giay
     * @return
     */
    public static final int getDeltaTimeToTime(ETimeType type, int timeStamp){
        if(type == null || timeStamp <= 0){
            return -1;
        }
        switch (type){
            case HOURS_1:
                return 3600 - (Utils.getTimestampInSecond() - timeStamp);

            case HOURS_8:
                return 28800 - (Utils.getTimestampInSecond() - timeStamp);

            case HOURS_12:
                return 43200 - (Utils.getTimestampInSecond() - timeStamp);

            case NEW_DAY:
                return (int) Utils.getDeltaSecondsToEndDay();

            case NEW_7_DAY:
                int time = (int) ((7 - 1 - ((Utils.getTimestampInSecond() - timeStamp) / 86400)) * 86400 + Utils.getDeltaSecondsToEndDay());
                return (time > 0) ? time : 0;

            case NEW_WEEK:
                return (int) Utils.getDeltaSecondsToEndWeek();

            case NEW_2_WEEK:
                int deltaTime = Utils.getTimestampInSecond() - timeStamp;
                if(deltaTime <= 604800) return (int) Utils.getDeltaSecondsToEndWeek(2);
                if(deltaTime > 1209600) return 0;
                return (int) Utils.getDeltaSecondsToEndWeek(1);

            case NEW_MONTH:
                return (int) Utils.getDeltaSecondsToEndMonth();

            case DAY_1:
                return 86400 - (Utils.getTimestampInSecond() - timeStamp);

            case DAY_7:
                return 604800 - (Utils.getTimestampInSecond() - timeStamp);

            case DAY_28:
                return 2419200 - (Utils.getTimestampInSecond() - timeStamp);

            case DAY_30:
                return 2592000 - (Utils.getTimestampInSecond() - timeStamp);

            case DAY_33:
                return 2851200 - (Utils.getTimestampInSecond() - timeStamp);

            case DAY_42:
                return 3628800 - (Utils.getTimestampInSecond() - timeStamp);

            case DAY_45:
                return 3888000 - (Utils.getTimestampInSecond() - timeStamp);

            case DAY_90:
                return 7776000 - (Utils.getTimestampInSecond() - timeStamp);

            default:
                return -1;
        }
    }
    /**
     * lay delta time theo type
     * @param type
     * @param timeStamp tinh = giay
     * @return
     */
    public final static boolean isTimeTo(ETimeType type, int timeStamp){
        if(type == null){
            return false;
        }
        switch (type){
            case HOURS_1:
                return Utils.getTimestampInSecond() - timeStamp > 3600;

            case HOURS_8:
                return Utils.getTimestampInSecond() - timeStamp > 28800;

            case HOURS_12:
                return Utils.getTimestampInSecond() - timeStamp > 43200;

            case NEW_DAY:
                return Utils.isNewDay(timeStamp);

            case NEW_7_DAY:
                int day = (Utils.getTimestampInSecond() - timeStamp) / 86400;
                if(Utils.isNewDay(timeStamp + day * 86400)) day += 1;
                return day >= 7;

            case NEW_WEEK:
                return Utils.isNewWeek(timeStamp);

            case NEW_2_WEEK:
                if(Utils.getTimestampInSecond() - timeStamp <= 604800) return false;
                if(Utils.getTimestampInSecond() - timeStamp >= 1209600) return true;

                return Utils.isNewWeek(timeStamp + 604800);

            case NEW_MONTH:
                return Utils.isNewMonth(timeStamp);

            case DAY_1:
                return Utils.getTimestampInSecond() - timeStamp > 86400;

            case DAY_7:
                return Utils.getTimestampInSecond() - timeStamp > 604800;

            case DAY_28:
                return Utils.getTimestampInSecond() - timeStamp > 2419200;

            case DAY_30:
                return Utils.getTimestampInSecond() - timeStamp > 2592000;

            case DAY_33:
                return Utils.getTimestampInSecond() - timeStamp > 2851200;

            case DAY_42:
                return Utils.getTimestampInSecond() - timeStamp > 3628800;

            case DAY_45:
                return Utils.getTimestampInSecond() - timeStamp > 3888000;

            case DAY_90:
                return Utils.getTimestampInSecond() - timeStamp > 7776000;
        }
        return false;
    }
}
