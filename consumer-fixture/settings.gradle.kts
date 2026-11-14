pluginManagement { repositories { google(); mavenCentral(); gradlePluginPortal() } }
dependencyResolutionManagement {
    repositories {
        maven { url = uri("../build/repository") }
        google()
        mavenCentral()
    }
}
rootProject.name = "pill-consumer-fixture"
