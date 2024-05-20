package com.lolo.io.onelist.core.database.di

import androidx.room.Room
import org.koin.dsl.module

val daosModule = module {

    single<com.lolo.io.onelist.core.database.OneListDatabase> {
        Room.databaseBuilder(
            get(),
            com.lolo.io.onelist.core.database.OneListDatabase::class.java,
            "onelist.db"
        ).build()
    }

    single<com.lolo.io.onelist.core.database.dao.ItemListDao> {
        val database = get<com.lolo.io.onelist.core.database.OneListDatabase>()
        database.itemListDao
    }
}