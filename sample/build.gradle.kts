plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}
kotlin {
    jvmToolchain(21)
    jvm()
    wasmJs { browser(); binaries.executable() }
    sourceSets {
        commonMain.dependencies { implementation(project(":pill")) }
        jvmMain.dependencies { implementation(compose.desktop.currentOs) }
        wasmJsMain.dependencies { implementation("org.jetbrains.kotlinx:kotlinx-browser:0.5.0") }
    }
}
compose.desktop { application { mainClass = "io.github.samuolis.pill.sample.MainKt" } }
