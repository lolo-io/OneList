import android.util.Log

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

fun <T> List<T>.ifNotEmpty(block: (List<T>) -> Unit): List<T> {
    takeIf { it.isNotEmpty() }?.let { block.invoke(this) }
    return this
}

fun logD(message: String) {
    Log.d("1LogD", message)
}