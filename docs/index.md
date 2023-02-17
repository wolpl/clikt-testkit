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
There is no stable version available yet. 
You can use snapshots as described below.

## Snapshots
[![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/com.wolpl/clikt-testkit?label=latest%20snapshot&server=https%3A%2F%2Fs01.oss.sonatype.org)](https://s01.oss.sonatype.org/content/repositories/snapshots/com/wolpl/clikt-testkit/)

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