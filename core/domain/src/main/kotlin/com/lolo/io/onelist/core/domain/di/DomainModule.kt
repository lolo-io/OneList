package com.lolo.io.onelist.core.domain.di

import com.lolo.io.onelist.core.domain.use_cases.AddItemToList
import com.lolo.io.onelist.core.domain.use_cases.ClearList
import com.lolo.io.onelist.core.domain.use_cases.CreateList
import com.lolo.io.onelist.core.domain.use_cases.EditItemOfList
import com.lolo.io.onelist.core.domain.use_cases.GetAllLists
import com.lolo.io.onelist.core.domain.use_cases.HandleFirstLaunch
import com.lolo.io.onelist.core.domain.use_cases.ImportList
import com.lolo.io.onelist.core.domain.use_cases.LoadAllLists
import com.lolo.io.onelist.core.domain.use_cases.MoveList
import com.lolo.io.onelist.core.domain.use_cases.OneListUseCases
import com.lolo.io.onelist.core.domain.use_cases.RemoveItemFromList
import com.lolo.io.onelist.core.domain.use_cases.RemoveList
import com.lolo.io.onelist.core.domain.use_cases.ReorderLists
import com.lolo.io.onelist.core.domain.use_cases.SaveListToDb
import com.lolo.io.onelist.core.domain.use_cases.SelectList
import com.lolo.io.onelist.core.domain.use_cases.SetBackupUri
import com.lolo.io.onelist.core.domain.use_cases.SetItemOfList
import com.lolo.io.onelist.core.domain.use_cases.ShowWhatsNew
import com.lolo.io.onelist.core.domain.use_cases.SwitchItemCommentShown
import com.lolo.io.onelist.core.domain.use_cases.SwitchItemStatus
import com.lolo.io.onelist.core.domain.use_cases.SyncAllLists
import org.koin.dsl.module

val domainModule = module {

    single {
        val saveListToDb = SaveListToDb(get())
        OneListUseCases(
            createList = CreateList(get()),
            loadAllLists = LoadAllLists(get()),
            getAllLists = GetAllLists(get()),
            removeList = RemoveList((get())),
            handleFirstLaunch = HandleFirstLaunch(get(), get()),
            saveListToDb = saveListToDb,
            importList = ImportList(get()),
            moveList = MoveList(get()),
            setBackupUri = SetBackupUri(get()),
            syncAllLists = SyncAllLists(get()),
            showWhatsNew = ShowWhatsNew(get()),
            addItemToList = AddItemToList(saveListToDb),
            editItemOfList = EditItemOfList(saveListToDb),
            clearList = ClearList(saveListToDb),
            removeItemFromList = RemoveItemFromList(saveListToDb),
            switchItemStatus = SwitchItemStatus(saveListToDb),
            setItemsOfList = SetItemOfList(saveListToDb),
            switchItemCommentShown = SwitchItemCommentShown(saveListToDb),
            reorderLists = ReorderLists(get()),
            selectList = SelectList(get())
        )
    }
}