package com.bamisu.gamelib.entities;

import com.bamisu.gamelib.utils.Utils;
import com.bamisu.gamelib.utils.Utils;

import java.util.*;

public class LIZRandom {
    List<RandomObj> list = new ArrayList<>();
    List<RandomObj> processedList = new ArrayList<>();

    public void push(RandomObj randomObj) {
        if (randomObj.rate > 0) {
            list.add(randomObj);
            list.sort(new Comparator<RandomObj>() {
                @Override
                public int compare(RandomObj o1, RandomObj o2) {
                    if (o1.rate > o2.rate) return 1;
                    if (o1.rate < o2.rate) return -1;
                    return 0;
                }
            });
            processedList.clear();
            for (RandomObj tmp : list) {
                processedList.add(new RandomObj(tmp.value, tmp.rate));
            }

            while (true) {
                boolean isLoop = false;
                for (RandomObj tmpObj : processedList) {
                    if (tmpObj.rate < 1) {
                        isLoop = true;
                    }
                }
                if (!isLoop) break;

                for (RandomObj tmpObj : processedList) {
                    tmpObj.rate *= 10;
                }
            }

//            for (RandomObj tmpObj : processedList) {
//                System.out.println(tmpObj.rate);
//            }
//            System.out.println("=====");

            for (int i = 1; i < processedList.size(); i++) {
                if (i != 0) {
                    processedList.get(i).rate += processedList.get(i - 1).rate;
                }
            }
        }
    }

    public RandomObj next() {
        int index = 0;
        int randNumber = Utils.randomInRange(1, (int) Math.round(processedList.get(processedList.size() - 1).rate));
//        System.out.println("ran " + randNumber + " " + (int) Math.round(processedList.get(processedList.size() - 1).rate));
        for (int i = 0; i < processedList.size(); i++) {
            if (randNumber <= processedList.get(i).rate) {
                index = i;
                break;
            }
        }

        return list.get(index);
    }

    public static void main(String[] args) {
    }
}
