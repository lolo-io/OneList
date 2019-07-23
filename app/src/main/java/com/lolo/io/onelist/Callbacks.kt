package com.lolo.io.onelist

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
