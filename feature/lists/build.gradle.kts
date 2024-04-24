plugins {
    alias(libs.plugins.onelist.android.feature)
    alias(libs.plugins.onelist.android.library.compose)
}

android {
    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    namespace = "com.lolo.io.onelist.feature.lists"
}

dependencies {

    // koin di
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.navigation)
    implementation(libs.koin.androidx.compose)

    // Compose
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.extensions)
    implementation(libs.androidx.activity.compose)
    implementation (libs.androidx.lifecycle.runtime.compose)


    // Libs
    implementation(libs.reorderable)
    implementation(libs.lazylist.hijacker)

    implementation(project(":core:common"))
    implementation(project(":core:domain"))
}