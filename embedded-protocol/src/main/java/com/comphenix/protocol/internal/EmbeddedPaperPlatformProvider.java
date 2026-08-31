package com.comphenix.protocol.internal;

import io.netty.channel.Channel;
import io.papermc.paper.network.ChannelInitializeListenerHolder;
import java.util.function.Consumer;
import net.kyori.adventure.key.Key;

final class EmbeddedPaperPlatformProvider implements PlatformProvider {

    private static final Key CHANNEL_INITIALIZER_KEY =
            Key.key("protocolstringreplacer", "channel-initializer");

    @Override
    public boolean hasEarlyChannelInitialization() {
        return true;
    }

    @Override
    public Runnable registerChannelInitializer(Consumer<Channel> channelInitializer) {
        ChannelInitializeListenerHolder.addListener(CHANNEL_INITIALIZER_KEY, channelInitializer::accept);
        return () -> ChannelInitializeListenerHolder.removeListener(CHANNEL_INITIALIZER_KEY);
    }
}
