import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

private val javaVersion = JavaVersion.VERSION_17

java {
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = javaVersion.toString()
    }
}


dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
}


gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "onelist.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }

        register("androidApplicationCompose") {
            id = "onelist.android.application.compose"
            implementationClass = "AndroidApplicationComposeConventionPlugin"
        }

        register("androidFeature") {
            id = "onelist.android.feature"
            implementationClass = "AndroidFeatureConventionPlugin"
        }

        register("androidLibrary") {
            id = "onelist.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }

        register("androidLibraryCompose") {
            id = "onelist.android.library.compose"
            implementationClass = "AndroidLibraryComposeConventionPlugin"
        }

        register("androidKoin") {
            id = "onelist.android.koin"
            implementationClass = "AndroidKoinConventionPlugin"
        }

        register("androidFeatureKoin") {
            id = "onelist.android.feature.koin"
            implementationClass = "AndroidFeatureKoinConventionPlugin"
        }

    }
}