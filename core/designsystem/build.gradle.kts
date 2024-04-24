plugins {
    alias(libs.plugins.onelist.android.library)
    alias(libs.plugins.onelist.android.library.compose)
}

android {
    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    namespace = "com.lolo.io.onelist.core.designsystem"
}


dependencies {
    // compose
    api(libs.androidx.compose.material3)
    api(libs.androidx.activity.compose)
    api(libs.androidx.lifecycle.viewmodel.compose)
    api (libs.androidx.navigation.compose)
    api (libs.androidx.compose.runtime)

    // compose: ui tests
    api(libs.androidx.compose.ui.test.junit4)
    api (libs.androidx.lifecycle.runtime.compose)
    api(libs.androidx.compose.ui.test.manifest)

    debugApi(libs.androidx.compose.ui.tooling)
}