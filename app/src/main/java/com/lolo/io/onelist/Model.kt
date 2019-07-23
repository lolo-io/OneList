package com.lolo.io.onelist

data class Item (var title: String = "", var comment: String = "", var done: Boolean = false, var commentDisplayed: Boolean = false, val stableId : Long = System.currentTimeMillis())
data class ItemList(var title: String = "", val items: MutableList<Item> = arrayListOf(), val stableId : Long = System.currentTimeMillis())