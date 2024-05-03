package com.lolo.io.onelist.core.testing.fake

import com.lolo.io.onelist.core.data.repository.OneListRepository
import com.lolo.io.onelist.core.data.shared_preferences.SharedPreferencesHelper
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
import com.lolo.io.onelist.core.domain.use_cases.ShouldShowWhatsNew
import com.lolo.io.onelist.core.domain.use_cases.SwitchItemCommentShown
import com.lolo.io.onelist.core.domain.use_cases.SwitchItemStatus
import com.lolo.io.onelist.core.domain.use_cases.SyncAllLists

fun createFakeUseCases(
    oneListRepository: OneListRepository = FakeOneListRepository(),
    preferenceHelper: SharedPreferencesHelper = FakeSharedPreferenceHelper())
        : OneListUseCases {
    val saveListToDb = SaveListToDb(oneListRepository)
    return OneListUseCases(
        createList = CreateList(oneListRepository),
        loadAllLists = LoadAllLists(oneListRepository),
        getAllLists = GetAllLists(oneListRepository),
        removeList = RemoveList(oneListRepository),
        handleFirstLaunch = HandleFirstLaunch(oneListRepository, preferenceHelper),
        saveListToDb = saveListToDb,
        importList = ImportList(oneListRepository),
        moveList = MoveList(oneListRepository),
        setBackupUri = SetBackupUri(oneListRepository),
        syncAllLists = SyncAllLists(oneListRepository),
        shouldShowWhatsNew = ShouldShowWhatsNew(preferenceHelper),
        addItemToList = AddItemToList(saveListToDb),
        editItemOfList = EditItemOfList(saveListToDb),
        clearList = ClearList(saveListToDb),
        removeItemFromList = RemoveItemFromList(saveListToDb),
        switchItemStatus = SwitchItemStatus(saveListToDb),
        setItemsOfList = SetItemOfList(saveListToDb),
        switchItemCommentShown = SwitchItemCommentShown(saveListToDb),
        reorderLists = ReorderLists(oneListRepository),
        selectList = SelectList(oneListRepository)
    )
}

