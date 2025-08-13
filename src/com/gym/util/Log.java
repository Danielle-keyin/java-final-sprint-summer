package com.gym.util;

import java.io.IOException;
import java.util.logging.*;

public class Log {
    private static final Logger LOGGER = Logger.getLogger("GymApp");

    static {
        try {
            var handler = new FileHandler("logs/app.log", true);
            handler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(handler);
            LOGGER.setUseParentHandlers(false);
            LOGGER.setLevel(Level.INFO);
        } catch (IOException e) {
            System.err.println("Failed to setup logger: " + e.getMessage());
        }
    }
    public static Logger get(){ return LOGGER; }
}