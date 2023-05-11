plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.versions)
    alias(libs.plugins.kotest)
    `maven-publish`
    signing
    alias(libs.plugins.nexus.publish)
}

group = "com.wolpl.clikt-testkit"
version = "1.0.1-SNAPSHOT"

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
        val commonMain by getting {
            dependencies {
                implementation(libs.clikt)
                implementation(libs.kotlinx.coroutines)
                implementation(libs.kotest.assertions.core)
                implementation(libs.arrow.fx.coroutines)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotest.framework.engine)
            }
        }
        val jvmMain by getting
        val jvmTest by getting {
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

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
        }
    }
}

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
}

publishing {
    publications.withType<MavenPublication>() {
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
        artifact(javadocJar)
    }
}

val signingKey: String? by project
signing {
    val signingKey: String? by project
    val signingPassword: String? by project
    if (signingKey != null) {
        println("Signing key is set.")
        useInMemoryPgpKeys(signingKey, signingPassword)
    }
    sign(publishing.publications)
}