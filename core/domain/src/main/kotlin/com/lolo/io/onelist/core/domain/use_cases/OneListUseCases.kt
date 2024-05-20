package com.lolo.io.onelist.core.domain.use_cases


interface OneListUseCases {
    val createList: CreateList
    val importList: ImportList
    val clearList: ClearList
    val setItemsOfList: SetItemsOfList
    val reorderLists: ReorderLists
    val addItemToList: AddItemToList
    val editItemOfList: EditItemOfList
    val removeItemFromList: RemoveItemFromList
    val switchItemStatus: SwitchItemStatus
    val switchItemCommentShown: SwitchItemCommentShown
    val saveListToDb: SaveListToDb
    val loadAllLists: LoadAllLists
    val getAllLists: GetAllLists
    val setBackupUri: SetBackupUri
    val removeList: RemoveList
    val handleFirstLaunch: HandleFirstLaunch
    val syncAllLists: SyncAllLists
    val shouldShowWhatsNew: ShouldShowWhatsNew
    val selectList: SelectList
}

data class OneListUseCasesImpl(
    override val createList: CreateList,
    override val importList: ImportList,
    override val clearList: ClearList,
    override val setItemsOfList: SetItemsOfList,
    override val reorderLists: ReorderLists,
    override val addItemToList: AddItemToList,
    override val editItemOfList: EditItemOfList,
    override val removeItemFromList: RemoveItemFromList,
    override val switchItemStatus: SwitchItemStatus,
    override val switchItemCommentShown: SwitchItemCommentShown,
    override val saveListToDb: SaveListToDb,
    override val loadAllLists: LoadAllLists,
    override val getAllLists: GetAllLists,
    override val setBackupUri: SetBackupUri,
    override val removeList: RemoveList,
    override val handleFirstLaunch: HandleFirstLaunch,
    override val syncAllLists: SyncAllLists,
    override val shouldShowWhatsNew: ShouldShowWhatsNew,
    override val selectList: SelectList,
) : OneListUseCases