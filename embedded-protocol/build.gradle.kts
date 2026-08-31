plugins {
    `java-library`
    id("io.papermc.paperweight.userdev")
}

val protocolLibSources = file("src/upstream/java")
val overriddenSources = setOf(
    file("$protocolLibSources/com/comphenix/protocol/ProtocolLogger.java").canonicalFile,
    file("$protocolLibSources/com/comphenix/protocol/injector/PacketFilterBuilder.java").canonicalFile,
    file("$protocolLibSources/com/comphenix/protocol/internal/PlatformProvider.java").canonicalFile,
)

sourceSets.main {
    java.srcDir(protocolLibSources)
    java.exclude { it.file.canonicalFile in overriddenSources }
    java.exclude(
        "com/comphenix/protocol/ProtocolLib.java",
        "com/comphenix/protocol/Command*.java",
        "com/comphenix/protocol/PacketLogging.java",
        "com/comphenix/protocol/MultipleLinesPrompt.java",
        "com/comphenix/protocol/metrics/**",
        "com/comphenix/protocol/updater/**",
    )
}

dependencies {
    paperweight.paperDevBundle("${rootProject.property("targetMinecraftVersion")}.build.${rootProject.property("targetPaperBuild")}-stable")
    implementation("net.bytebuddy:byte-buddy:1.18.2")
    compileOnly("io.netty:netty-all:4.2.8.Final")
    compileOnly("net.kyori:adventure-text-serializer-gson:4.25.0")
    compileOnly("com.googlecode.json-simple:json-simple:1.1.1")
    compileOnly("commons-lang:commons-lang:2.6")
}

tasks.jar {
    archiveBaseName.set("protocolstringreplacer-embedded-protocol")
}
