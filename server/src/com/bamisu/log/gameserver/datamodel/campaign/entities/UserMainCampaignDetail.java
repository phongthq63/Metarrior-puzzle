package com.bamisu.log.gameserver.datamodel.campaign.entities;

import com.bamisu.log.gameserver.module.campaign.config.MainCampaignConfig;
import com.bamisu.log.gameserver.module.campaign.config.entities.Area;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Create by Popeye on 3:51 PM, 2/5/2020
 */
public class UserMainCampaignDetail {
    public String nextStation = "0,0";
//    public String nextStation = "10,35";
    public Map<Byte,Byte> mapSaveStation = new HashMap<>();
    public Map<String,Byte> logStation = new HashMap<>();

    public boolean canFightStation(int station){
        int areaNext = Integer.parseInt(readNextStation().split(",", 2)[0]);
        int stationNext = Integer.parseInt(readNextStation().split(",", 2)[1]);
        int star = readStarStation(areaNext, station);

        if(station == stationNext) return star != 3;
        if(!mapSaveStation.containsKey((byte)station))return false;

        return star < 3 && star >= 0;
    }

    public int readStarStation(int area, int station){
        return logStation.getOrDefault(readIdStation(area, station), (byte)0);
    }

    public int readStarArea(){
        int star = 0;
        for(Byte station : mapSaveStation.keySet()){
            star += mapSaveStation.get(station);
        }
        return star;
    }

    public int readTotalStar(){
        return logStation.values().parallelStream().mapToInt(Byte::intValue).sum();
    }

    public boolean updateState(int station, int star){
        int[] info = Arrays.stream(readNextStation().split(",", 2)).mapToInt(Integer::parseInt).toArray();
        int currentArea = info[0];
        int nextStation = info[1];

        //Save vao map neu khong du 3 sao
        if(station <= nextStation){
            if(star > 0 && star <= 3){
                if(!updateSaveSation(station, star))return false;
            }else {
                return false;
            }
        }else {
            return false;
        }

        MainCampaignConfig cf = MainCampaignConfig.getInstance();
        //TH neu dang danh station moi nhat
        if(station == nextStation){
            //TH o vi tri cuoi cua chap thi giu nguyen (doi user tu chuyen sang chap khac)
            //Ko thi auto + 1
            if(cf.area.get(currentArea).station.size() - 1 > nextStation){
                updateNextStation(currentArea, nextStation + 1);
            }
        }
        return true;
    }

    public boolean updateArea(){
        int[] info = Arrays.stream(readNextStation().split(",", 2)).mapToInt(Integer::parseInt).toArray();
        int currentArea = info[0];
        int nextStation = info[1];

        MainCampaignConfig cf = MainCampaignConfig.getInstance();
        //TH danh sang chap moi (phai dang focus vao vi tri cuoi cung cua chap cu)
        if(cf.area.get(currentArea).station.size() - 1 == nextStation){
            //TH da danh den chap cuoi cung
            if(cf.area.size() - 1 <= currentArea){
                return false;
            }else {
                updateNextStation(currentArea + 1, 0);
                mapSaveStation.clear();
                return true;
            }
        }
        return false;
    }

    private boolean updateSaveSation(int station, int star){
        if(mapSaveStation.containsKey((byte)station) && mapSaveStation.get((byte)station) >= star){
            return false;
        }
        mapSaveStation.put((byte)station, (byte)star);
        logStation.put(readIdStation(Integer.parseInt(readNextStation().split(",", 2)[0]), station), (byte)star);
        return true;
    }

    private void updateNextStation(int area, int station){
        nextStation = area + "," + station;
    }




    /*---------------------------------------------------------------------------------------------------------------*/
    private String readIdStation(int area, int station){
        return area + "," + station;
    }

    public String readNextStation(){
        int areaNext = Integer.parseInt(nextStation.split(",", 2)[0]);
        int stationNext = Integer.parseInt(nextStation.split(",", 2)[1]);

        MainCampaignConfig cf = MainCampaignConfig.getInstance();
        if(cf.area.size() <= areaNext) areaNext = cf.area.size() - 1;
        if(cf.getArea(areaNext).station.size() <= stationNext) stationNext = cf.getArea(areaNext).station.size() - 1;

        updateNextStation(areaNext, stationNext);
        return nextStation;
    }

}
