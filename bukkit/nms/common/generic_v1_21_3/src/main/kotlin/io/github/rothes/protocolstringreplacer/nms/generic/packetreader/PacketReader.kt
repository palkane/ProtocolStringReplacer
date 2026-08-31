package io.github.rothes.protocolstringreplacer.nms.generic.packetreader

import io.github.rothes.protocolstringreplacer.nms.packetreader.ChatType
import io.github.rothes.protocolstringreplacer.nms.packetreader.IPacketReader
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.ChatType as MinecraftChatType
import net.minecraft.network.protocol.game.ClientboundPlayerChatPacket
import net.minecraft.server.MinecraftServer

class PacketReader: IPacketReader {

    private val registry = MinecraftServer.getServer().registryAccess()
        .lookupOrThrow<MinecraftChatType>(Registries.CHAT_TYPE)
    private val chatTypes = registry.registryKeySet()
        .sortedBy { registry.getId(registry.getValue(it)) }
        .map { it.identifier().path }
        .map { key -> ChatType.entries.find { type -> type.keys.contains(key) }!! }
        .toTypedArray()

    override fun readChatType(packet: ClientboundPlayerChatPacket): ChatType {
        return chatTypes[registry.getId(packet.chatType.chatType.value())]
    }

}
