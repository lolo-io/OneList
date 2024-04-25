plugins {
    alias(libs.plugins.onelist.android.feature)
    alias(libs.plugins.onelist.android.library.compose)
}

android {
    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    namespace = "com.lolo.io.onelist.feature.whatsnew"
}

dependencies {
    implementation(project(":core:common"))
}