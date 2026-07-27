plugins {
    id("tastybuds.android.feature")
}

android {
    namespace = "com.app.tastybuds.feature.account"
}

// NOTE: `libs.*` accessors are not resolvable here — see :core:common/build.gradle.kts.
dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:ui"))
    implementation(project(":core:data"))

    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.github.bumptech.glide:compose:1.0.0-alpha.2")
    kapt("com.github.bumptech.glide:compiler:4.16.0")

    implementation("com.google.accompanist:accompanist-swiperefresh:0.32.0")
}
