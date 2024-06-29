import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.onelist.android.application)
    alias(libs.plugins.onelist.android.application.compose)
    alias(libs.plugins.onelist.android.koin)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.firebase.crashlytics)
}
if (gradle.startParameter.taskNames.toString().contains("Release")) {
    plugins.apply("com.google.gms.google-services")
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
        // version code elvis right operand should be incremented too when publishing a new release, for fDroid build.
        versionCode = versionCodeCI ?: 21
        versionName = "1.5.1"
        vectorDrawables.useSupportLibrary = true
        testBuildType = "instrumented"

        testInstrumentationRunner =
            "com.lolo.io.onelist.core.testing.OneListTestRunner"


    }

    androidResources {
        generateLocaleConfig = true
    }

    testOptions {
        unitTests {
            // For Robolectric
            isIncludeAndroidResources = true
        }
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
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-DEBUG"
        }



        release {
            isMinifyEnabled = true
            isShrinkResources = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            resValue("string", "app_name", "1List")
            signingConfig = signingConfigs.getByName("release")
        }

        create("instrumented") {
            initWith(getByName("debug"))
            applicationIdSuffix = ".tst"
            versionNameSuffix = "-TEST"
            matchingFallbacks.add("debug")
        }
    }
}

dependencies {

    implementation(libs.androidx.core.splashscreen)
    releaseImplementation(libs.firebase.crashlytics)
    implementation(libs.storage)

    // projects
    implementation(projects.core.designsystem)
    implementation(projects.core.domain)

    implementation(projects.feature.lists)
    implementation(projects.feature.settings)
    implementation(projects.feature.whatsnew)

}
