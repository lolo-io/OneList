package com.lolo.io.onelist.core.domain.use_cases

data class OneListUseCases(
    val createList: CreateList,
    val importList: ImportList,
    val moveList: MoveList,
    val clearList: ClearList,
    val setItemsOfList: SetItemOfList,
    val reorderLists: ReorderLists,
    val addItemToList: AddItemToList,
    val editItemOfList: EditItemOfList,
    val removeItemFromList: RemoveItemFromList,
    val switchItemStatus: SwitchItemStatus,
    val switchItemCommentShown: SwitchItemCommentShown,
    val saveListToDb: SaveListToDb,
    val loadAllLists: LoadAllLists,
    val getAllLists: GetAllLists,
    val setBackupUri: SetBackupUri,
    val removeList: RemoveList,
    val handleFirstLaunch: HandleFirstLaunch,
    val syncAllLists: SyncAllLists,
    val shouldShowWhatsNew: ShouldShowWhatsNew,
    val selectList: SelectList,
)