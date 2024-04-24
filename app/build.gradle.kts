import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.onelist.android.application)
    alias(libs.plugins.onelist.android.application.compose)
    alias(libs.plugins.onelist.android.koin)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
}
android {
    namespace = "com.lolo.io.onelist"

    val versionPropsFile = file("../version.properties")
    var versionCodeCI: Int? = null
    if (versionPropsFile.canRead()) {
        val versionProps = Properties()
        versionProps.load(FileInputStream(versionPropsFile))
        versionCodeCI = (versionProps["VERSION_CODE"] as String).toInt()
    }

    defaultConfig {
        multiDexEnabled = true
        applicationId = "com.lolo.io.onelist"
        versionCode = versionCodeCI ?: 19
        versionName = "1.5.0"
        vectorDrawables.useSupportLibrary = true
    }

    androidResources {
        generateLocaleConfig = true
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    signingConfigs {
        create("release") {
            storeFile = file("keystore.jks")
            storePassword = System.getenv("ONELIST_KEYSTORE_PASSWORD")
            keyAlias = System.getenv("ONELIST_KEYSTORE_ALIAS")
            keyPassword = System.getenv("ONELIST_KEYSTORE_ALIAS_PASSWORD")
        }
    }

    buildTypes {
        getByName("debug") {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-DEBUG"
            resValue("string", "app_name", "1ListDev")
        }
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            resValue("string", "app_name", "1List")
            signingConfig = signingConfigs.getByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

}

dependencies {
    // android
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.preference.ktx)
    implementation(libs.androidx.lifecycle.extensions)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.androidx.appcompat)

    // android - design
    implementation(libs.constraint.layout)
    implementation(libs.androidx.recyclerview)
    implementation(libs.flexbox)
    implementation(libs.material)
    implementation(libs.androidx.swiperefreshlayout)

    // kotlin
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlin.stdlib.jdk7)

    // compose
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation (libs.androidx.compose.ui.viewbinding) // To inflate SettingsFragment
    implementation (libs.androidx.navigation.compose)

    // compose: ui tests
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    implementation (libs.androidx.lifecycle.runtime.compose)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // firebase
    implementation(libs.firebase.crashlytics)

    // json
    implementation(libs.gson)

    // other libs
    implementation (libs.whatsnew)
    implementation (libs.storage)
    implementation (libs.advrecyclerview)
    implementation(libs.reorderable)
    implementation(libs.lazylist.hijacker)

    // projects
    implementation(project(":core:designsystem"))
    implementation(project(":core:ui"))
    implementation(project(":core:domain"))

    implementation(project(":feature:lists"))
    implementation(project(":feature:settings"))
    implementation(project(":feature:whatsnew"))
}
