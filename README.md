[![Maven Central](https://img.shields.io/maven-central/v/com.wolpl.clikt-testkit/clikt-testkit)](https://central.sonatype.com/artifact/com.wolpl.clikt-testkit/clikt-testkit/1.0.0/versions)
[![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/com.wolpl.clikt-testkit/clikt-testkit?label=latest%20snapshot&server=https%3A%2F%2Fs01.oss.sonatype.org)](https://s01.oss.sonatype.org/content/repositories/snapshots/com/wolpl/clikt-testkit/clikt-testkit/)

# Clikt Testkit

Test your [Clikt](https://github.com/ajalt/clikt) cli with a convenient extension function:

```kotlin
class Greeter : CliktCommand() {
    override fun run() {
        val name = prompt("Enter your name")
        echo("Hello $name!")
    }
}

Greeter().test {
    expectOutput("Enter your name: ")
    provideInput("Tester")
    expectOutput("Hello Tester!")
}
```
Clikt Testkit provides a testing dsl for the [Clikt](https://github.com/ajalt/clikt) command line parser library.
Compared to testing with vanilla Clikt, you benefit from
- Easier setup. 
  Just call `.test{}` on the command you want to test and start writing assertions.
  No `.parse()`, catching `ProgramResult` or overwriting the context to set environment variables.
- Functions to assert on console output and provide console input in your test.

## Usage
Clikt Testkit is published through Maven Central.
You can include it in your Gradle project by adding this dependency:
```kotlin
dependencies {
    implementation("com.wolpl.clikt-testkit:clikt-testkit:1.0.0")
}
```

For instructions how to implement tests, please refer to the [Clikt Testkit documentation](https://wolpl.github.io/clikt-testkit/).

## Multiplatform Support
Clikt Testkit is a Kotlin Multiplatform library, currently supporting these targets:
- JVM
- Native (linuxX64, mingwX64, macosX64)