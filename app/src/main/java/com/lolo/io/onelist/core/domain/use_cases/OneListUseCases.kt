package com.lolo.io.onelist.core.domain.use_cases

data class OneListUseCases(
    val upsertList: UpsertList,
    val getAllLists: GetAllLists,
    val removeList: RemoveList,
    val selectedListIndex: SelectedListIndex,
    val handleFirstLaunch: HandleFirstLaunch,
    val version: Version,
)