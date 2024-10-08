
// FDroid uses applicationId (com.lolo.io.onelist) as repo dire name & gradle doesn't like it
// this fixes build on FDroid by replacing the dots by dashes
rootProject.name="com-lolo-io-onelist"

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(":app")
include(":core:designsystem")
include(":core:model")
include(":core:database")
include(":core:data")
include(":core:domain")
include(":core:common")
include(":core:testing")
include(":feature:lists")
include(":feature:settings")
include(":feature:whatsnew")
