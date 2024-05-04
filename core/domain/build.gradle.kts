plugins {
    alias(libs.plugins.onelist.android.library)
    alias(libs.plugins.onelist.android.koin)
}

android {
    defaultConfig {
        testInstrumentationRunner = "com.lolo.io.onelist.core.testing.OneListTestRunner"
    }
    namespace = "com.lolo.io.onelist.core.domain"
}

dependencies {
    implementation(libs.gson)
    api(project(":core:data"))

    testImplementation(libs.robolectric)
}