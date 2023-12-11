package com.lolo.io.onelist.core.database.dao

import com.lolo.io.onelist.core.database.model.ItemEntity

interface ItemDao {
    fun upsert(itemList: ItemEntity): Long
    fun delete(itemList: ItemEntity)
    fun get(id: Long): ItemEntity
    fun getAll(): List<ItemEntity>
}