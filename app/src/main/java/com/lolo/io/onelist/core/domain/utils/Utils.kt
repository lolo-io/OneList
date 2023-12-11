fun String?.isNotNullOrEmpty(): Boolean {
    return this?.isNotEmpty() == true
}

fun <T> List<T>.updateOne(newItem: T, condition: (T) -> Boolean): List<T> {
    return this.map {
        if (condition(it)) {
            newItem
        } else it
    }
}