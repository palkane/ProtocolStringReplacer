package com.comphenix.protocol.injector;

import com.comphenix.protocol.async.AsyncFilterManager;
import com.comphenix.protocol.error.ErrorReporter;
import com.comphenix.protocol.scheduler.ProtocolScheduler;
import com.comphenix.protocol.utility.MinecraftVersion;
import javax.annotation.Nonnull;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;

public class PacketFilterBuilder {

    private Server server;
    private Plugin library;
    private MinecraftVersion mcVersion;
    private ErrorReporter reporter;
    private ProtocolScheduler scheduler;
    private AsyncFilterManager asyncManager;

    public PacketFilterBuilder server(@Nonnull Server server) {
        this.server = server;
        return this;
    }

    public PacketFilterBuilder library(@Nonnull Plugin library) {
        this.library = library;
        return this;
    }

    public PacketFilterBuilder minecraftVersion(@Nonnull MinecraftVersion mcVersion) {
        this.mcVersion = mcVersion;
        return this;
    }

    public PacketFilterBuilder reporter(@Nonnull ErrorReporter reporter) {
        this.reporter = reporter;
        return this;
    }

    public PacketFilterBuilder scheduler(@Nonnull ProtocolScheduler scheduler) {
        this.scheduler = scheduler;
        return this;
    }

    public Server getServer() {
        return server;
    }

    public Plugin getLibrary() {
        return library;
    }

    public MinecraftVersion getMinecraftVersion() {
        return mcVersion;
    }

    public ErrorReporter getReporter() {
        return reporter;
    }

    public AsyncFilterManager getAsyncManager() {
        return asyncManager;
    }

    public InternalManager build() {
        if (reporter == null) {
            throw new IllegalArgumentException("reporter cannot be NULL.");
        }
        if (scheduler == null) {
            throw new IllegalArgumentException("scheduler cannot be NULL.");
        }

        asyncManager = new AsyncFilterManager(reporter, scheduler);
        return new PacketFilterManager(this);
    }
}
