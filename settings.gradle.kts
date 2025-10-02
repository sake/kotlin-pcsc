pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}


dependencyResolutionManagement {
    repositories {
        // mavenLocal()
        mavenCentral()
    }
}


rootProject.name = "kotlin-pcsc"

//include("sample")
