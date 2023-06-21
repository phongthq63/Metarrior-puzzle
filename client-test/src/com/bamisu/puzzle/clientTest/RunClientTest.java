package com.bamisu.puzzle.clientTest;

import com.bamisu.gamelib.task.LizThreadManager;

import java.util.concurrent.TimeUnit;

public class RunClientTest {
    public static void main(String[] args) {
        for (int i = 0; i < 1; i++) {
            LizThreadManager.getInstance().getFixExecutorServiceByName("123").schedule(
                    () -> {
                        new Client("", "localhost", 9933, "1").start();
//                        new Client("", "192.168.1.126", 9933, "s1").start();
                    },
                    100 * i,
                    TimeUnit.MILLISECONDS
            );
        }
    }
}
