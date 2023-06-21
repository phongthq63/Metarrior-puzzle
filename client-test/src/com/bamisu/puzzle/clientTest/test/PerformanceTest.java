package com.bamisu.puzzle.clientTest.test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PerformanceTest {
    private volatile int index = 0;
    private volatile boolean checkResponse = true;
    private Map<Integer,Long> mapCommucate = new ConcurrentHashMap<>();

    private final Object lockIndex = new Object();

    public void onExtensionRequest(){
        synchronized (lockIndex){
//            //System.out.println("index : " + index + " --- reponse : " + checkResponse);
            if(checkResponse){
                mapCommucate.put(index, System.currentTimeMillis());
                checkResponse = false;
            }else {
                mapCommucate.put(index, System.currentTimeMillis());
                checkResponse = false;
            }
        }
    }

    public void onExtensionResponse(){
        synchronized (lockIndex){
            checkResponse = true;

            Long timeRequest = mapCommucate.get(index);
            if(timeRequest != null) mapCommucate.put(index, System.currentTimeMillis() - timeRequest);

            index++;
        }
    }

    public void show(){
        synchronized (lockIndex){
            if(mapCommucate.isEmpty())return;
            System.out.println(
                    "--------------\n" +
                    "- COUNT : " + mapCommucate.size() + "\n" +
                    "- AVERAGE : " + Math.round(mapCommucate.values().parallelStream().filter(index -> index < 10000000).mapToLong(x -> x).average().getAsDouble() * 1000) / 1000 + "ms \n" +
                    "- MAX : " + mapCommucate.values().parallelStream().mapToLong(x -> x).max().getAsLong() + "ms \n" +
                    "- MIN : " + mapCommucate.values().parallelStream().mapToLong(x -> x).min().getAsLong() + "ms \n" +
                    "--------------\n");
        }
        clear();
    }

    public void clear(){
        synchronized (lockIndex){
            index = 0;
            mapCommucate.clear();
        }
    }
}
