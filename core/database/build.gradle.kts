plugins {
    alias(libs.plugins.onelist.android.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.onelist.android.koin)
}

android {
    defaultConfig {
        testInstrumentationRunner = "com.lolo.io.onelist.core.testing.OneListTestRunner"
    }
    namespace = "com.lolo.io.onelist.core.database"

    testOptions {
        unitTests {
            // For Robolectric
            isIncludeAndroidResources = true
        }
    }
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

dependencies {

    // room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // json
    implementation(libs.gson)

    //implementation("com.google.guava:listenablefuture:9999.0-empty-to-avoid-conflict-with-guava")

    api(project(":core:model"))

    testImplementation(libs.robolectric)
}