package com.lolo.io.onelist.core.data.di

import androidx.room.Room
import com.lolo.io.onelist.core.data.persistence.PersistenceHelper
import com.lolo.io.onelist.core.database.OneListDatabase
import com.lolo.io.onelist.core.database.dao.ItemListDao
import com.lolo.io.onelist.core.domain.use_cases.GetAllLists
import com.lolo.io.onelist.core.domain.use_cases.HandleFirstLaunch
import com.lolo.io.onelist.core.domain.use_cases.OneListUseCases
import com.lolo.io.onelist.core.domain.use_cases.RemoveList
import com.lolo.io.onelist.core.domain.use_cases.SelectedListIndex
import com.lolo.io.onelist.core.domain.use_cases.UpsertList
import com.lolo.io.onelist.core.domain.use_cases.Version
import org.koin.dsl.module

val appModule = module {

    single {
        PersistenceHelper(get(), get())
    }

    single<OneListDatabase> {
        Room.databaseBuilder(
            get(),
            OneListDatabase::class.java,
            "onelist.db"
        ).build()
    }

    single {
        OneListUseCases(
            upsertList = UpsertList(get()),
            getAllLists = GetAllLists(
                (get())
            ),
            removeList = RemoveList((get())),
            selectedListIndex = SelectedListIndex((get())),
            handleFirstLaunch = HandleFirstLaunch(get()),
            version = Version(get())
        )
    }

    single<ItemListDao> {
        val database = get<OneListDatabase>()
        database.itemListDao
    }

}

