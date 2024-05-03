plugins {
    alias(libs.plugins.onelist.android.library)
    alias(libs.plugins.onelist.android.koin)
}

android {
    defaultConfig {
        testInstrumentationRunner = "com.lolo.io.onelist.core.testing.OneListTestRunner"
    }
    namespace = "com.lolo.io.onelist.core.data"

    testOptions {
        unitTests {
            // For Robolectric
            isIncludeAndroidResources = true
        }
    }
}


dependencies {
    implementation(libs.androidx.preference.ktx)
    implementation (libs.storage)
    implementation(libs.gson)

    api(project(":core:database"))

    testImplementation(libs.robolectric)
}