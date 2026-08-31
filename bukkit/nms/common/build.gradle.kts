val serverVer = rootProject.property("targetMinecraftVersion").toString()

dependencies {
    val devBundle = if (serverVer.startsWith("26")) {
        "$serverVer.build.${rootProject.property("targetPaperBuild")}-stable"
    } else {
        "$serverVer-R0.1-SNAPSHOT"
    }
    paperweight.paperDevBundle(devBundle)
}
