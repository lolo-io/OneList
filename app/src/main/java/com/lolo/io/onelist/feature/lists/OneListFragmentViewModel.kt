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
import com.lolo.io.onelist.feature.lists.utils.toStringForShare
import kotlinx.coroutines.Job
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

    private val allListsResources = MutableStateFlow(AllListsWithErrors())

    private var getAllListsJob: Job? = null

    val allLists = allListsResources.map {
        _errorMessage.value = getErrorMessageWhenLoadingLists(it.errors)
        it.lists
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(3000), listOf())


    private val selectedListIndex =
        preferences.selectedListIndexStateFlow

    private val _forceRefreshTrigger = MutableStateFlow(0)
    val forceRefreshTrigger = _forceRefreshTrigger.asStateFlow()

    private val _errorMessage = MutableStateFlow<UIString?>(null)
    val errorMessage = _errorMessage.asStateFlow()


    private val _showWhatsNew = MutableStateFlow(false)
    val showWhatsNew = _showWhatsNew.asStateFlow()

    var selectedList = combine(allLists, selectedListIndex) { pAllLists, pIndex ->
        pAllLists.getOrNull(pIndex) ?: ItemList()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ItemList())

    suspend fun init() {
        if (useCases.handleFirstLaunch(firstLaunchLists.firstLaunchLists())) {
            refreshAllLists()
        }
        setAppVersion()
    }

    private fun updateUiState(block: UIState.() -> UIState) {
        _uiState.value = block(_uiState.value).apply { }
    }

    suspend fun createList(itemList: ItemList) {
        useCases.createList(itemList)
    }

    fun editList(itemList: ItemList) {
        viewModelScope.launch {
            useCases.editList(itemList)
        }
    }

    fun refreshAllLists() {
        updateUiState { copy(isRefreshing = true) }
        viewModelScope.launch {
            getAllLists()

            _forceRefreshTrigger.value++
        }
    }

    suspend fun removeList(
        itemList: ItemList,
        deleteBackupFile: Boolean,
        onFileDeleted: () -> Unit
    ) {
        useCases.removeList(itemList, deleteBackupFile, onFileDeleted)
    }

    fun selectList(position: Int) {
        preferences.selectedListIndex = position
    }

    suspend fun importList(uri: Uri): ItemList {
        return useCases.importList(uri)
    }

    fun moveList(fromPosition: Int, toPosition: Int) {
        viewModelScope.launch {
            useCases.moveList(fromPosition, toPosition, allLists.value)
        }
    }

    fun clearComment() {
        updateUiState { copy(addCommentText = "") }
    }

    fun switchItemStatus(item: Item, onNewPositions: (old: Int, new: Int) -> Unit) {
        item.done = !item.done
        val oldPosition = selectedList.value.items.indexOf(item)
        val newPosition = when (item.done) {
            true -> selectedList.value.items.size - 1
            else -> 0
        }
        val tempList = selectedList.value.copy()
        tempList.items.removeAt(oldPosition)
        tempList.items.add(newPosition, item)

        onNewPositions(oldPosition, newPosition)

        editList(tempList)
    }

    fun showOrHideComment(item: Item) {
        item.commentDisplayed = !item.commentDisplayed
        editList(selectedList.value.copy())
    }

    fun addItem(item: Item) {
        selectedList.value.items.add(0, item.apply {
            comment = _uiState.value.addCommentText
        })
        updateUiState { copy(addCommentText = "") }
        editList(selectedList.value.copy())
    }

    fun setAddItemComment(text: String) {
        updateUiState {
            copy(
                addCommentText = text,
                showButtonClearComment = text.isNotEmpty()
            )
        }
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

    fun removeItem(item: Item) {
        selectedList.value.items.remove(item)
        editList(selectedList.value.copy())
    }

    fun clearSelectedList() {
        selectedList.value.items.clear()
        editList(selectedList.value.copy())
    }

    fun editItem(index: Int, item: Item) {
        selectedList.value.items[index] = item
        editList(selectedList.value.copy())
    }

    fun moveItem(fromPosition: Int, toPosition: Int) {
        val fromItem = selectedList.value.items[fromPosition]
        selectedList.value.items.removeAt(fromPosition)
        selectedList.value.items.add(toPosition, fromItem)
        editList(selectedList.value.copy())
    }

    private fun setAppVersion() {
        if (preferences.version != BuildConfig.VERSION_NAME) {
            _showWhatsNew.value = useCases.showWhatsNew()
            preferences.version = BuildConfig.VERSION_NAME
        }
    }

    fun shareSelectedList(context: Context) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, selectedList.value.toStringForShare(context))
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        context.startActivity(shareIntent)
    }

    private fun getAllLists() {
        getAllListsJob?.cancel()
        viewModelScope.launch {
            updateUiState { copy(isRefreshing = true) }
            useCases.getAllLists().onEach {
                allListsResources.value = it
                updateUiState { copy(isRefreshing = false) }
            }.launchIn(this)
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
        allListsResources.value = allListsResources.value.copy(errors = listOf())
    }


    fun whatsNewShown() {
        _showWhatsNew.value = false
    }
}