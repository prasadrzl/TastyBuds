pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    // Lets Gradle auto-download a JDK 17 toolchain (via jvmToolchain(17) in the convention
    // plugins) since the system JDK here is too new for Kotlin 1.9.22/AGP 8.5.2 to parse.
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "TastyBuds"
include(":app")

include(":core:common")
include(":core:ui")
include(":core:data")

include(":feature:onboarding")
include(":feature:home")
include(":feature:restaurants")
include(":feature:orders")
include(":feature:favorites")
include(":feature:account")
