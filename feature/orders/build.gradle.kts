plugins {
    id("tastybuds.android.feature")
}

android {
    namespace = "com.app.tastybuds.feature.orders"
}

// NOTE: `libs.*` accessors are not resolvable here — see :core:common/build.gradle.kts.
dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:ui"))
    implementation(project(":core:data"))

    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.github.bumptech.glide:compose:1.0.0-alpha.2")
    kapt("com.github.bumptech.glide:compiler:4.16.0")

    // Order tracking map + delivery-address location picker (absorbed from checkout/location/inbox)
    implementation("com.google.maps.android:maps-compose:4.3.3")
    implementation("com.google.android.gms:play-services-maps:19.2.0")
    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation("com.google.accompanist:accompanist-permissions:0.32.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.8.0")
}
