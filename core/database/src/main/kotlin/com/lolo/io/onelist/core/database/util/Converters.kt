package com.lolo.io.onelist.core.database.util

import android.net.Uri
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lolo.io.onelist.core.database.model.ItemEntity
import com.lolo.io.onelist.core.database.model.ItemListEntity
import com.lolo.io.onelist.core.model.Item
import com.lolo.io.onelist.core.model.ItemList

class Converters {
    @TypeConverter
    fun toItemEntityList(value: String?): List<ItemEntity> {
        val listType = object : TypeToken<MutableList<ItemEntity?>?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromItemEntityList(list: List<ItemEntity?>?): String {
        val gson = Gson()
        return gson.toJson(list)
    }


    @TypeConverter
    fun toUri(value: String?): Uri? {
        return value?.let { Uri.parse(value.toString()) }
    }

    @TypeConverter
    fun fromUri(uri: Uri?): String? {
        return uri?.toString()
    }
}

fun ItemListEntity.toItemListModel() = com.lolo.io.onelist.core.model.ItemList(
    id = this.id,
    items = this.items.toItemModels().toMutableList(),
    uri = this.uri,
    position = this.position,
    title = this.title
)

fun List<ItemListEntity>.toItemListModels() = map { it.toItemListModel() }

fun ItemEntity.toItemModel() = com.lolo.io.onelist.core.model.Item(
    id = id,
    title = title,
    comment = comment,
    done = done,
    commentDisplayed = commentDisplayed,
)

fun List<ItemEntity>.toItemModels() = map { it.toItemModel() }