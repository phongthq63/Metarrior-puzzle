package com.bamisu.gamelib.utils;

import java.util.UUID;

public class GenSessionKeyUtil {
	public static String genSessionKey() {
		return UUID.randomUUID().toString();
	}
}
