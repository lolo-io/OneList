package com.lolo.io.onelist.feature.lists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lolo.io.onelist.core.data.model.ErrorLoadingList
import com.lolo.io.onelist.core.data.shared_preferences.SharedPreferencesHelper
import com.lolo.io.onelist.core.domain.use_cases.OneListUseCases
import com.lolo.io.onelist.core.model.Item
import com.lolo.io.onelist.core.model.ItemList
import com.lolo.io.onelist.core.ui.util.UIString
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.IOException

class ListScreenViewModel(
    private val useCases: OneListUseCases,
    preferences: SharedPreferencesHelper
) : ViewModel() {

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val allListsWithErrors = useCases.getAllLists()

    val allLists = allListsWithErrors.map {
        it.lists
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(3000), listOf())


    private val _displayedItems = MutableStateFlow(listOf<Item>())
    val displayedItems
        get() = _displayedItems.asStateFlow()


    private val selectedListIndex =
        preferences.selectedListIndexStateFlow

    var selectedList = combine(allLists, selectedListIndex) { pAllLists, pIndex ->
        (pAllLists.getOrNull(pIndex)).also {
            _displayedItems.value = it?.items ?: listOf()
        }
    }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000),
        null
    )


    private val _errorMessage = MutableStateFlow<UIString?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    init {
        allListsWithErrors.onEach {
            _errorMessage.value = if (it.errors.isNotEmpty()) {
                getErrorMessageWhenLoadingLists(it.errors)
            } else null
        }.launchIn(viewModelScope)
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

    fun resetError() {
        _errorMessage.value = null
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
            selectedList.value?.let {
                useCases.reorderLists(lists, it)
            }
        }
    }

    fun deleteList(
        itemList: ItemList,
        deleteBackupFile: Boolean,
        onFileDeleted: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                useCases.removeList(itemList, deleteBackupFile, onFileDeleted)
            } catch (e: IOException) {
                if (deleteBackupFile) {
                    _errorMessage.value = UIString
                        .StringResources(
                            R.string.error_deleting_file
                        )
                }
            }
        }
    }

    fun clearList(list: ItemList) {
        viewModelScope.launch {
            _displayedItems.value = useCases.clearList(list).items
        }
    }

    fun onSelectedListReordered(items: List<Item>) {
        viewModelScope.launch {
            selectedList.value?.let {
                useCases.setItemsOfList(it, items)
            }
        }
    }

    private suspend fun getAllLists(showRefreshIndicator: Boolean = true) {
        _isRefreshing.value = showRefreshIndicator
        useCases.loadAllLists().onEach {
            _isRefreshing.value = false
        }.launchIn(viewModelScope)
    }


    // ITEMS
    fun switchItemStatus(item: Item) {
        viewModelScope.launch {
            selectedList.value?.let {
                _displayedItems.value = useCases.switchItemStatus(it, item).items
            }
        }
    }

    fun switchItemCommentShown(item: Item) {
        viewModelScope.launch {
            selectedList.value?.let {
                _displayedItems.value =
                    useCases.switchItemCommentShown(it, item).items
            }
        }
    }

    fun addItem(item: Item) {
        viewModelScope.launch {
            selectedList.value?.let {
                _displayedItems.value = useCases.addItemToList(it, item).items
            }
        }
    }

    fun createListThenAddItem(itemList: ItemList, item: Item) {
        viewModelScope.launch {
            useCases.createList(itemList)
            selectedList.value?.let {
                _displayedItems.value = useCases.addItemToList(it, item).items
            }
        }
    }

    fun removeItem(item: Item) {
        viewModelScope.launch {
            selectedList.value?.let {
                _displayedItems.value = useCases.removeItemFromList(it, item).items
            }
        }
    }

    fun editItem(item: Item) {
        viewModelScope.launch {
            selectedList.value?.let {
                _displayedItems.value = useCases.editItemOfList(it, item).items
            }
        }
    }
}