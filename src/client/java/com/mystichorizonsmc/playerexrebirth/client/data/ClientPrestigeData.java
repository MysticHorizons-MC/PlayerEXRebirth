package com.mystichorizonsmc.playerexrebirth.client.data;

public class ClientPrestigeData {

    private static int currentPrestigeLevel = 0;
    private static int effectiveMaxLevel = 100;
    private static boolean toastShown = false;

    public static int getCurrentPrestigeLevel() {
        return currentPrestigeLevel;
    }

    public static void setCurrentPrestigeLevel(int level) {
        currentPrestigeLevel = level;
    }

    public static boolean shouldShowToast() {
        return !toastShown;
    }

    public static void setToastShown() {
        toastShown = true;
    }

    public static int getEffectiveMaxLevel() {
        return effectiveMaxLevel;
    }

    public static void setEffectiveMaxLevel(int level) {
        effectiveMaxLevel = level;
    }
}
