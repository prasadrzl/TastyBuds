plugins {
    id("tastybuds.jvm.library")
}

// NOTE: the `libs` version-catalog type-safe accessor is not resolvable in build scripts
// that apply a plugin id sourced from the `build-logic` included build (a known Gradle
// limitation), so dependencies here use raw coordinates. Versions are kept in sync with
// gradle/libs.versions.toml (kotlinxCoroutinesCore, junit5, mockk, coroutinesTest, turbine).
dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")

    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
    testImplementation("io.mockk:mockk:1.13.10")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0")
    testImplementation("app.cash.turbine:turbine:1.1.0")
}
