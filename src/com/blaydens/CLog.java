package com.blaydens;

import org.rspeer.ui.Log;

public class CLog {

    public static enum Level {
        DEBUG,
        INFO,
        USER,
        ERROR
    }

    private static final Level DEBUG_LEVEL = Level.INFO;

    public static void cLog(String msg) {
        cLog(msg, Level.DEBUG);
    }

    public static void cLog(String msg, int level) {
        cLog(msg, Level.values()[level]);
    }

    public static void cLog(String msg, String level) {
        cLog(msg, Level.valueOf(level));
    }

    public static void cLog(String msg, Level level){
        if(DEBUG_LEVEL.compareTo(level) <= 0){
            switch (level) {
                case USER:
                    Log.fine(msg);
                    break;
                case ERROR:
                    Log.severe(msg);
                case DEBUG:
                case INFO:
                default:
                    Log.info(msg);
                    break;
            }
        }
    }
}
