plugins {
    alias(libs.plugins.onelist.android.feature)
    alias(libs.plugins.onelist.android.library.compose)
    alias(libs.plugins.onelist.android.feature.koin)
}

android {
    defaultConfig {
        testInstrumentationRunner = "com.lolo.io.onelist.core.testing.OneListTestRunner"
    }
    namespace = "com.lolo.io.onelist.feature.lists"
}

dependencies {

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

    androidTestImplementation(project(":core:testing"))
}