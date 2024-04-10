package com.lolo.io.onelist.feature.lists

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lolo.io.onelist.BuildConfig
import com.lolo.io.onelist.R
import com.lolo.io.onelist.core.data.model.AllListsWithErrors
import com.lolo.io.onelist.core.data.model.ErrorLoadingList
import com.lolo.io.onelist.core.data.shared_preferences.SharedPreferencesHelper
import com.lolo.io.onelist.core.domain.use_cases.OneListUseCases
import com.lolo.io.onelist.core.model.Item
import com.lolo.io.onelist.core.model.ItemList
import com.lolo.io.onelist.core.ui.util.UIString
import com.lolo.io.onelist.feature.lists.tuto.FirstLaunchLists
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class OneListScreenViewModel(
    private val firstLaunchLists: FirstLaunchLists,
    private val useCases: OneListUseCases,
    private val preferences: SharedPreferencesHelper
) : ViewModel() {

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val allListsWithErrors = MutableStateFlow(AllListsWithErrors())

    val allLists = allListsWithErrors.map {
        it.lists
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(3000), listOf())


    private val _displayedItems = MutableStateFlow(listOf<Item>())
    val displayedItems
        get() = _displayedItems.asStateFlow()


    private val selectedListIndex =
        preferences.selectedListIndexStateFlow

    var selectedList = combine(allLists, selectedListIndex) { pAllLists, pIndex ->
        (pAllLists.getOrNull(pIndex) ?: ItemList()).also {
            _displayedItems.value = it.items
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ItemList())


    val errorMessage = allListsWithErrors.map {
        getErrorMessageWhenLoadingLists(it.errors)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(3000), null)

    private val _showWhatsNew = MutableStateFlow(false)
    val showWhatsNew = _showWhatsNew.asStateFlow()

    init {
        refresh(showRefreshIndicator = false)
    }

    suspend fun init() {
        useCases.handleFirstLaunch(firstLaunchLists.firstLaunchLists())
        setAppVersion()
    }


    private fun setAppVersion() {
        if (preferences.version != BuildConfig.VERSION_NAME) {
            //   _showWhatsNew.value = useCases.showWhatsNew() // no whatsNew for this version
            preferences.version = BuildConfig.VERSION_NAME
        }
    }

    private fun getErrorMessageWhenLoadingLists(errors: List<ErrorLoadingList>): UIString? {
        return if (errors.isNotEmpty()) {
            UIString
                .StringResources(
                    R.string.error_could_not_fetch_some_lists,
                    *errors.map { err ->
                        when (err) {
                            ErrorLoadingList.FileCorruptedError -> R.string.error_file_corrupted
                            ErrorLoadingList.FileMissingError -> R.string.error_file_missing
                            ErrorLoadingList.PermissionDeniedError -> R.string.error_permission_not_granted
                            ErrorLoadingList.UnknownError -> R.string.error_unknown
                        }
                    }.toIntArray()
                )
        } else null
    }


    fun refresh(showRefreshIndicator: Boolean = true) {
        viewModelScope.launch {
            getAllLists(showRefreshIndicator)
        }
    }

    // LISTS

    fun createList(itemList: ItemList) {
        viewModelScope.launch {
            useCases.createList(itemList)
        }
    }

    fun editList(itemList: ItemList) {
        viewModelScope.launch {
            useCases.saveListToDb(itemList)
        }
    }

    fun selectList(itemList: ItemList) {
        useCases.selectList(itemList)
    }

    fun reorderLists(lists: List<ItemList>) {
        viewModelScope.launch {
            useCases.reorderLists(lists, selectedList.value)
        }
    }

    suspend fun importList(uri: Uri): ItemList {
        return useCases.importList(uri)
    }

    fun deleteList(
        itemList: ItemList,
        deleteBackupFile: Boolean,
        onFileDeleted: () -> Unit
    ) {
        viewModelScope.launch {
            useCases.removeList(itemList, deleteBackupFile, onFileDeleted)
        }
    }


    fun clearList(list: ItemList) {
        viewModelScope.launch {
            _displayedItems.value = useCases.clearList(list).items
        }
    }

    fun onSelectedListReordered(items: List<Item>) {
        viewModelScope.launch {
            useCases.setItemsOfList(selectedList.value, items)
        }
    }

    private suspend fun getAllLists(showRefreshIndicator: Boolean = true) {
        _isRefreshing.value = showRefreshIndicator
        useCases.getAllLists().onEach {
            allListsWithErrors.value = it
            _isRefreshing.value = false
        }.launchIn(viewModelScope)
    }


    // ITEMS


    fun switchItemStatus(item: Item) {
        viewModelScope.launch {
            _displayedItems.value = useCases.switchItemStatus(selectedList.value, item).items
        }
    }

    fun switchItemCommentShown(item: Item) {
        viewModelScope.launch {
            _displayedItems.value = useCases.switchItemCommentShown(selectedList.value, item).items
        }
    }

    fun addItem(item: Item) {
        viewModelScope.launch {
            _displayedItems.value = useCases.addItemToList(selectedList.value, item).items
        }
    }

    fun removeItem(item: Item) {
        viewModelScope.launch {
            _displayedItems.value = useCases.removeItemFromList(selectedList.value, item).items
        }
    }

    fun editItem(item: Item) {
        viewModelScope.launch {
            _displayedItems.value = useCases.editItemOfList(selectedList.value, item).items
        }
        editList(selectedList.value.copy())
    }

}