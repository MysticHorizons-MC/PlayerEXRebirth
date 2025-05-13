package com.mystichorizonsmc.playerexrebirth.client.data;

public class ClientPrestigeData {

    private static int maxPrestigeLevel = 100;
    private static int currentPrestigeLevel = 0;
    private static boolean toastShown = false;

    public static void setMaxPrestigeLevel(int level) {
        maxPrestigeLevel = level;
    }

    public static int getMaxPrestigeLevel() {
        return maxPrestigeLevel;
    }

    public static void setCurrentPrestigeLevel(int level) {
        currentPrestigeLevel = level;
    }

    public static int getCurrentPrestigeLevel() {
        return currentPrestigeLevel;
    }

    public static boolean shouldShowToast() {
        return !toastShown;
    }

    public static void setToastShown() {
        toastShown = true;
    }
}
