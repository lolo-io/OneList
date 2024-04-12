package com.lolo.io.onelist.core.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DeleteColumn
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.AutoMigrationSpec
import com.lolo.io.onelist.core.database.dao.ItemListDao
import com.lolo.io.onelist.core.database.model.ItemEntity
import com.lolo.io.onelist.core.database.model.ItemListEntity
import com.lolo.io.onelist.core.database.util.Converters

@Database(
    entities = [ItemListEntity::class, ItemEntity::class],
    version = 2,
    autoMigrations = [
        AutoMigration(from = 1, to = 2, spec = OneListDatabase.Migration1To2::class)
    ]
)
@TypeConverters(Converters::class)
abstract class OneListDatabase: RoomDatabase() {
    abstract val itemListDao: ItemListDao

    @DeleteColumn("item", "stableId")
    @DeleteColumn("itemList", "path")
    class Migration1To2 : AutoMigrationSpec


}