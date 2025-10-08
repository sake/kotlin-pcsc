# [kotlin-pcsc][]

[Kotlin Multiplatform][multi] bindings for the [PC/SC][] API ([winscard][]),
which targets:

* **[JNA][]** on JVM (builds a single library for anything [JNA][] supports)
* **[Native][]** for [Kotlin/Native][native] apps

This was developed to support the PC version of [Metrodroid][] (a public transit
card reader).

This repository is a fork of [kotlin-pcsc-orig][] by micolous.
It mainly updates the build system and provides artifacts via maven-central.
There are also some minor changes in the API, but it should mostly work as a drop-in replacement.

## Quick Start

The library is published at maven central and can be added like this:

```kotlin
kotlin {
    // add all targets that should be supported
    linuxX64()
    linuxArm64()
    macosX64()
    macosArm64()
    mingwX64()
    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation("org.electrologic.pcsc:kotlin-pcsc:1.0.0")
        }
    }
}
```

The libraries entry point is the `Context` class.
For an example refer to the [sample project](./sample/).

## Supported target platforms

Platform            | [PC/SC][] Implementation
------------------- |--------------------------
JVM                 | see notes
Linux x86_64        | [pcsclite][]
Linux arm_64        | [pcsclite][]
macOS 10.14+ x86_64 | `PCSC.framework`
macOS 10.14+ arm_64 | `PCSC.framework`
Windows 10+ x86_64  | [WinSCard.dll][winscard]

**Notes:** 

- Cross-compiling **Native** targets with cinterop is not supported.
- The JVM target uses [JNA][], which has a broad [platform support](https://github.com/java-native-access/jna/tree/master/lib/native). 
  Depending on the host system it loads either [WinSCard.dll][winscard], `PCSC.framework` or [pcsclite][].

## API

The [API documentation can be viewed online][api-docs], or built locally with:

`./gradlew :dokkaGenerate`

The generated API documentation can then be found in the [build directory](./build/dokka/html/index.html)

This library _mostly_ follows the PC/SC API, but takes some liberties to make it
easier to use in Kotlin, such as using object orientation, providing helper
methods for common patterns, parsing bitfields into properties, and abstracting
away the small platform-specific API differences.

The result is that the same "common" API can be used on _all_ platforms: see
[the `sample` directory](./sample/) for an example.

The online version of the documentation can be updated with `./update_online_docs.sh`.

## Build and test

All targets, even native ones, require JDK 17 or later to be installed (for Gradle).

The complete distribution can be published to the local maven repository with the following command:

* `./gradlew :publishToMavenLocal`

For publishing to maven central, execute the following:

* `./gradlew :publishToMavenCentral`

The version and the group coordinates of the artifact can be overridden by setting the following gradle project properties:
`./gradlew -Pversion=1.0.0 -Pgroup=com.example :publishToMavenLocal`

Due to the way kotlin multiplatform packages are publishes to maven repositories, it is necessary to build all targets at once.
Unfortunately not all crossplatform toolchains are available on all platforms.
Especially the OSX target is problematic as it is only available on OSX.
That means in order to build the library, OSX must be used.

To run the tests, you need:

* a working [PC/SC][]-compatible smart card reader
* a card inserted into the reader

### JNA (all platforms)

```sh
./gradlew :jnaMainClasses :jnaTest
```

This builds for all platforms, as the prebuilt `net.java.dev.jna` package already includes
platform-specific JNI helpers.  You don't need any cross-compiling or special machine for that.

### Native targets

#### Linux

* Build dependencies: none
* Run-time dependencies: `libpcsclite1`

```sh
./gradlew :linuxX64MainKlibrary :linuxX64Test
```

or

```sh
./gradlew :linuxArm64MainKlibrary :linuxArm64Test
```

#### macOS

* Build dependencies: Xcode in a version compatible with kotlin multiplatform

```sh
./gradlew :macosX64MainKlibrary :macosX64Test
```

or

```sh
./gradlew :macosArm64MainKlibrary :macosArm64Test
```

#### Windows

**Note:** Only `x86_64` target is currently supported.

```powershell
.\gradlew :mingwX64MainKlibrary :mingwX64Test
```

## Runtime notes

### Linux (JNA and Native)

Install `libpcsclite1` and `pcscd` packages.

If you're using a reader with NXP PN53x series chipset (eg: ACS ARC122U), you
need to disable the `pn533` and `pn533_usb` modules:

```sh
# First, unplug the card reader.

# On Linux 3.1 - 4.6:
echo "blacklist pn533" | sudo tee -a /etc/modprobe.d/blacklist.conf
sudo rmmod pn533

# On Linux 4.7 and later:
echo "blacklist pn533_usb" | sudo tee -a /etc/modprobe.d/blacklist.conf
sudo rmmod pn533_usb

# Finally, plug the card reader in again.
```

The `pn533`/`pn533_usb` module is a driver for a new Linux-kernel-specific NFC
subsystem, which is **incompatible with all existing software**, including
`libacsccid1` (its PC/SC IFD handler).

## FAQ

### Is there sample code?

Yes!  See [the `sample` directory of this repository](./sample/).

This supports building on all target platforms, and includes a `jnaFatJar` task, which pulls in all
dependencies to a single JAR file.

### How does this relate or compare to...

#### `javax.smartcardio`?

This is _entirely different_, and does not support these APIs at all, even when
they are available (on Java 8 and earlier).

If you want to use that API, take a look at [jnasmartcardio][]. `kotlin-pcsc`'s
JNA implementation was inspired by it.

#### intarsys smartcard-io?

[intarsys smartcard-io][intarsys] is a Java/JRE library which provides a
Java-friendly PC/SC API (and a `javax.smartcardio` wrapper).

While it _can_ be used with Kotlin, it only targets the JRE (not Native).

### What about mobile (Android / iOS) support?

This is explicitly _not_ in scope for this project.

Most mobile devices do not offer a [PC/SC][]-compatible API. The few devices
that _do_ run a regular enough Linux userland that you should be able to build
using that.

### How do I use this to connect to FeliCa / MIFARE / etc?

You'll need to provide your own implementation of those protocols. PC/SC only provides a very low
level interface, and you'll be sending `ByteArray` to the ICC and getting `ByteArray` back.

We don't even parse the APDUs for you...

[api-docs]: https://sake.github.io/kotlin-pcsc/index.html
[intarsys]: https://github.com/intarsys/smartcard-io
[JNA]: https://github.com/java-native-access/jna
[jnasmartcardio]: https://github.com/jnasmartcardio/jnasmartcardio
[kotlin-pcsc]: https://github.com/sake/kotlin-pcsc
[kotlin-pcsc-orig]: https://github.com/micolous/kotlin-pcsc
[Metrodroid]: https://github.com/metrodroid/metrodroid
[multi]: https://kotlinlang.org/docs/reference/multiplatform.html
[native]: https://kotlinlang.org/docs/reference/native-overview.html
[PC/SC]: https://www.pcscworkgroup.com/
[pcsclite]: https://pcsclite.apdu.fr/
[winscard]: https://docs.microsoft.com/en-us/windows/win32/api/winscard/
