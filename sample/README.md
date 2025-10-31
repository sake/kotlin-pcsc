# kotlin-pcsc sample

This is a simple example application that uses [kotlin-pcsc][].

This implements [the PC/SC sample application][pcsc-sample] developed by [Ludovic Rousseau][].

The main source file is [Sample.kt](./src/commonMain/kotlin/Sample.kt).

## Running

From the root of the `kotlin-pcsc` repository:

* JNA: `./gradlew :sample:jvmRun`
* Linux x86_64: `./gradlew :sample:runPcsc_sampleReleaseExecutableLinuxX64`
* Linux arm_64: `./gradlew :sample:runPcsc_sampleReleaseExecutableLinuxArm64`
* macOS x86_64: `./gradlew :sample:runPcsc_sampleReleaseExecutableMacosX64`
* macOS arm_64: `./gradlew :sample:runPcsc_sampleReleaseExecutableMacosArm64`
* Windows x86_64: `.\gradlew :sample:runPcsc_sampleReleaseExecutableMingwX64`

[kotlin-pcsc]: https://github.com/sake/kotlin-pcsc
[Ludovic Rousseau]: https://ludovicrousseau.blogspot.com/
[pcsc-sample]: https://ludovicrousseau.blogspot.com/2010/04/pcsc-sample-in-different-languages.html
