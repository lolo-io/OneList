package com.lolo.io.onelist.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity (
    tableName = "item"
)
data class ItemEntity(
     val title: String = "",
     val comment: String = "",
     val done: Boolean = false,
     val commentDisplayed: Boolean = false,

    @PrimaryKey(autoGenerate = true)
     val id: Long = 0L,

    )