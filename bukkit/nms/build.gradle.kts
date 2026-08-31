plugins {
    kotlin("jvm") version "2.3.10"
    id("java")
    id("com.gradleup.shadow") version "9.2.2"
    id("io.papermc.paperweight.userdev")
}

val serverVer = rootProject.property("targetMinecraftVersion").toString()

dependencies {
    paperweight.paperDevBundle("$serverVer.build.${rootProject.property("targetPaperBuild")}-stable")
    subprojects.filter {
        it.parent == project
    }.forEach {
        api(project(it.path, configuration = "shadow"))
    }
}

subprojects {
    apply(plugin = "io.papermc.paperweight.userdev")
    if (this.name != "common") {
        dependencies {
            compileOnly(project(":bukkit:nms:common"))
        }
    }

    tasks.shadowJar {
        relocate(
            "io.github.rothes.protocolstringreplacer.nms.generic",
            "io.github.rothes.protocolstringreplacer.nms.${project.name}"
        )
    }
}

allprojects {
    tasks {
        build {
            dependsOn(shadowJar)
        }
    }

    paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION
}
