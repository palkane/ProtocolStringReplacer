package io.github.rothes.protocolstringreplacer.nms

import io.github.rothes.protocolstringreplacer.nms.packetreader.IBlockEntityTypeGetter
import io.github.rothes.protocolstringreplacer.nms.packetreader.IDisguisedPacketHandler
import io.github.rothes.protocolstringreplacer.nms.packetreader.IMenuTypeGetter
import io.github.rothes.protocolstringreplacer.nms.packetreader.IPacketReader
import java.util.function.Consumer

object NmsManager {

    private const val PACKAGE_PREFIX = "io.github.rothes.protocolstringreplacer.nms"

    private lateinit var minecraftVersion: String
    lateinit var warningFunction: Consumer<String>

    fun setVersion(major: Int, minor: Int, patch: Int) {
        if (major >= 26) {
            setVersionProp("v${major}_${minor}_${patch}")
            try {
                create<IPacketReader>()
                return
            } catch (ignored: ClassNotFoundException) {
                warningFunction.accept("Minecraft version $major.$minor.$patch is not supported by the plugin version you are running on.")
                return
            }
        }

        var m = minor
        while (m < 10) {
            setVersionProp("v1_${major}_$m")
            try {
                create<IPacketReader>()
                return
            } catch (ignored: ClassNotFoundException) {
                m++
            }
        }
        // On older versions we are not using NMS.
        if (major >= 19) {
            warningFunction.accept("Minecraft version 1.$major.$minor is not supported by the plugin version you are running on.")
            m = minor
            while (--m >= 0) {
                setVersionProp("v1_${major}_$m")
                try {
                    create<IPacketReader>()
                    warningFunction.accept("Fallback to 1.$major.$m support and luckily it could be compatible.")
                    return
                } catch (ignored: ClassNotFoundException) {
                }
            }
        }
        setVersionProp("v1_${major}_$minor")
    }

    private fun setVersionProp(version: String) {
        minecraftVersion = version
    }

    val packetReader by lazy { create<IPacketReader>() }
    val disguisedPacketHandler by lazy { create<IDisguisedPacketHandler>() }
    val menuTypeGetter by lazy { create<IMenuTypeGetter>() }
    val blockEntityTypeGetter by lazy { create<IBlockEntityTypeGetter>() }

    private inline fun <reified T> create(): T {
        return T::class.java.instance
    }

    @Suppress("UNCHECKED_CAST")
    private val <T> Class<T>.versioned: Class<out T>
        get() = Class.forName(buildString {
            append(PACKAGE_PREFIX)
            append(".")
            append(minecraftVersion)
            append(this@versioned.`package`.name.substring(PACKAGE_PREFIX.length))
            append('.')
            append(this@versioned.simpleName.substring(1))
        }) as Class<out T>

    private val <T> Class<T>.instance: T
        get() = this.versioned.getConstructor().newInstance() as T

}
