package com.bamisu.gamelib.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RateObject {
    List<Object> listRandom = new ArrayList<>();
    List<Rate> listRate = new ArrayList<>();
    Random random = new Random();

    public RateObject() {

    }

    public void push(double rate, Object object) {
        listRate.add(new Rate(rate, object));
    }

    public Object get() {
        if (listRandom.isEmpty()) {
            genListRandom();
        }

        return listRandom.get(random.nextInt(listRandom.size()));
    }

    private void genListRandom() {
        double min = listRate.get(0).rate;
        for (Rate rate : listRate) {
            if (min > rate.rate) {
                min = rate.rate;
            }
        }

        int realRate = 1;
        while (min < 1) {
            min*=10;
            realRate *= 10;
        }

        for (Rate rate : listRate) {
            for (int i = 0; i < (rate.rate * realRate); i++){
                listRandom.add(rate.value);
            }
        }
    }

    public void clear() {
        listRandom.clear();
        listRate.clear();
    }

    public static void main(String[] args) {
        RateObject rateObject = new RateObject();
        rateObject.push(0.5, 1);
        rateObject.push(0.7, 2);
        rateObject.push(0.6, 3);
        rateObject.push(0.20, 4);
        rateObject.push(0.10, 5);

        System.out.println(rateObject.get());
    }
}

class Rate {
    public double rate;
    public Object value;

    public Rate(double rate, Object value) {
        this.rate = rate;
        this.value = value;
    }
}


