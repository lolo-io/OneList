package com.lolo.io.onelist.feature.lists.lists_adapters

import com.lolo.io.onelist.core.model.Item

interface ItemsCallbacks {
    fun onRemoveItem(item: Item)
    fun openEditItemDialog(index: Int)
    fun onMoveItem(fromPosition: Int, toPosition: Int)
    fun onSwitchItemStatus(item: Item)
    fun onShowOrHideComment(item: Item)
}

interface ListsCallbacks {
    fun onSelectList(position: Int)
    fun onListMoved(fromPosition: Int, toPosition: Int)
    fun onListAdapterStartDrag()
}
