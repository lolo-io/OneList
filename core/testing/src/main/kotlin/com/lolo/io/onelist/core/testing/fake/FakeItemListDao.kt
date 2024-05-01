package com.lolo.io.onelist.core.testing.fake

import com.lolo.io.onelist.core.data.utils.toItemListEntity
import com.lolo.io.onelist.core.database.dao.ItemListDao
import com.lolo.io.onelist.core.database.model.ItemListEntity
import com.lolo.io.onelist.core.database.util.toItemListModel
import com.lolo.io.onelist.core.model.ItemList

class FakeItemListDao : ItemListDao {

    private var listIdsIncrement = 0L
    var lists = mutableListOf<ItemList>()
    private set

    fun setLists(lists: List<ItemList>) {
        this.lists = lists.map {
            it.copy(id = ++listIdsIncrement)
        }.toMutableList()
    }

    override fun upsert(itemList: ItemListEntity): Long {
        var returnId = itemList.id
        if (lists.any { it.id == itemList.id }) {
            lists[lists.indexOfFirst {
                it.id == itemList.id
            }] = itemList.toItemListModel()
        } else {
            lists.add(itemList.toItemListModel().copy(id = ++listIdsIncrement))
            returnId = listIdsIncrement
        }
        return returnId
    }

    override fun delete(itemList: ItemListEntity) {
        lists.removeAll { it.id == itemList.id }
    }

    override fun get(id: Long): ItemListEntity {
        return lists.first { it.id == id }.toItemListEntity()
    }

    override fun getAll(): List<ItemListEntity> {
        return lists.map {
            it.toItemListEntity()
        }
    }

    fun tearDown() {
        lists.clear()
        listIdsIncrement = 0L
    }
}