plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.versions)
    alias(libs.plugins.kotest)
    alias(libs.plugins.ksp)
    alias(libs.plugins.maven.publish)
    alias(libs.plugins.dokka)
}

group = "com.wolpl.clikt-testkit"
version = "3.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(11)

    jvm()
    macosX64()
    linuxX64()
    mingwX64()

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.clikt)
                implementation(libs.kotlinx.coroutines)
                implementation(libs.kotest.assertions.core)
            }
        }
        commonTest {
            dependencies {
                implementation(libs.kotest.framework.engine)
            }
        }

        jvmTest {
            dependencies {
                implementation(libs.kotest.runner.junit5)
            }
        }
    }
}

tasks.named<Test>("jvmTest") {
    useJUnitPlatform()
    filter {
        isFailOnNoMatchingTests = true
    }
}

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()
    coordinates(group.toString(), "clikt-testkit", version.toString())
    pom {
        name.set("Clikt Testkit")
        description.set("Testing functions for the Clikt command line parser library.")
        url.set("https://github.com/wolpl/clikt-testkit")
        licenses {
            license {
                name.set("MIT License")
                url.set("https://www.opensource.org/licenses/mit-license.php")
            }
        }
        developers {
            developer {
                id.set("wolpl")
                url.set("https://github.com/wolpl")
            }
        }

        scm {
            url.set("https://github.com/wolpl/clikt-testkit")
        }
    }
}
