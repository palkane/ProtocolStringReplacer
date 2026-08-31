package com.comphenix.protocol.internal;

import io.netty.channel.Channel;
import java.util.function.Consumer;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.Plugin;

public interface PlatformProvider {

    static PlatformProvider get() {
        return Holder.INSTANCE;
    }

    boolean hasEarlyChannelInitialization();

    Runnable registerChannelInitializer(Consumer<Channel> channelInitializer);

    default void registerCommand(Plugin plugin, String name, CommandExecutor executor) {
        throw new UnsupportedOperationException("The embedded protocol layer does not expose ProtocolLib commands.");
    }

    final class Holder {
        private static final PlatformProvider INSTANCE = new EmbeddedPaperPlatformProvider();

        private Holder() {
        }
    }
}
