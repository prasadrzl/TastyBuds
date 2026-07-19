plugins {
    id("tastybuds.android.feature")
}

android {
    namespace = "com.app.tastybuds.feature.onboarding"
}

// NOTE: `libs.*` accessors are not resolvable here — see :core:common/build.gradle.kts.
dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:ui"))
    implementation(project(":core:data"))

    implementation("androidx.datastore:datastore-preferences:1.0.0")
}
