# Getting Started

Clikt Testkit provides a testing dsl for the [Clikt](https://github.com/ajalt/clikt) command line parser library:

```kotlin
class HelloWorld : CliktCommand() {
    override fun run() {
        echo("Hello World!")
    }
}

HelloWorld().test {
    expectOutput("Hello World!")
}
```

Compared to testing with vanilla Clikt, you benefit from

- Easier setup. 
  Just call `.test{}` on the command you want to test and start writing assertions.
  No `.parse()`, catching `ProgramResult` or overwriting the context to set environment variables.
- Functions to assert on console output and provide console input in your test.

## Use Clikt Testkit in your project
[![Maven Central](https://img.shields.io/maven-central/v/com.wolpl.clikt-testkit/clikt-testkit)](https://central.sonatype.com/artifact/com.wolpl.clikt-testkit/clikt-testkit/1.0.0/versions)

Clikt Testkit is published through Maven Central.
To use it in your Gradle project, include the following dependency:
```kotlin
dependencies {
    implementation("com.wolpl.clikt-testkit:clikt-testkit:1.0.0")
}
```

You can also select a specific target platform, by appending it to the library name, e.g. for the JVM:
```kotlin
dependencies {
    implementation("com.wolpl.clikt-testkit:clikt-testkit-jvm:1.0.0")
}
```

## Snapshots
[![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/com.wolpl.clikt-testkit/clikt-testkit?label=latest%20snapshot&server=https%3A%2F%2Fs01.oss.sonatype.org)](https://s01.oss.sonatype.org/content/repositories/snapshots/com/wolpl/clikt-testkit/clikt-testkit/)

To use snapshots, add this repository to your Gradle config:
```kotlin
repositories {
    maven {
        url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    }
}
```

## Multiplatform Support
Clikt Testkit is a Kotlin Multiplatform library, currently supporting these targets:

- JVM
- Native (linuxX64, mingwX64, macosX64)