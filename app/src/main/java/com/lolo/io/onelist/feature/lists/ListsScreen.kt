package com.lolo.io.onelist.feature.lists

import android.content.res.Configuration
import android.view.SoundEffectConstants
import android.widget.Toast
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lolo.io.onelist.R
import com.lolo.io.onelist.core.design.space
import com.lolo.io.onelist.core.model.Item
import com.lolo.io.onelist.core.model.ItemList
import com.lolo.io.onelist.core.model.previewMany
import com.lolo.io.onelist.core.ui.composables.ComposePreview
import com.lolo.io.onelist.feature.lists.components.add_item_input.AddItemInput
import com.lolo.io.onelist.feature.lists.components.dialogs.CreateListDialog
import com.lolo.io.onelist.feature.lists.components.dialogs.DeleteListDialog
import com.lolo.io.onelist.feature.lists.components.dialogs.EditItemDialog
import com.lolo.io.onelist.feature.lists.components.dialogs.EditListDialog
import com.lolo.io.onelist.feature.lists.components.dialogs.components.DialogContainer
import com.lolo.io.onelist.feature.lists.components.dialogs.model.DialogShown
import com.lolo.io.onelist.feature.lists.components.header.OneListHeader
import com.lolo.io.onelist.feature.lists.components.header.OneListHeaderActions
import com.lolo.io.onelist.feature.lists.components.items_lists.ReorderableAndSwipeableItemList
import com.lolo.io.onelist.feature.lists.components.items_lists.rememberSwipeableLazyListState
import com.lolo.io.onelist.feature.lists.components.list_chips.ListsFlowRow
import com.lolo.io.onelist.feature.lists.utils.shareList
import ifNotEmpty
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import kotlin.math.roundToInt

@Composable
internal fun ListsScreen(navigateToSettings: () -> Unit) {
    val viewModel = koinInject<ListScreenViewModel>()

    val context = LocalContext.current

    val allLists = viewModel.allLists.collectAsStateWithLifecycle().value
    val selectedList = viewModel.selectedList.collectAsStateWithLifecycle().value
    val displayedItems = viewModel.displayedItems.collectAsStateWithLifecycle().value
    val refreshing = viewModel.isRefreshing.collectAsStateWithLifecycle().value

    val errorMessage = viewModel.errorMessage.collectAsStateWithLifecycle().value

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            val message = StringBuilder(context.getString(it.resId)).apply {
                it.restResIds.ifNotEmpty { restResIds ->
                    append(" : ")
                    append(context.getString(restResIds.first()))
                }
            }.toString()

            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.resetError()
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.init()
    }

    val listScreenActions = remember {
        object : ListScreenActions {
            override fun selectList(list: ItemList) = viewModel.selectList(list)
            override fun reorderLists(lists: List<ItemList>) = viewModel.reorderLists(lists)
            override fun addItem(item: Item) = viewModel.addItem(item)
            override fun switchItemStatus(item: Item) = viewModel.switchItemStatus(item)
            override fun removeItem(item: Item) = viewModel.removeItem(item)
            override fun switchItemCommentShown(item: Item) = viewModel.switchItemCommentShown(item)
            override fun onSelectedListReordered(items: List<Item>) =
                viewModel.onSelectedListReordered(items)

            override fun refresh() = viewModel.refresh()
            override fun createList(list: ItemList) = viewModel.createList(list)
            override fun editList(list: ItemList) = viewModel.editList(list)
            override fun editItem(item: Item) = viewModel.editItem(item)
            override fun deleteList(
                list: ItemList, deleteBackupFile: Boolean,
                onFileDeleted: () -> Unit
            ) = viewModel.deleteList(list, deleteBackupFile, onFileDeleted)

            override fun clearList(list: ItemList) = viewModel.clearList(list)
        }
    }

    ListsScreenUI(
        allLists = allLists,
        selectedList = selectedList,
        displayedItems = displayedItems,
        refreshing = refreshing,
        actions = listScreenActions,
        navigateToSettings = navigateToSettings
    )
}

@Composable
private fun ListsScreenUI(
    allLists: List<ItemList>,
    selectedList: ItemList,
    displayedItems: List<Item>,
    refreshing: Boolean,
    actions: ListScreenActions,
    navigateToSettings: () -> Unit
) {
    val view = LocalView.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val keyboardController = LocalSoftwareKeyboardController.current
    var showSelectedListControls by remember { mutableStateOf(false) }

    var showDialog by rememberSaveable { mutableStateOf(DialogShown.None) }
    var editedItem by remember { mutableStateOf<Item?>(null) }

    val swipeableListState = rememberSwipeableLazyListState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    showSelectedListControls = false
                    keyboardController?.hide()
                })
            }, horizontalAlignment = Alignment.CenterHorizontally
    ) {


        Surface(
            modifier = Modifier.padding(bottom = MaterialTheme.space.Small), shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(bottom = MaterialTheme.space.SmallUpper),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                OneListHeader(
                    showSelectedListControls = showSelectedListControls,
                    actions = OneListHeaderActions(onClickCreateList = {
                        showDialog = DialogShown.CreateListDialog
                    }, onClickEditList = {
                        showSelectedListControls = false
                        showDialog = DialogShown.EditListDialog
                    }, onClickDeleteList = {
                        showSelectedListControls = false
                        showDialog = DialogShown.DeleteListDialog
                    }, onClickShareList = {
                        coroutineScope.launch {
                            delay(200)
                            shareList(context, selectedList)
                        }
                        view.playSoundEffect(SoundEffectConstants.CLICK)
                    },
                        onClickSettings = {
                            navigateToSettings()
                        })
                )

                ListsFlowRow(modifier = Modifier.padding(horizontal = MaterialTheme.space.Small),
                    lists = allLists,
                    selectedList = selectedList,
                    onClick = {
                        showSelectedListControls = false
                        actions.selectList(it)
                    },
                    onLongClick = {
                        actions.selectList(it)
                        showSelectedListControls = true
                    },
                    onListReordered = { list ->
                        actions.reorderLists(list)
                    })

            }
        }

        var addItemTitle by rememberSaveable { mutableStateOf("") }
        var addItemComment by rememberSaveable { mutableStateOf("") }


        AddItemInput(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = MaterialTheme.space.Normal)
            .padding(top = MaterialTheme.space.Small)
            .zIndex(10f),
            value = addItemTitle,
            onValueChange = { addItemTitle = it },
            commentValue = addItemComment,
            onCommentValueChange = { addItemComment = it },
            onSubmit = {
                actions.addItem(
                    Item(
                        title = addItemTitle,
                        comment = addItemComment,
                        commentDisplayed = addItemComment.isNotEmpty()
                    )
                )
                addItemTitle = ""
                addItemComment = ""
                coroutineScope.launch {
                    swipeableListState.listState.animateScrollToItem(0)
                }
            })

        val themeSpaces = MaterialTheme.space

        val deleteItemJobs = remember {
            mutableMapOf<Long, Job?>()
        }

        ReorderableAndSwipeableItemList(
            modifier = Modifier.offset {
                IntOffset(x = 0, y = themeSpaces.Tiny.toPx().roundToInt() * -1)
            },
            onClickOnItem = {
                actions.switchItemStatus(it)
                view.playSoundEffect(SoundEffectConstants.CLICK)
            },
            items = displayedItems,
            onItemSwipedToStart = {
                deleteItemJobs[it.id] = coroutineScope.launch {
                    delay(2000)
                    actions.removeItem(it)
                }
            },
            onItemSwipedToEnd = {
                showDialog = DialogShown.EditItemDialog
                editedItem = it
            },
            onItemSwipedBackToCenter = {
                deleteItemJobs[it.id]?.cancel(CancellationException("Canceled by user"))
                deleteItemJobs -= it.id
            },
            onShowOrHideComment = {
                actions.switchItemCommentShown(it)
                view.playSoundEffect(SoundEffectConstants.CLICK)
            },
            onListReordered = { list ->
                actions.onSelectedListReordered(list)
            },
            isRefreshing = refreshing,
            onRefresh = {
                actions.refresh()
            },
            swipeableListState = swipeableListState,
        )
    }


    DialogContainer(shown = showDialog != DialogShown.None, dismiss = {
        showDialog = DialogShown.None
        editedItem?.let {
            swipeableListState.resetSwipeState(it)
            editedItem = null
        }
    }) {

        when (showDialog) {
            DialogShown.CreateListDialog -> {
                CreateListDialog(onSubmit = {
                    actions.createList(ItemList(title = it))
                    dismiss()
                })
            }

            DialogShown.EditListDialog -> {
                EditListDialog(selectedList, onSubmit = {
                    actions.editList(selectedList.copy(title = it))
                    dismiss()
                })
            }

            DialogShown.EditItemDialog -> {
                editedItem?.let { itemToEdit ->
                    EditItemDialog(
                        itemToEdit,
                        onSubmit = {
                            actions.editItem(it)
                            dismiss()
                        },
                    )
                }

            }

            DialogShown.DeleteListDialog -> {
                DeleteListDialog(list = selectedList, onDeleteList = {
                    actions.deleteList(selectedList, true, onFileDeleted = {
                        Toast.makeText(
                            context,
                            context.getString(R.string.file_deleted),
                            Toast.LENGTH_SHORT
                        ).show()
                    })
                    view.playSoundEffect(SoundEffectConstants.CLICK)
                    dismiss()
                }, onJustClearList = {
                    actions.clearList(selectedList)
                    view.playSoundEffect(SoundEffectConstants.CLICK)
                    dismiss()
                })
            }

            DialogShown.None -> {}
        }
    }

}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview
@Composable
private fun Preview_ListsScreen() = ComposePreview {

    val allLists = ItemList.previewMany(5)
    val selectedList = allLists[0]
    val displayedItems = allLists[0].items
    var refreshing by remember { mutableStateOf(false) }

    val listScreenActions = remember {
        object : ListScreenActions {
            override fun selectList(list: ItemList) = showPreviewDialog("selectList")
            override fun reorderLists(lists: List<ItemList>) = showPreviewDialog("reorderLists")
            override fun addItem(item: Item) = showPreviewDialog("addItem")
            override fun switchItemStatus(item: Item) = showPreviewDialog("switchItemStatus")
            override fun removeItem(item: Item) = showPreviewDialog("removeItem")
            override fun switchItemCommentShown(item: Item) =
                showPreviewDialog("switchItemCommentShown")

            override fun onSelectedListReordered(items: List<Item>) =
                showPreviewDialog("onSelectedListReordered")

            override fun refresh() {
                refreshing = true
            }

            override fun createList(list: ItemList) = showPreviewDialog("createList")
            override fun editList(list: ItemList) = showPreviewDialog("editList")
            override fun editItem(item: Item) = showPreviewDialog("editItem")
            override fun deleteList(
                list: ItemList, deleteBackupFile: Boolean,
                onFileDeleted: () -> Unit
            ) = showPreviewDialog("deleteList")

            override fun clearList(list: ItemList) = showPreviewDialog("clearList")
        }
    }

    ListsScreenUI(
        allLists = allLists,
        selectedList = selectedList,
        displayedItems = displayedItems,
        refreshing = refreshing,
        actions = listScreenActions,
        navigateToSettings = { showPreviewDialog() },
    )
}