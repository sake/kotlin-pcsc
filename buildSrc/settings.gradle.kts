dependencyResolutionManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }

    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

plugins {
    // apply toolchain plugin, version comes from root project
    id("org.gradle.toolchains.foojay-resolver-convention")
}
