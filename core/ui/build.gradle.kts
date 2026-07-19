plugins {
    id("tastybuds.android.feature")
}

android {
    namespace = "com.app.tastybuds.core.ui"
}

dependencies {
    implementation(project(":core:common"))
    implementation(libs.accompanist.systemuicontroller)
}
