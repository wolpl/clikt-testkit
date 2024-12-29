rootProject.name = "clikt-testkit"

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            version("kotlin", "2.1.0")
            plugin("kotlin.multiplatform", "org.jetbrains.kotlin.multiplatform").versionRef("kotlin")

            plugin("versions", "com.github.ben-manes.versions").version("0.51.0")
            plugin("nexus.publish","io.github.gradle-nexus.publish-plugin").version("2.0.0")

            library("clikt", "com.github.ajalt.clikt:clikt:5.0.2")
            library("kotlinx.coroutines", "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")

            version("kotest", "6.0.0.M1")
            plugin("kotest", "io.kotest.multiplatform").versionRef("kotest")
            library("kotest.assertions.core", "io.kotest", "kotest-assertions-core").versionRef("kotest")
            library("kotest.framework.engine", "io.kotest", "kotest-framework-engine").versionRef("kotest")
            library("kotest.runner.junit5", "io.kotest", "kotest-runner-junit5").versionRef("kotest")
        }
    }
}
