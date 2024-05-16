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
import com.lolo.io.onelist.core.domain.use_cases.OneListUseCases
import com.lolo.io.onelist.core.domain.use_cases.OneListUseCasesImpl
import com.lolo.io.onelist.core.domain.use_cases.RemoveItemFromList
import com.lolo.io.onelist.core.domain.use_cases.RemoveList
import com.lolo.io.onelist.core.domain.use_cases.ReorderLists
import com.lolo.io.onelist.core.domain.use_cases.SaveListToDb
import com.lolo.io.onelist.core.domain.use_cases.SelectList
import com.lolo.io.onelist.core.domain.use_cases.SetBackupUri
import com.lolo.io.onelist.core.domain.use_cases.SetItemsOfList
import com.lolo.io.onelist.core.domain.use_cases.ShouldShowWhatsNew
import com.lolo.io.onelist.core.domain.use_cases.SwitchItemCommentShown
import com.lolo.io.onelist.core.domain.use_cases.SwitchItemStatus
import com.lolo.io.onelist.core.domain.use_cases.SyncAllLists


class FakeUseCases(
    oneListRepository: OneListRepository = FakeOneListRepository(),
    preferenceHelper: SharedPreferencesHelper = FakeSharedPreferenceHelper()
) : OneListUseCases {

    val calledFunctions = mutableListOf<String>()

    private val useCasesImpl = createFakeUseCasesImpl(
        oneListRepository, preferenceHelper
    )

    override val createList: CreateList
        get() {
            calledFunctions += Thread.currentThread().stackTrace[1].methodName
            return useCasesImpl.createList
        }
    override val importList: ImportList
        get() {
            calledFunctions += Thread.currentThread().stackTrace[1].methodName
            return useCasesImpl.importList
        }
    override val clearList: ClearList
        get() {
            calledFunctions += Thread.currentThread().stackTrace[1].methodName
            return useCasesImpl.clearList
        }
    override val setItemsOfList: SetItemsOfList
        get() {
            calledFunctions += Thread.currentThread().stackTrace[1].methodName
            return useCasesImpl.setItemsOfList
        }
    override val reorderLists: ReorderLists
        get() {
            calledFunctions += Thread.currentThread().stackTrace[1].methodName
            return useCasesImpl.reorderLists
        }
    override val addItemToList: AddItemToList
        get() {
            calledFunctions += Thread.currentThread().stackTrace[1].methodName
            return useCasesImpl.addItemToList
        }
    override val editItemOfList: EditItemOfList
        get() {
            calledFunctions += Thread.currentThread().stackTrace[1].methodName
            return useCasesImpl.editItemOfList
        }
    override val removeItemFromList: RemoveItemFromList
        get() {
            calledFunctions += Thread.currentThread().stackTrace[1].methodName
            return useCasesImpl.removeItemFromList
        }
    override val switchItemStatus: SwitchItemStatus
        get() {
            calledFunctions += Thread.currentThread().stackTrace[1].methodName
            return useCasesImpl.switchItemStatus
        }
    override val switchItemCommentShown: SwitchItemCommentShown
        get() {
            calledFunctions += Thread.currentThread().stackTrace[1].methodName
            return useCasesImpl.switchItemCommentShown
        }
    override val saveListToDb: SaveListToDb
        get() {
            calledFunctions += Thread.currentThread().stackTrace[1].methodName
            return useCasesImpl.saveListToDb
        }
    override val loadAllLists: LoadAllLists
        get() {
            calledFunctions += Thread.currentThread().stackTrace[1].methodName
            return useCasesImpl.loadAllLists
        }
    override val getAllLists: GetAllLists
        get() {
            calledFunctions += Thread.currentThread().stackTrace[1].methodName
            return useCasesImpl.getAllLists
        }
    override val setBackupUri: SetBackupUri
        get() {
            calledFunctions += Thread.currentThread().stackTrace[1].methodName
            return useCasesImpl.setBackupUri
        }
    override val removeList: RemoveList
        get() {
            calledFunctions += Thread.currentThread().stackTrace[1].methodName
            return useCasesImpl.removeList
        }
    override val handleFirstLaunch: HandleFirstLaunch
        get() {
            calledFunctions += Thread.currentThread().stackTrace[1].methodName
            return useCasesImpl.handleFirstLaunch
        }
    override val syncAllLists: SyncAllLists
        get() {
            calledFunctions += Thread.currentThread().stackTrace[1].methodName
            return useCasesImpl.syncAllLists
        }
    override val shouldShowWhatsNew: ShouldShowWhatsNew
        get() {
            calledFunctions += Thread.currentThread().stackTrace[1].methodName
            return useCasesImpl.shouldShowWhatsNew
        }
    override val selectList: SelectList
        get() {
            calledFunctions += Thread.currentThread().stackTrace[1].methodName
            return useCasesImpl.selectList
        }

}


fun createFakeUseCasesImpl(
    oneListRepository: OneListRepository = FakeOneListRepository(),
    preferenceHelper: SharedPreferencesHelper = FakeSharedPreferenceHelper()
)
        : OneListUseCases {
    val saveListToDb = FakeSaveListToDb()
    return OneListUseCasesImpl(
        createList = CreateList(oneListRepository),
        loadAllLists = LoadAllLists(oneListRepository),
        getAllLists = GetAllLists(oneListRepository),
        removeList = RemoveList(oneListRepository),
        handleFirstLaunch = HandleFirstLaunch(oneListRepository, preferenceHelper),
        saveListToDb = saveListToDb,
        importList = ImportList(oneListRepository),
        setBackupUri = SetBackupUri(oneListRepository),
        syncAllLists = SyncAllLists(oneListRepository),
        shouldShowWhatsNew = ShouldShowWhatsNew(preferenceHelper),
        addItemToList = AddItemToList(saveListToDb),
        editItemOfList = EditItemOfList(saveListToDb),
        clearList = ClearList(saveListToDb),
        removeItemFromList = RemoveItemFromList(saveListToDb),
        switchItemStatus = SwitchItemStatus(saveListToDb),
        setItemsOfList = SetItemsOfList(saveListToDb),
        switchItemCommentShown = SwitchItemCommentShown(saveListToDb),
        reorderLists = ReorderLists(oneListRepository),
        selectList = SelectList(oneListRepository)
    )
}

