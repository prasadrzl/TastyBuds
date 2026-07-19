plugins {
    id("tastybuds.android.feature")
}

android {
    namespace = "com.app.tastybuds.core.ui"
}

// NOTE: `libs.*` accessors are not resolvable here — see :core:common/build.gradle.kts.
dependencies {
    implementation(project(":core:common"))
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.32.0")
}
