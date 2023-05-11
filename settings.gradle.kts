rootProject.name = "clikt-testkit"

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            version("kotlin", "1.8.21")
            plugin("kotlin.multiplatform", "org.jetbrains.kotlin.multiplatform").versionRef("kotlin")

            plugin("versions", "com.github.ben-manes.versions").version("0.46.0")
            plugin("nexus.publish","io.github.gradle-nexus.publish-plugin").version("1.1.0")

            library("clikt", "com.github.ajalt.clikt:clikt:3.5.2")
            library("kotlinx.coroutines", "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
            library("arrow.fx.coroutines", "io.arrow-kt:arrow-fx-coroutines:1.1.5")

            version("kotest", "5.6.2")
            plugin("kotest", "io.kotest.multiplatform").versionRef("kotest")
            library("kotest.assertions.core", "io.kotest", "kotest-assertions-core").versionRef("kotest")
            library("kotest.framework.engine", "io.kotest", "kotest-framework-engine").versionRef("kotest")
            library("kotest.runner.junit5", "io.kotest", "kotest-runner-junit5").versionRef("kotest")
        }
    }
}

