package com.comphenix.protocol;

import com.comphenix.protocol.error.BasicErrorReporter;
import com.comphenix.protocol.error.ErrorReporter;
import com.comphenix.protocol.injector.InternalManager;
import com.comphenix.protocol.injector.PacketFilterManager;
import com.comphenix.protocol.scheduler.DefaultScheduler;
import com.comphenix.protocol.scheduler.FoliaScheduler;
import com.comphenix.protocol.scheduler.ProtocolScheduler;
import com.comphenix.protocol.utility.ByteBuddyFactory;
import com.comphenix.protocol.utility.MinecraftVersion;
import com.comphenix.protocol.utility.Util;
import org.bukkit.plugin.Plugin;

public final class EmbeddedProtocolLibrary {

    private static InternalManager manager;

    private EmbeddedProtocolLibrary() {
    }

    public static synchronized void initialize(Plugin plugin) {
        if (manager != null) {
            return;
        }

        ProtocolLogger.init(plugin);
        ByteBuddyFactory.getInstance().setClassLoader(plugin.getClass().getClassLoader());

        ErrorReporter reporter = new BasicErrorReporter();
        ProtocolScheduler scheduler = Util.isUsingFolia()
                ? new FoliaScheduler(plugin)
                : new DefaultScheduler(plugin);

        manager = (InternalManager) PacketFilterManager.newBuilder()
                .server(plugin.getServer())
                .library(plugin)
                .minecraftVersion(new MinecraftVersion(plugin.getServer()))
                .reporter(reporter)
                .scheduler(scheduler)
                .build();

        ProtocolLibrary.init(plugin, null, manager, scheduler, reporter);
        manager.registerEvents(plugin.getServer().getPluginManager(), plugin);
    }

    public static synchronized void shutdown() {
        if (manager != null) {
            manager.close();
            manager = null;
        }
    }
}
