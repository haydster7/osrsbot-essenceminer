package com.blaydens;

import org.rspeer.ui.Log;

public class CLog {

    private void cLog(String msg) {
        cLog(msg, Main.Level.DEBUG);
    }

    private void cLog(String msg, int level) {
        cLog(msg, Main.Level.values()[level]);
    }

    private void cLog(String msg, String level) {
        cLog(msg, Main.Level.valueOf(level));
    }

    private void cLog(String msg, Main.Level level){
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
