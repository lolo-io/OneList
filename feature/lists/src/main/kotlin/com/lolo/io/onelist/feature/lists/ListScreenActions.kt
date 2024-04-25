package com.lolo.io.onelist.feature.lists

import com.lolo.io.onelist.core.model.Item
import com.lolo.io.onelist.core.model.ItemList

interface ListScreenActions {
    fun selectList(list: ItemList)
    fun reorderLists(lists: List<ItemList>)
    fun addItem(item: Item)
    fun switchItemStatus(item: Item)
    fun removeItem(item: Item): Any
    fun switchItemCommentShown(item: Item)
    fun onSelectedListReordered(items: List<Item>)
    fun refresh()
    fun createList(list: ItemList)
    fun editList(list: ItemList)
    fun editItem(item: Item)
    fun deleteList(list: ItemList, deleteBackupFile: Boolean, onFileDeleted: () -> Unit)
    fun clearList(list: ItemList)
}