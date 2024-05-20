plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.test.logger) apply true
}

subprojects {
    apply(plugin = "com.adarshr.test-logger")
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}