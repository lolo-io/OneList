package com.lolo.io.onelist.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.lolo.io.onelist.core.database.model.ItemListEntity
import com.lolo.io.onelist.core.model.ItemList

@Dao
interface ItemListDao {
    @Upsert
    fun upsert(itemList: ItemListEntity): Long

    @Delete
    fun delete(itemList: ItemListEntity)

    @Transaction
    @Query("SELECT * FROM itemList WHERE id = :id")
    fun get(id: Long): ItemListEntity

    @Transaction
    @Query("SELECT * FROM itemList ORDER BY position ASC")
    fun getAll(): List<ItemListEntity>
}