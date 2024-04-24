plugins {
    alias(libs.plugins.onelist.android.library)
    alias(libs.plugins.onelist.android.koin)
}

android {
    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    namespace = "com.lolo.io.onelist.core.data"
}

dependencies {
    implementation(libs.androidx.preference.ktx)
    implementation (libs.storage)
    implementation(libs.gson)

    api(project(":core:database"))
}