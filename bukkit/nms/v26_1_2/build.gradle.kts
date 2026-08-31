val serverVer = "26.1.2"

sourceSets.main {
    kotlin.srcDir("../common/generic_v1_21_3/src/main/kotlin")
}

dependencies {
    paperweight.paperDevBundle("$serverVer.build.${rootProject.property("targetPaperBuild")}-stable")
}
