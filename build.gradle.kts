import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
    alias(libs.plugins.dokka)
    alias(libs.plugins.maven.publish.vannitek)
}

private fun KotlinNativeTarget.withCinterop(withIncludes: Boolean = false) {
    compilations.getByName("main") {
        cinterops {
            val winscard by creating {
                if (withIncludes) {
                    includeDirs.allHeaders(layout.projectDirectory.dir("src/nativeInterop/cinterop/include"))
                    includeDirs.allHeaders(layout.projectDirectory.dir("src/nativeInterop/cinterop/include/PCSC"))
                }
            }
        }
        defaultSourceSet {
            kotlin.srcDirs(layout.projectDirectory.dir("src/sharedNativeMain/kotlin"))
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_9
    targetCompatibility = JavaVersion.VERSION_1_9
}

kotlin {
    applyDefaultHierarchyTemplate()

    jvm {
        this.compilerOptions {
            jvmTarget = JvmTarget.JVM_9
        }
    }

    macosArm64 {
        withCinterop()
    }
    macosX64 {
        withCinterop()
    }

    linuxArm64 {
        withCinterop(true)
    }
    linuxX64 {
        withCinterop(true)
    }

    mingwX64 {
        withCinterop()
    }

    // make sure source jars are built
    withSourcesJar(true)

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlin.coroutines.core)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }

        jvmMain.dependencies {
            api(libs.jna)
        }
        jvmTest.dependencies {
        }
    }

    sourceSets.all {
        // languageSettings.optIn("kotlin.ExperimentalStdlibApi")
        languageSettings.optIn("kotlinx.cinterop.ExperimentalForeignApi")
        languageSettings.optIn("kotlin.experimental.ExperimentalNativeApi")
    }
    compilerOptions {
        // suppress warnings for actual object implementations
        // https://youtrack.jetbrains.com/issue/KT-61573
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }
}

tasks.withType<Test> {
    useJUnitPlatform { }
}

mavenPublishing {
    publishToMavenCentral()

    signAllPublications()

    pom {
        name = "kotlin-pcsc"
        description = "Kotlin wrapper for PC/SC (winscard) API"
        inceptionYear = "2019"
        url = "https://github.com/sake/kotlin-pcsc/"
        licenses {
            license {
                name = "The Apache License, Version 2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                distribution = "https://www.apache.org/licenses/LICENSE-2.0.txt"
            }
        }
        developers {
            developer {
                id = "micolous"
                name = "Michael Farrell"
            }
            developer {
                id = "sake"
                name = "Tobias Wich"
            }
        }
        scm {
            url = "https://github.com/sake/kotlin-pcsc/"
            connection = "scm:git:git://github.com/sake/kotlin-pcsc.git"
            developerConnection = "scm:git:ssh://git@github.com/sake/kotlin-pcsc.git"
        }
    }
}

dokka {
    dokkaSourceSets {
        commonMain {
            // includeNonPublic.set(false)
            reportUndocumented.set(true)
            skipEmptyPackages.set(true)
            includes.from("src/module.md")
            // sourceRoot(kotlin.sourceSets.getByName("commonMain").kotlin.srcDirs.first())
            // platform.set(org.jetbrains.dokka.Platform.common)
            perPackageOption {
                matchingRegex.set("au\\.id\\.micolous\\.kotlin\\.pcsc\\.(jna|internal|native)(\$|\\\\.).*")
                suppress.set(true)
            }
        }

        // There are source sets for each platform-specific target. Our API is only the `common`
        // source set, so we intentionally don't generate docs for the other targets. Also,
        // building docs for those targets requires a working (cross-)compiler... which is hard. :)
        configureEach {
            suppress.set(name != "commonMain")
        }
    }
}

afterEvaluate {
    tasks.filterIsInstance<AbstractArchiveTask>().forEach {
        it.isPreserveFileTimestamps = false
        it.isReproducibleFileOrder = true
    }
}
