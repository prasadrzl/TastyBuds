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
