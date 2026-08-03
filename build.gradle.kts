// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.android.test) apply false
    alias(libs.plugins.androidx.baselineprofile) apply false
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin") version "2.0.1" apply false
    jacoco
}

// ---------------------------------------------------------------------------
// Aggregated unit-test coverage across every test-bearing module.
// Run: ./gradlew jacocoAggregatedReport
// Merged HTML: build/reports/jacoco/aggregated/html/index.html
// Merged XML (for CI/Codecov): build/reports/jacoco/aggregated/jacoco.xml
// ---------------------------------------------------------------------------
val coveredModules = listOf(
    ":core:common", ":core:data",
    ":feature:home", ":feature:restaurants", ":feature:orders",
    ":feature:favorites", ":feature:account", ":feature:onboarding"
)

// What's excluded and why:
//  - DI / generated Hilt-Dagger plumbing, R/BuildConfig/Manifest
//  - Generated Moshi adapters (*JsonAdapter) and the DTO model package (mapped, not logic)
//  - Compose UI (screens, previews, ComposableSingletons, theme) — exercised by
//    instrumentation/UI tests, not unit tests, so counting it here just hides the
//    real unit-test coverage of the ViewModel / use-case / repository layers.
val jacocoExcludes = listOf(
    "**/R.class", "**/R\$*.class", "**/BuildConfig.*", "**/Manifest*.*",
    "**/*_Hilt*.*", "**/hilt_aggregated_deps/**", "**/dagger/**", "**/Dagger*.*",
    "**/*_Factory.*", "**/*_MembersInjector.*", "**/*Module.*", "**/*Module\$*.*",
    "**/*_Impl*.*", "**/di/**", "**/ComposableSingletons*", "**/*\$\$serializer.*",
    "**/*JsonAdapter.*", "**/data/model/**",
    "**/*Screen.*", "**/*ScreenKt.*", "**/*Preview*", "**/ui/theme/**",
    "**/ui/location/**", "**/ui/checkout/**", "**/util/ui/**", "**/*Kt\$*.*",
    "**/domain/model/**", "**/*Provider*", "**/*ToLocalizedString*",
    "**/*Ui.class", "**/*Dimensions*",
    "**/*\$*\$*"
)

tasks.register<JacocoReport>("jacocoAggregatedReport") {
    group = "verification"
    description = "Merged unit-test coverage report across all modules."

    val modules = coveredModules.map { project(it) }
    dependsOn(modules.map { m ->
        m.tasks.matching { it.name == "test" || it.name == "testDebugUnitTest" }
    })

    classDirectories.setFrom(modules.map { m ->
        m.fileTree(m.layout.buildDirectory) {
            include("tmp/kotlin-classes/debug/**", "classes/kotlin/main/**")
            exclude(jacocoExcludes)
        }
    })
    sourceDirectories.setFrom(modules.flatMap { m ->
        listOf("src/main/java", "src/main/kotlin").map { m.layout.projectDirectory.dir(it) }
    })
    executionData.setFrom(modules.map { m ->
        m.fileTree(m.layout.buildDirectory) {
            include("jacoco/*.exec", "outputs/unit_test_code_coverage/**/*.exec")
        }
    })

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
        xml.outputLocation.set(layout.buildDirectory.file("reports/jacoco/aggregated/jacoco.xml"))
        html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/aggregated/html"))
    }
}