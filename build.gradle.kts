import com.vanniktech.maven.publish.KotlinMultiplatform

group = "nl.w8mr"
version = "0.0.1"

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.publish)

}

kotlin {
    jvm {
        java {
            toolchain {
                languageVersion.set(JavaLanguageVersion.of(11))
            }
        }
    }
    js {
        browser()
        nodejs()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }

}
repositories {
    mavenCentral()
}

mavenPublishing {
    publishToMavenCentral()

    coordinates("nl.w8mr", project.name, "$version")

    pom {
        name.set("Parsek")
        description.set("Simple Version library for Kotlin")
        inceptionYear.set("2025")
        url.set("https://github.com/w8mr/version")

        licenses {
            license {
                name.set("MIT License")
                url.set("https://opensource.org/license/mit")
                distribution.set("https://opensource.org/license/mit")
            }
        }
        issueManagement {
            system.set("Github")
            url.set("https://github.com/w8mr/version/issues")
        }

        developers {
            developer {
                id.set("w8mr")
                name.set("Elmar Wachtmeester")
                url.set("https://github.com/w8mr")
            }
        }

        scm {
            url.set("https://github.com/w8mr/version/")
            connection.set("https://github.com/w8mr/version.git")
            developerConnection.set("scm:git:ssh://git@github.com:w8mr/version.git")
        }
    }
    signAllPublications()
}

