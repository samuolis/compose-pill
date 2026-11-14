import com.vanniktech.maven.publish.DeploymentValidation

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.android.kotlin.multiplatform.library")
    id("com.vanniktech.maven.publish")
    `maven-publish`
}

group = "io.github.samuolis"
version = providers.gradleProperty("pillVersion").getOrElse("0.1.0-SNAPSHOT")

if (providers.gradleProperty("release").isPresent) {
    mavenPublishing {
        publishToMavenCentral(automaticRelease = true, validateDeployment = DeploymentValidation.PUBLISHED)
        signAllPublications()
    }
}

kotlin {
    jvmToolchain(21)
    android {
        namespace = "io.github.samuolis.pill"
        compileSdk { version = release(37) { minorApiLevel = 2 } }
        minSdk = 24
    }
    jvm()
    iosArm64()
    iosSimulatorArm64()
    wasmJs { browser() }
    sourceSets {
        commonMain.dependencies {
            api(compose.runtime)
            api(compose.foundation)
            api(compose.ui)
            implementation("dev.chrisbanes.haze:haze:2.1.0")
            implementation("dev.chrisbanes.haze:haze-blur:2.1.0")
            implementation("dev.chrisbanes.haze:haze-glass:2.1.0")
        }
        commonTest.dependencies { implementation(kotlin("test")) }
        jvmTest.dependencies {
            implementation("org.jetbrains.compose.ui:ui-test-junit4:1.12.2")
            implementation(compose.desktop.currentOs)
        }
    }
}

publishing {
    repositories { maven { name = "LocalFixture"; url = uri(rootProject.layout.buildDirectory.dir("repository")) } }
    publications.withType<MavenPublication>().configureEach {
        pom {
            name = "Compose Pill"
            description = "Floating navigation and frosted chrome for Compose Multiplatform"
            url = "https://github.com/samuolis/compose-pill"
            licenses { license { name = "Apache License, Version 2.0"; url = "https://www.apache.org/licenses/LICENSE-2.0.txt" } }
            developers { developer { id = "samuolis"; name = "Lukas Samuolis" } }
            scm { url = "https://github.com/samuolis/compose-pill" }
        }
    }
}
