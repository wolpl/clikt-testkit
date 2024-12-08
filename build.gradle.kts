plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.versions)
    alias(libs.plugins.kotest)
    `maven-publish`
    signing
    alias(libs.plugins.nexus.publish)
    alias(libs.plugins.dokka)
}

group = "com.wolpl.clikt-testkit"
version = "3.0.0"

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

publishing {
    publications.withType<MavenPublication> {
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
        val dokkaJar = project.tasks.register("${name}DokkaJar", Jar::class) {
            group = JavaBasePlugin.DOCUMENTATION_GROUP
            description = "Assembles Kotlin docs with Dokka into a Javadoc jar"
            archiveClassifier.set("javadoc")
            from(tasks.named("dokkaHtml"))

            // Each archive name should be distinct, to avoid implicit dependency issues.
            // We use the same format as the sources Jar tasks.
            // https://youtrack.jetbrains.com/issue/KT-46466
            archiveBaseName.set("${archiveBaseName.get()}-${name}")
        }
        artifact(dokkaJar)
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