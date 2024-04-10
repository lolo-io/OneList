package com.lolo.io.onelist.feature.lists

import android.content.Context
import android.content.Intent
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

class OneListFragmentViewModel(
    private val firstLaunchLists: FirstLaunchLists,
    private val useCases: OneListUseCases,
    private val preferences: SharedPreferencesHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(UIState())
    val uiState = _uiState.asStateFlow()


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



    suspend fun init() {
        if (useCases.handleFirstLaunch(firstLaunchLists.firstLaunchLists())) {
            refreshAllLists()
        }

        setAppVersion()
    }

    private fun updateUiState(block: UIState.() -> UIState) {
        _uiState.value = block(_uiState.value).apply { }
    }

    suspend fun createListFragment(itemList: ItemList) {
        useCases.createList(itemList)
    }

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

    suspend fun removeList(
        itemList: ItemList,
        deleteBackupFile: Boolean,
        onFileDeleted: () -> Unit
    ) {
        useCases.removeList(itemList, deleteBackupFile, onFileDeleted)
    }


    fun editItem(index: Int, item: Item) {
        //    selectedList.value.items[index] = item
        editList(selectedList.value.copy())
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

    fun resetError() {
        allListsWithErrors.value = allListsWithErrors.value.copy(errors = listOf())
    }


    fun whatsNewShown() {
        _showWhatsNew.value = false
    }


    /***
     *
     *
     *    ALMOST CLEAN
     *
     *
     */


    private suspend fun getAllLists() {
        updateUiState { copy(isRefreshing = true) }
        useCases.getAllLists().onEach {
            allListsWithErrors.value = it
            updateUiState { copy(isRefreshing = false) }
        }.launchIn(viewModelScope)
    }

    fun refresh() {
        viewModelScope.launch {
            getAllLists()
        }
    }

    // LISTS

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

    fun clearSelectedList() {
        viewModelScope.launch {
            _displayedItems.value = useCases.clearList(selectedList.value).items
        }
    }

    fun onSelectedListReordered(items: List<Item>) {
        viewModelScope.launch {
            useCases.setItemsOfList(selectedList.value, items)
        }
    }

    /***
     *
     *
     *    TRASH
     *
     *
     */

    fun clearComment() {
        updateUiState { copy(addCommentText = "") }
    }

    fun moveItem(fromPosition: Int, toPosition: Int) {
        val fromItem = selectedList.value.items[fromPosition]
        //    selectedList.value.items.removeAt(fromPosition)
        //   selectedList.value.items.add(toPosition, fromItem)
        editList(selectedList.value.copy())
    }

    fun setAddItemText(text: String) {
        updateUiState {
            copy(
                addCommentText = "",
                showValidate = text.isNotEmpty(),
                showAddCommentArrow = text.isNotEmpty()
            )
        }
    }

    fun setAddItemComment(text: String) {
        updateUiState {
            copy(
                addCommentText = text,
                showButtonClearComment = text.isNotEmpty()
            )
        }
    }


    fun moveList(fromPosition: Int, toPosition: Int) {
        viewModelScope.launch {
            useCases.moveList(fromPosition, toPosition, allLists.value)
        }
    }

    fun selectList(position: Int) {
        preferences.selectedListIndex = position
        _displayedItems.value = selectedList.value.items
    }

    fun refreshAllLists() {
        updateUiState { copy(isRefreshing = true) }
        viewModelScope.launch {
            getAllLists()
            _forceRefreshTrigger.value++
        }
    }


    private val _forceRefreshTrigger = MutableStateFlow(0)
    val forceRefreshTrigger = _forceRefreshTrigger.asStateFlow()
}