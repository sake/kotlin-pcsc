import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
    alias(libs.plugins.dokka)
    id("maven-publish")
}


private fun KotlinNativeTarget.withCinterop() {
    compilations.getByName("main") {
        cinterops {
            create("winscard")
        }
    }
}

kotlin {
    applyDefaultHierarchyTemplate()

    jvm()

    val hostOs = System.getProperty("os.name")
    //val isArm64 = System.getProperty("os.arch") == "aarch64"
    when {
        hostOs == "Mac OS X" -> {
            macosArm64 {
                withCinterop()
            }
            macosX64 {
                withCinterop()
            }
        }

        hostOs == "Linux" -> {
            linuxArm64 {
                withCinterop()
            }
            linuxX64 {
                withCinterop()
            }
        }

        hostOs.startsWith("Windows") -> {
            mingwX64() {
                withCinterop()
            }
        }

        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }


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
        //languageSettings.optIn("kotlin.ExperimentalStdlibApi")
        languageSettings.optIn("kotlinx.cinterop.ExperimentalForeignApi")
        languageSettings.optIn("kotlin.experimental.ExperimentalNativeApi")
    }
}

tasks.withType<Test> {
    useJUnitPlatform { }
}


publishing {
//    publications {
//    }
}

dokka {
    dokkaSourceSets {
        commonMain {
            //includeNonPublic.set(false)
            reportUndocumented.set(true)
            skipEmptyPackages.set(true)
            includes.from("src/module.md")
            //sourceRoot(kotlin.sourceSets.getByName("commonMain").kotlin.srcDirs.first())
            //platform.set(org.jetbrains.dokka.Platform.common)
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
