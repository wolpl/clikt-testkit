rootProject.name = "clikt-testkit"

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            version("kotlin", "2.3.0")
            plugin("kotlin.multiplatform", "org.jetbrains.kotlin.multiplatform").versionRef("kotlin")
            plugin("ksp","com.google.devtools.ksp").version("2.3.4")
            plugin("dokka","org.jetbrains.dokka").version("2.1.0")

            plugin("versions", "com.github.ben-manes.versions").version("0.53.0")
            plugin("nexus.publish","io.github.gradle-nexus.publish-plugin").version("2.0.0")
            plugin("maven.publish","com.vanniktech.maven.publish").version("0.35.0")


            library("clikt", "com.github.ajalt.clikt:clikt:5.0.3")
            library("kotlinx.coroutines", "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")

            version("kotest", "6.0.7")
            plugin("kotest", "io.kotest").versionRef("kotest")
            library("kotest.assertions.core", "io.kotest", "kotest-assertions-core").versionRef("kotest")
            library("kotest.framework.engine", "io.kotest", "kotest-framework-engine").versionRef("kotest")
            library("kotest.runner.junit5", "io.kotest", "kotest-runner-junit5").versionRef("kotest")
        }
    }
}
