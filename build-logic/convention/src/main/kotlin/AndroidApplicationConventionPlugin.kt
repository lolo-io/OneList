
import com.android.build.api.dsl.ApplicationExtension
import com.lolo.io.onelist.configureKotlinAndroid
import com.lolo.io.onelist.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
            }

            extensions.configure<ApplicationExtension> {
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = 34
               // @Suppress("UnstableApiUsage")
               // testOptions.animationsDisabled = true
            }

            dependencies {
                add("testImplementation", kotlin("test"))
                add("testImplementation", project(":core:testing"))
                add("androidTestImplementation", kotlin("test"))
                add("androidTestImplementation", project(":core:testing"))
                add("testImplementation", libs.findLibrary("koin-android-test").get())
                add("androidTestImplementation", libs.findLibrary("koin-android-test").get())
            }
        }
    }
}