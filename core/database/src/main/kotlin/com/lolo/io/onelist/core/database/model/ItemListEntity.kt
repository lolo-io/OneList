package com.lolo.io.onelist.core.database.model

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity (
    tableName = "itemList"
)
data class ItemListEntity(
    val title: String = "",
    val position: Int = 0,
    val items: List<ItemEntity> = listOf(),
    var uri: Uri? = null,

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    )


