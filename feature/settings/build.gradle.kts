plugins {
    alias(libs.plugins.onelist.android.feature)
    alias(libs.plugins.onelist.android.library.compose)
}

android {
    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    namespace = "com.lolo.io.onelist.feature.settings"

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.preference.ktx)

    // koin di
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.navigation)
    implementation(libs.koin.androidx.compose)

    // Inflate SettingsFragment
    implementation (libs.androidx.compose.ui.viewbinding)

    // other libs
    implementation (libs.storage)

    implementation(project(":core:common"))
    implementation(project(":core:domain"))
}