plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}
kotlin {
    jvmToolchain(21)
    jvm()
    iosArm64 { binaries.framework { baseName = "PillSample"; isStatic = true } }
    iosSimulatorArm64 { binaries.framework { baseName = "PillSample"; isStatic = true } }
    wasmJs { browser(); binaries.executable() }
    sourceSets {
        commonMain.dependencies {
            implementation(project(":pill"))
            implementation("org.jetbrains.compose.components:components-resources:1.12.2")
        }
        val iosMain = create("iosMain") { dependsOn(commonMain.get()) }
        getByName("iosArm64Main").dependsOn(iosMain)
        getByName("iosSimulatorArm64Main").dependsOn(iosMain)
        jvmMain.dependencies { implementation(compose.desktop.currentOs) }
        wasmJsMain.dependencies { implementation("org.jetbrains.kotlinx:kotlinx-browser:0.5.0") }
    }
}
compose.desktop { application { mainClass = "io.github.samuolis.pill.sample.MainKt" } }

compose.resources { packageOfResClass = "io.github.samuolis.pill.sample.resources" }
