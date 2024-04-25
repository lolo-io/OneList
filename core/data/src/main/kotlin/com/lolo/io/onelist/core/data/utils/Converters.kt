package com.lolo.io.onelist.core.data.utils

import com.lolo.io.onelist.core.database.model.ItemEntity
import com.lolo.io.onelist.core.database.model.ItemListEntity
import com.lolo.io.onelist.core.model.Item
import com.lolo.io.onelist.core.model.ItemList

fun com.lolo.io.onelist.core.model.ItemList.toItemListEntity() =
    com.lolo.io.onelist.core.database.model.ItemListEntity(
        id = this.id,
        items = this.items.toItemEntities().toMutableList(),
        uri = this.uri,
        position = this.position,
        title = this.title
    )

fun com.lolo.io.onelist.core.model.Item.toItemEntity() =
    com.lolo.io.onelist.core.database.model.ItemEntity(
        id = id,
        title = title,
        comment = comment,
        done = done,
        commentDisplayed = commentDisplayed,
    )

fun List<com.lolo.io.onelist.core.model.Item>.toItemEntities() = map { it.toItemEntity() }