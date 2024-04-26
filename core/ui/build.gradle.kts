plugins {
    alias(libs.plugins.onelist.android.library)
    alias(libs.plugins.onelist.android.library.compose)
}

android {
    defaultConfig {
        testInstrumentationRunner = "com.lolo.io.onelist.core.testing.OneListTestRunner"
    }
    namespace = "com.lolo.io.onelist.core.ui"
}


dependencies {
    api(project(":core:designsystem"))
}