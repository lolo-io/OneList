plugins {
    alias(libs.plugins.onelist.android.feature)
    alias(libs.plugins.onelist.android.library.compose)
    alias(libs.plugins.onelist.android.feature.koin)
}

android {
    defaultConfig {
        testInstrumentationRunner = "com.lolo.io.onelist.core.testing.OneListTestRunner"
    }
    namespace = "com.lolo.io.onelist.feature.settings"

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Inflate SettingsFragment
    implementation (libs.androidx.compose.ui.viewbinding)

    implementation(libs.androidx.preference.ktx)
    implementation (libs.storage)
    implementation (libs.fragment.testing)

    implementation(project(":core:common"))
    implementation(project(":core:domain"))
}