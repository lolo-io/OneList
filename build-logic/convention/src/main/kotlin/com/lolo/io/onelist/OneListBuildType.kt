package com.lolo.io.onelist

class OneListBuildType {
    enum class NiaBuildType(
        val applicationIdSuffix: String? = null,
        val versionNameSuffix: String? = null) {
        DEBUG(".debug", "-DEBUG"),
        RELEASE,
    }
}