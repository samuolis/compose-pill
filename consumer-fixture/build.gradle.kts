plugins {
    kotlin("multiplatform") version "2.4.21"
    id("org.jetbrains.compose") version "1.12.2"
    id("org.jetbrains.kotlin.plugin.compose") version "2.4.21"
}
kotlin {
    jvm()
    sourceSets.jvmMain {
        kotlin.srcDir("src/main/kotlin")
        dependencies { implementation("io.github.samuolis:pill:0.1.0-SNAPSHOT") }
    }
}
