plugins {
    alias(libs.plugins.onelist.android.library)
    alias(libs.plugins.onelist.android.library.compose)
    alias(libs.plugins.onelist.android.koin)
}

android {
    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    namespace = "com.lolo.io.onelist.core.testing"
}

dependencies {
    api(libs.androidx.activity.compose)
    api(libs.androidx.compose.ui.test.junit4)
    api(libs.androidx.test.core)
    api(libs.androidx.espresso.core)
    api(libs.androidx.test.rules)
    api(libs.androidx.test.runner)
    api(libs.junit4)
    api(libs.kotlinx.coroutines.test)

    debugApi(libs.androidx.compose.ui.test.manifest)

    implementation(project(":core:domain"))
}