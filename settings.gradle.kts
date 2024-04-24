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

include(":app")
include(":core:ui")
include(":core:designsystem")
include(":core:model")
include(":core:database")
include(":core:data")
include(":core:domain")
include(":core:common")
include(":feature:lists")
include(":feature:settings")
include(":feature:whatsnew")
