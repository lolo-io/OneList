package com.lolo.io.onelist.core.data.di

import androidx.room.Room
import com.lolo.io.onelist.core.data.file_access.FileAccess
import com.lolo.io.onelist.core.data.reporitory.OneListRepository
import com.lolo.io.onelist.core.data.shared_preferences.SharedPreferencesHelper
import com.lolo.io.onelist.core.database.OneListDatabase
import com.lolo.io.onelist.core.database.dao.ItemListDao
import com.lolo.io.onelist.core.domain.use_cases.CreateList
import com.lolo.io.onelist.core.domain.use_cases.EditList
import com.lolo.io.onelist.core.domain.use_cases.GetAllLists
import com.lolo.io.onelist.core.domain.use_cases.HandleFirstLaunch
import com.lolo.io.onelist.core.domain.use_cases.ImportList
import com.lolo.io.onelist.core.domain.use_cases.MoveList
import com.lolo.io.onelist.core.domain.use_cases.OneListUseCases
import com.lolo.io.onelist.core.domain.use_cases.RemoveList
import com.lolo.io.onelist.core.domain.use_cases.SetBackupUri
import com.lolo.io.onelist.core.domain.use_cases.ShowWhatsNew
import com.lolo.io.onelist.core.domain.use_cases.SyncAllLists
import org.koin.dsl.module

val appModule = module {

    single {
        SharedPreferencesHelper(get())
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
            createList = CreateList(get()),
            getAllLists = GetAllLists(
                (get())
            ),
            removeList = RemoveList((get())),
            handleFirstLaunch = HandleFirstLaunch(get(), get()),
            editList = EditList(get()),
            importList = ImportList(get()),
            moveList = MoveList(get()),
            setBackupUri = SetBackupUri(get()),
            syncAllLists = SyncAllLists(get()),
            showWhatsNew = ShowWhatsNew(get())
        )
    }

    single {
        OneListRepository(get(), get(), get())
    }

    single {
        FileAccess(get())
    }

    single<ItemListDao> {
        val database = get<OneListDatabase>()
        database.itemListDao
    }

}

