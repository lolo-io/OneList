package com.lolo.io.onelist

import com.lolo.io.onelist.model.Item
import com.lolo.io.onelist.model.ItemList

interface ItemsCallbacks {
    fun onRemoveItem(item: Item)
    fun onEditItem(item: Item)
    fun onMoveItem(fromPosition: Int, toPosition: Int)
    fun onSwitchItemStatus(item: Item)
    fun onShowOrHideComment(item: Item)
}

interface ListsCallbacks {
    fun onSelectList(itemList: ItemList)
    fun onListMoved(fromPosition: Int, toPosition: Int)
    fun onListAdapterStartDrag()
}
