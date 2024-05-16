package com.lolo.io.onelist.core.data.updates

class Version(val name: String) {
    val major
        get() = name.substringBefore(".")
            .filter { it.isDigit() }.toIntOrNull() ?: 0
    val minor
        get() = name.substringAfter(".").substringBeforeLast(".")
            .filter { it.isDigit() }.toIntOrNull() ?: 0
    val patch
        get() = name.substringAfterLast(".")
            .filter { it.isDigit() }.toIntOrNull() ?: 0

    override fun toString(): String {
        return "$major.$minor.$patch"
    }

    override fun equals(other: Any?): Boolean {
        return this.toString() == other.toString()
    }
    override fun hashCode(): Int {
        return this.toString().hashCode()
    }
}