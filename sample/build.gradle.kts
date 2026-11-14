plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}
kotlin {
    jvmToolchain(21)
    jvm()
    sourceSets.jvmMain.dependencies {
        implementation(project(":pill"))
        implementation(compose.desktop.currentOs)
    }
}
compose.desktop { application { mainClass = "io.github.samuolis.pill.sample.MainKt" } }
