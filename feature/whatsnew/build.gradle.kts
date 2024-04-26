plugins {
    alias(libs.plugins.onelist.android.feature)
    alias(libs.plugins.onelist.android.library.compose)
}

android {
    defaultConfig {
        testInstrumentationRunner = "com.lolo.io.onelist.core.testing.OneListTestRunner"
    }
    namespace = "com.lolo.io.onelist.feature.whatsnew"
}

dependencies {
    implementation(project(":core:common"))
}