package com.comphenix.protocol;

import com.comphenix.protocol.utility.MinecraftVersion;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.plugin.Plugin;

public class ProtocolLogger {

    private static Logger logger = Logger.getLogger("Minecraft");

    public static void init(Plugin plugin) {
        logger = plugin.getLogger();
    }

    public static void log(Level level, String message, Object... args) {
        logger.log(level, MessageFormat.format(message, args));
    }

    public static void log(String message, Object... args) {
        log(Level.INFO, message, args);
    }

    public static void log(Level level, String message, Throwable ex) {
        logger.log(level, message, ex);
    }

    public static void debug(String message, Object... args) {
    }

    public static void debug(String message, Throwable ex) {
    }

    public static void warnAbove(MinecraftVersion version, String message, Object... args) {
        if (version.atOrAbove()) {
            log(Level.WARNING, message, args);
        }
    }
}
