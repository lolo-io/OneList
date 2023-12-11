package com.lolo.io.onelist.feature.lists

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lolo.io.onelist.core.domain.use_cases.OneListUseCases
import com.lolo.io.onelist.core.model.Item
import com.lolo.io.onelist.core.model.ItemList
import com.lolo.io.onelist.feature.lists.tuto.FirstLaunchLists
import com.lolo.io.onelist.feature.lists.utils.toStringForShare
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import updateOne
import java.util.Collections

class OneListFragmentViewModel(
    private val firstLaunchLists: FirstLaunchLists,
    private val useCases: OneListUseCases,
) : ViewModel() {

    private val _uiState = MutableStateFlow(UIState())
    val uiState = _uiState.asStateFlow()

    private val _allLists = MutableStateFlow<List<ItemList>>(listOf())
    val allLists = _allLists.asStateFlow()

    private val _selectedListIndex = MutableStateFlow(useCases.selectedListIndex())

    private val _newListImportedTrigger = MutableStateFlow(0)
    val newListImportedTrigger = _newListImportedTrigger.asStateFlow()

    var selectedList = combine(_allLists, _selectedListIndex) { pAllLists, pIndex ->
        pAllLists.getOrNull(pIndex) ?: ItemList()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ItemList())

    suspend fun init() {
        useCases.handleFirstLaunch(firstLaunchLists.firstLaunchLists())
        _allLists.value = useCases.getAllLists()
    }

    private fun updateUiState(block: UIState.() -> UIState) {
        _uiState.value = block(_uiState.value).apply { }
    }


    fun createList(itemList: ItemList) {
        viewModelScope.launch {
            _selectedListIndex.value = _allLists.value.size
            itemList.position = _allLists.value.size
            _allLists.value = (_allLists.value + useCases.upsertList(itemList))
        }
    }

    fun editList(itemList: ItemList) {
        viewModelScope.launch {
            useCases.upsertList(itemList).let {
                _allLists.value = _allLists.value.updateOne(itemList) { it.id == itemList.id }
            }
        }
    }

    fun refreshAllLists() {
        viewModelScope.launch {
            updateUiState { copy(isRefreshing = true) }
            _allLists.value = useCases.getAllLists()
            updateUiState { copy(isRefreshing = false) }
        }
    }

    fun removeList(itemList: ItemList, deleteBackupFile: Boolean) {
        viewModelScope.launch {
            useCases.removeList(itemList, deleteBackupFile).let {
                _allLists.value = _allLists.value.filter { it.id != itemList.id }.also {
                    val position = allLists.value.indexOf(itemList)
                    if (position < it.size) {
                        selectList(position)
                    } else if (position > 0) {
                        selectList(position - 1)
                    }
                }

            }
        }
    }

    fun selectList(position: Int) {
        _selectedListIndex.value = position
        useCases.selectedListIndex(position)
    }

    suspend fun importList(uri: Uri): ItemList {
        return useCases.upsertList(uri).also {
            viewModelScope.launch {
                _allLists.value = useCases.getAllLists()
                _selectedListIndex.value = _allLists.value.size
                it.position = _allLists.value.size
                _allLists.value = (_allLists.value + useCases.upsertList(it))
                _newListImportedTrigger.value = _newListImportedTrigger.value + 1
            }
        }
    }

    fun moveList(fromPosition: Int, toPosition: Int) {
        val tempAllList = ArrayList(allLists.value)
        if (fromPosition < toPosition && toPosition < tempAllList.size) {
            for (i in fromPosition until toPosition) {
                Collections.swap(tempAllList, i, i + 1)
            }
        } else if (toPosition < tempAllList.size) {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(tempAllList, i, i - 1)
            }
        }
        tempAllList.forEachIndexed { i, list -> list.position = i + 1 }
        selectList(tempAllList.indexOf(selectedList.value))

        _allLists.value = tempAllList
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

    fun setAppVersion(versionName: String) {
        if (useCases.version() != versionName) useCases.version(versionName)
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
}