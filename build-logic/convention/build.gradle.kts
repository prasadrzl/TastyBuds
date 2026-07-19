plugins {
    `kotlin-dsl`
}

group = "com.app.tastybuds.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.hilt.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidFeature") {
            id = "tastybuds.android.feature"
            implementationClass = "TastyBudsAndroidFeatureConventionPlugin"
        }
        register("jvmLibrary") {
            id = "tastybuds.jvm.library"
            implementationClass = "TastyBudsJvmLibraryConventionPlugin"
        }
    }
}
