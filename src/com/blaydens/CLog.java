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

    public static void log(String msg) {
        log(msg, Level.DEBUG);
    }

    public static void log(String msg, int level) {
        log(msg, Level.values()[level]);
    }

    public static void log(String msg, String level) {
        log(msg, Level.valueOf(level));
    }

    public static void log(String msg, Level level){
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
