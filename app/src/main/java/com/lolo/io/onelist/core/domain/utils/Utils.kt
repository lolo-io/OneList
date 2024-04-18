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


fun <T>List<T>.insertAtIndex(item1: T, index: Int): List<T> {
    var listCopy = this.toList()
    listCopy -= item1
    return (listCopy.subList(
        0,
        index
    ) + item1 + listCopy.subList(
        index,
        listCopy.size
    ))
}

fun <T>List<T>.moveItemToRightOf(
    item: T,
    toItem: T
): List<T> {
    var listCopy = this.toList()
    listCopy -= item
    val toItemIndex = listCopy.indexOf(toItem)
    return listCopy.insertAtIndex(item, toItemIndex + 1)
}

fun <T>List<T>.moveItemToLeftOf(
    item: T,
    toItem: T
): List<T> {
    var listCopy = this.toList()
    listCopy -= item
    val toItemIndex = listCopy.indexOf(toItem)
    return listCopy.insertAtIndex(item, toItemIndex)
}