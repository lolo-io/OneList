package com.lolo.io.onelist.core.domain.use_cases

data class OneListUseCases(
    val createList: CreateList,
    val importList: ImportList,
    val moveList: MoveList,
    val clearList: ClearList,
    val setItemsOfList: SetItemOfList,
    val addItemToList: AddItemToList,
    val removeItemFromList: RemoveItemFromList,
    val switchItemStatus: SwitchItemStatus,
    val switchItemCommentShown: SwitchItemCommentShown,
    val saveListToDb: SaveListToDb,
    val getAllLists: GetAllLists,
    val setBackupUri: SetBackupUri,
    val removeList: RemoveList,
    val handleFirstLaunch: HandleFirstLaunch,
    val syncAllLists: SyncAllLists,
    val showWhatsNew: ShowWhatsNew,
)