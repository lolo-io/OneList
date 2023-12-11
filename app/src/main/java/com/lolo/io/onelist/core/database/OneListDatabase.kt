package com.lolo.io.onelist.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.lolo.io.onelist.core.database.dao.ItemListDao
import com.lolo.io.onelist.core.database.model.ItemEntity
import com.lolo.io.onelist.core.database.model.ItemListEntity
import com.lolo.io.onelist.core.database.util.Converters

@Database(
    entities = [ItemListEntity::class, ItemEntity::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class OneListDatabase: RoomDatabase() {
    abstract val itemListDao: ItemListDao
}