plugins {
    id("tastybuds.jvm.library")
    id("kotlin-kapt")
}

// NOTE: the `libs` version-catalog type-safe accessor is not resolvable in build scripts
// that apply a plugin id sourced from the `build-logic` included build (a known Gradle
// limitation), so dependencies here use raw coordinates. Versions are kept in sync with
// gradle/libs.versions.toml (kotlinxCoroutinesCore, junit5, mockk, coroutinesTest, turbine).
// ... (imports or whatever)
dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")

    // UseCase classes here use @Inject constructor(...) (Hilt wires them from :app),
    // which needs the JSR-330 annotations on the classpath. Android modules get this
    // transitively via Dagger/Hilt; this plain JVM module needs it declared directly.
    implementation("javax.inject:javax.inject:1")

    // Needed because several repository interfaces here still return raw API DTOs
    // (data/model/*) instead of domain models — a pre-existing layering issue, not
    // something this migration introduces. See migration notes: repo interfaces for
    // Favorites/Order/RestaurantDetails/Search leak Moshi-annotated response types.
    implementation(libs.findLibrary("moshi").get())
    implementation(libs.findLibrary("moshi-kotlin").get())
    kapt(libs.findLibrary("moshi-kotlin-codegen").get())
    kapt(libs.findLibrary("moshi").get())

    testImplementation(libs.findLibrary("junit-jupiter").get())
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
    testImplementation("io.mockk:mockk:1.13.10")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0")
    testImplementation("app.cash.turbine:turbine:1.1.0")
}
