import org.jetbrains.kotlin.konan.properties.loadProperties

val propsFile = buildFile.parentFile.parentFile.resolve("gradle.properties")
val props = loadProperties(propsFile.absolutePath)

val javaToolchain: String by props
kotlin {
    jvmToolchain {
        languageVersion = JavaLanguageVersion.of(javaToolchain)
    }
}

plugins {
    // Support convention plugins written in Kotlin. Convention plugins are build scripts in 'src/main' that automatically become available as plugins in the main build.
    `kotlin-dsl`
}

dependencies {
    implementation(libs.kotlin.gradlePlugin)
}
