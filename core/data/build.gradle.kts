import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.hilt)
    id("kotlin-kapt")
    id("jacoco")  // unit-test coverage; aggregated by :jacocoAggregatedReport
}

// App configuration read from config.properties at the repo root (with safe fallbacks),
// then surfaced to code via BuildConfig — no hardcoded URLs/timeouts in source.
val appConfig = Properties().apply {
    val configFile = rootProject.file("config.properties")
    if (configFile.exists()) FileInputStream(configFile).use { load(it) }
}
fun config(key: String, default: String): String = appConfig.getProperty(key, default)

android {
    namespace = "com.app.tastybuds.core.data"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "API_BASE_URL",
            "\"${config("API_BASE_URL", "http://10.0.2.2:8080/api/v1/")}\"")
        buildConfigField("long", "NETWORK_TIMEOUT_SECONDS",
            "${config("NETWORK_TIMEOUT_SECONDS", "30")}L")
    }

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    testOptions {
        // Repositories call android.util.Log; return defaults so JVM unit tests
        // don't blow up on the un-mocked android.jar stubs.
        unitTests.isReturnDefaultValues = true
    }
}

dependencies {
    implementation(project(":core:common"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.core)

    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    implementation(libs.retrofit)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    implementation(libs.retrofit.moshi)
    kapt(libs.moshi.kotlin.codegen)

    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore.preferences.core)
    implementation(libs.androidx.security.crypto)

    testImplementation(libs.junit)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.mockk)
}

kapt {
    correctErrorTypes = true
    arguments {
        arg("dagger.hilt.android.internal.disableAndroidSuperclassValidation", "true")
    }
}
