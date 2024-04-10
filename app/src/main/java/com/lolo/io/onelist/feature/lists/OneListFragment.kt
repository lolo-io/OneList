package com.lolo.io.onelist.feature.lists

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.NinePatchDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.SoundEffectConstants
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.anggrayudi.storage.extension.launchOnUiThread
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.h6ah4i.android.widget.advrecyclerview.animator.DraggableItemAnimator
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager
import com.h6ah4i.android.widget.advrecyclerview.touchguard.RecyclerViewTouchActionGuardManager
import com.lolo.io.onelist.MainActivity
import com.lolo.io.onelist.R
import com.lolo.io.onelist.core.data.migration.UpdateHelper
import com.lolo.io.onelist.core.design.OneListTheme
import com.lolo.io.onelist.core.design.space
import com.lolo.io.onelist.core.model.Item
import com.lolo.io.onelist.core.model.ItemList
import com.lolo.io.onelist.core.ui.Config
import com.lolo.io.onelist.core.ui.util.BUTTON_ANIM_DURATION
import com.lolo.io.onelist.core.ui.util.afterTextChanged
import com.lolo.io.onelist.core.ui.util.animHideFlip
import com.lolo.io.onelist.core.ui.util.animShowFlip
import com.lolo.io.onelist.core.ui.util.flipX
import com.lolo.io.onelist.core.ui.util.isVisible
import com.lolo.io.onelist.core.ui.util.isVisibleInvisible
import com.lolo.io.onelist.core.ui.util.shake
import com.lolo.io.onelist.databinding.FragmentOneListBinding
import com.lolo.io.onelist.feature.lists.components.add_item_input.AddItemInput
import com.lolo.io.onelist.feature.lists.components.dialogs.CreateListDialog
import com.lolo.io.onelist.feature.lists.components.dialogs.components.DialogContainer
import com.lolo.io.onelist.feature.lists.components.dialogs.EditItemDialog
import com.lolo.io.onelist.feature.lists.components.dialogs.EditListDialog
import com.lolo.io.onelist.feature.lists.components.dialogs.DeleteListDialog
import com.lolo.io.onelist.feature.lists.components.dialogs.model.DialogShown
import com.lolo.io.onelist.feature.lists.components.header.OneListHeader
import com.lolo.io.onelist.feature.lists.components.header.OneListHeaderActions
import com.lolo.io.onelist.feature.lists.components.items_lists.ReorderableAndSwipeableItemList
import com.lolo.io.onelist.feature.lists.components.items_lists.rememberSwipeableLazyListState
import com.lolo.io.onelist.feature.lists.components.list_chips.ListsFlowRow
import com.lolo.io.onelist.feature.lists.dialogs.ACTION_CLEAR
import com.lolo.io.onelist.feature.lists.dialogs.ACTION_RM_FILE
import com.lolo.io.onelist.feature.lists.dialogs.deleteListDialog
import com.lolo.io.onelist.feature.lists.dialogs.editItemDialog
import com.lolo.io.onelist.feature.lists.dialogs.editListDialog
import com.lolo.io.onelist.feature.lists.lists_adapters.ItemTouchHelperCallback
import com.lolo.io.onelist.feature.lists.lists_adapters.ItemsAdapter
import com.lolo.io.onelist.feature.lists.lists_adapters.ItemsCallbacks
import com.lolo.io.onelist.feature.lists.lists_adapters.ListsAdapter
import com.lolo.io.onelist.feature.lists.lists_adapters.ListsCallbacks
import com.lolo.io.onelist.feature.lists.utils.shareList
import com.lolo.io.onelist.feature.settings.SettingsFragment
import com.lolo.io.onelist.feature.settings.showReleaseNote
import ifNotEmpty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.getViewModel
import kotlin.math.roundToInt


class OneListFragment : Fragment(), ListsCallbacks, ItemsCallbacks,
    MainActivity.OnDispatchTouchEvent {

    companion object {
        const val ARG_EXT_FILE_URI = "EXT_FILE_URI"
    }

    private var _binding: FragmentOneListBinding? = null
    private val binding: FragmentOneListBinding
        get() = _binding!!

    private val viewModel
            by lazy { getViewModel<OneListFragmentViewModel>() }

    private val _fragmentAllListsFinalInstance = mutableListOf<ItemList>()

    private var container: ViewGroup? = null

    private val listsAdapter: ListsAdapter by lazy {
        ListsAdapter(
            requireActivity(),
            _fragmentAllListsFinalInstance,
            this
        )
    }
    private val itemsAdapter by lazy { ItemsAdapter(requireActivity(), this) }

    private val isAddCommentShown
        get() = binding.addCommentEditText.height > 0

    private val updateHelper: UpdateHelper by inject()


    override fun onAttach(context: Context) {
        super.onAttach(context)

        updateHelper.applyMigrationsIfNecessary(requireActivity()) {
            launchOnUiThread {
                listsAdapter.notifyDataSetChanged()
                itemsAdapter.notifyDataSetChanged()
            }
        }

        activity?.let {
            it.packageManager.getPackageInfo(it.packageName, 0).versionName
        }

    }

    fun ComposeView.set(content: @Composable () -> Unit) {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            OneListTheme {
                content()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOneListBinding.inflate(inflater, container, false)
        this.container = container

        return ComposeView(requireContext()).apply {
            setContent {
                OneListTheme {

                    val view = LocalView.current
                    val coroutineScope = rememberCoroutineScope()

                    val keyboardController = LocalSoftwareKeyboardController.current
                    var showSelectedListControls by remember { mutableStateOf(false) }

                    var showDialog by rememberSaveable { mutableStateOf(DialogShown.None) }
                    var editedItem by remember { mutableStateOf<Item?>(null) }

                    val selectedList =
                        viewModel.selectedList.collectAsStateWithLifecycle().value

                    val swipeableListState = rememberSwipeableLazyListState()


                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(Unit) {
                                detectTapGestures(onTap = {
                                    showSelectedListControls = false
                                    keyboardController?.hide()
                                })
                            },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        val allLists = viewModel.allLists.collectAsStateWithLifecycle().value


                        var addItemTitle by rememberSaveable { mutableStateOf("") }
                        var addItemComment by rememberSaveable { mutableStateOf("") }

                        val displayedItems =
                            viewModel.displayedItems.collectAsStateWithLifecycle().value
                        val themeSpaces = MaterialTheme.space
                        val refreshing =
                            viewModel.uiState.collectAsStateWithLifecycle().value.isRefreshing


                        Surface(
                            modifier = Modifier
                                .padding(bottom = MaterialTheme.space.Small),
                            shadowElevation = 8.dp
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(bottom = MaterialTheme.space.SmallUpper),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {

                                OneListHeader(
                                    showSelectedListControls = showSelectedListControls,
                                    actions = OneListHeaderActions(
                                        onClickCreateList = {
                                            showDialog = DialogShown.CreateListDialog
                                        },
                                        onClickEditList = {
                                            showSelectedListControls = false
                                            showDialog = DialogShown.EditListDialog
                                        },
                                        onClickDeleteList = {
                                            showSelectedListControls = false
                                            showDialog = DialogShown.DeleteListDialog
                                        },
                                        onClickShareList = {
                                            coroutineScope.launch {
                                                delay(200)
                                                shareList(context, selectedList)
                                            }
                                            view.playSoundEffect(SoundEffectConstants.CLICK)
                                        }
                                    )
                                )

                                ListsFlowRow(
                                    modifier = Modifier.padding(horizontal = MaterialTheme.space.Small),
                                    lists = allLists,
                                    selectedList = selectedList,
                                    onClick = {
                                        showSelectedListControls = false
                                        viewModel.selectList(it)
                                    },
                                    onLongClick = {
                                        viewModel.selectList(it)
                                        showSelectedListControls = true
                                    },
                                    onListReordered = { list, _ ->
                                        viewModel.reorderLists(list)
                                    })

                            }
                        }

                        AddItemInput(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = MaterialTheme.space.Normal)
                                .padding(top = MaterialTheme.space.Small)
                                .zIndex(10f),
                            value = addItemTitle,
                            onValueChange = { addItemTitle = it },
                            commentValue = addItemComment,
                            onCommentValueChange = { addItemComment = it },
                            onSubmit = {
                                viewModel.addItem(
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
                            }
                        )


                        ReorderableAndSwipeableItemList(
                            modifier = Modifier.offset {
                                IntOffset(x = 0, y = themeSpaces.Tiny.toPx().roundToInt() * -1)
                            },
                            onClickOnItem = {
                                viewModel.switchItemStatus(it)
                                view.playSoundEffect(SoundEffectConstants.CLICK)
                            },
                            items = displayedItems,
                            onItemSwipedToStart = {
                                coroutineScope.launch {
                                    delay(1500)
                                    viewModel.removeItem(it)
                                }
                            },
                            onItemSwipedToEnd = {
                                showDialog = DialogShown.EditItemDialog
                                editedItem = it
                            },
                            onShowOrHideComment = {
                                viewModel.switchItemCommentShown(it)
                                view.playSoundEffect(SoundEffectConstants.CLICK)
                            },
                            onListReordered = { list ->
                                viewModel.onSelectedListReordered(list)
                            },
                            isRefreshing = refreshing,
                            onRefresh = {
                                viewModel.refresh()
                            },
                            swipeableListState = swipeableListState,
                        )
                    }


                    DialogContainer(
                        shown = showDialog != DialogShown.None,
                        dismiss = {
                            showDialog = DialogShown.None
                            editedItem?.let {
                                swipeableListState.resetSwipeState(it)
                                editedItem = null
                            }
                        }) {

                        when (showDialog) {
                            DialogShown.CreateListDialog -> {
                                CreateListDialog(
                                    onSubmit = {
                                        viewModel.createList(ItemList(title = it))
                                        dismiss()
                                    }
                                )
                            }

                            DialogShown.EditListDialog -> {
                                EditListDialog(selectedList,
                                    onSubmit = {
                                        viewModel.editList(selectedList.copy(title = it))
                                        dismiss()
                                    })
                            }

                            DialogShown.EditItemDialog -> {
                                editedItem?.let { itemToEdit ->
                                    EditItemDialog(
                                        itemToEdit,
                                        onSubmit = {
                                            viewModel.editItem(it)
                                            dismiss()
                                        },
                                    )
                                }

                            }

                            DialogShown.DeleteListDialog -> {
                                DeleteListDialog(
                                    list = selectedList,
                                    onDeleteList = {
                                        viewModel.deleteList(
                                            selectedList,
                                            true,
                                            onFileDeleted = {
                                                Toast.makeText(
                                                    context,
                                                    context.getString(R.string.file_deleted),
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            })
                                        view.playSoundEffect(SoundEffectConstants.CLICK)
                                        dismiss()
                                    },
                                    onJustClearList = {
                                        viewModel.clearSelectedList()
                                        view.playSoundEffect(SoundEffectConstants.CLICK)
                                        dismiss()
                                    })
                            }

                            DialogShown.None -> {}
                        }
                    }
                }
            }
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        setupListsRecyclerView()
        setupItemsRecyclerView()

        lifecycleScope.launch {
            viewModel.init()
            if (_fragmentAllListsFinalInstance.size != viewModel.allLists.value.size) {
                listsAdapter.notifyItemRangeInserted(0, viewModel.allLists.value.size)
            }
        }


        binding.headerComposed.set {
            OneListHeader()
        }

        binding.listsRecyclerViewComposed.set {
            val allLists = viewModel.allLists.collectAsStateWithLifecycle().value
            val selectedList = viewModel.selectedList.collectAsStateWithLifecycle().value
            ListsFlowRow(lists = allLists, selectedList = selectedList,
                onClick = { viewModel.selectList(it) },
                onLongClick = {
                    viewModel.selectList(it)
                },
                onListReordered = { list, _ ->
                    viewModel.reorderLists(list)
                })
        }


        binding.addItem.set {
            var itemTitle by remember { mutableStateOf("") }
            var itemComment by remember { mutableStateOf("") }

            AddItemInput(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = MaterialTheme.space.Normal)
                    .padding(top = MaterialTheme.space.Small),
                value = itemTitle,
                onValueChange = { itemTitle = it },
                commentValue = itemComment,
                onCommentValueChange = { itemComment = it },
                onSubmit = {
                    viewModel.addItem(
                        Item(
                            title = itemTitle,
                            comment = itemComment,
                            commentDisplayed = itemComment.isNotEmpty()
                        )
                    )
                    itemTitle = ""
                    itemComment = ""
                    // todo focus to item title
                }
            )
        }

        binding.itemsRecyclerViewComposed.set {
            val displayedItems = viewModel.displayedItems.collectAsStateWithLifecycle().value
            val themeSpaces = MaterialTheme.space
            val refreshing = viewModel.uiState.collectAsStateWithLifecycle().value.isRefreshing
            ReorderableAndSwipeableItemList(
                items = displayedItems,
                modifier = Modifier.offset {
                    IntOffset(x = 0, y = themeSpaces.Small.toPx().roundToInt() * -1)
                },
                onItemSwipedToStart = {
                    viewModel.removeItem(it)
                },
                onClickOnItem = {
                    viewModel.switchItemStatus(it)
                },
                onListReordered = { list ->
                    viewModel.onSelectedListReordered(list)
                },
                onShowOrHideComment = {
                    viewModel.switchItemCommentShown(it)

                },
                isRefreshing = refreshing,
                onRefresh = {
                    viewModel.refresh()
                },
            )
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.allLists.collect {
                    _fragmentAllListsFinalInstance.clear()
                    _fragmentAllListsFinalInstance.addAll(it)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.selectedList.collect {
                    //     itemsAdapter.items = it.items
                    itemsAdapter.notifyDataSetChanged()
                    listsAdapter.selectList(it)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.forceRefreshTrigger.collect {
                    if (it > 0) {
                        _fragmentAllListsFinalInstance.clear()
                        _fragmentAllListsFinalInstance.addAll(viewModel.allLists.value)
                        listsAdapter.notifyDataSetChanged()
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {
                    updateUI(it)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.errorMessage.collect {

                    if (it != null) {
                        val message = StringBuilder(getString(it.resId)).apply {
                            it.restResIds.ifNotEmpty { restResIds ->
                                append(" : ")
                                append(getString(restResIds.first()))
                            }
                        }.toString()

                        Toast.makeText(
                            context,
                            message,
                            Toast.LENGTH_SHORT
                        ).show()

                        launch {
                            delay(500)
                            viewModel.resetError()
                        }
                    }
                }
            }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.showWhatsNew.collect {
                    if (it) {
                        showReleaseNote(requireActivity())
                        viewModel.whatsNewShown()

                    }
                }
            }
        }

        binding.addItemEditText.setOnEditorActionListener { tv, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                addItem(Item(tv.text.toString()))
            }
            true
        }

        binding.validate.setOnClickListener {
            addItem(Item(binding.addItemEditText.text.toString()))
        }

        binding.buttonAddList.setOnClickListener {
            editListDialog(requireContext()) { list ->
                lifecycleScope.launch {
                    createList(
                        list
                    )
                }
            }.show()
        }

        binding.buttonEditList.setOnClickListener { editList() }
        binding.buttonRemoveList.setOnClickListener {
            showDeleteDialog(viewModel.selectedList.value)
        }
        binding.buttonAddComment.setOnClickListener { switchCommentSection() }
        binding.buttonClearComment.setOnClickListener { viewModel.clearComment() }


        binding.menuSettings.setOnClickListener { v ->
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.enter_from_right,
                    R.anim.zoom_out,
                    R.anim.zoom_in,
                    R.anim.exit_to_right
                )
                .add(container?.id ?: 0, SettingsFragment())
                .hide(this@OneListFragment)
                .addToBackStack(null).commit()
        }

        binding.swipeContainer.setOnRefreshListener {
            viewModel.refreshAllLists()
        }


        binding.addItemEditText.afterTextChanged {
            viewModel.setAddItemText(it)
        }

        binding.addCommentEditText.afterTextChanged { text ->
            viewModel.setAddItemComment(text)
        }
    }

    private fun updateUI(uiState: UIState) {
        binding.swipeContainer.isRefreshing = uiState.isRefreshing
        binding.validate.isVisibleInvisible = uiState.showValidate
        binding.buttonAddComment.isVisibleInvisible = uiState.showAddCommentArrow
        if (uiState.addCommentText.isEmpty()) {
            binding.addCommentEditText.setText(uiState.addCommentText)
        }
        binding.buttonClearComment.isVisible = uiState.showButtonClearComment
    }

    override fun onStart() {
        super.onStart()
        viewModel.refresh()
    }


    override fun onResume() {
        super.onResume()

        if (arguments?.containsKey(ARG_EXT_FILE_URI) == true) {
            lifecycleScope.launch {
                try {
                    val imported = viewModel.importList(
                        Uri.parse(
                            arguments?.getString(ARG_EXT_FILE_URI)
                                ?: throw IllegalArgumentException("uri must not be null")
                        )
                    )

                    Toast.makeText(
                        activity,
                        getString(R.string.list_copied, imported.title),
                        Toast.LENGTH_LONG
                    ).show()

                } catch (e: Exception) {
                    Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
                }
            }
            arguments?.clear()
            viewModel.refresh()
        }

    }

    private fun setupListsRecyclerView() {
        binding.listsRecyclerView.adapter = listsAdapter
        val layoutManager = FlexboxLayoutManager(context)
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.justifyContent = JustifyContent.CENTER
        layoutManager.flexWrap = FlexWrap.WRAP
        binding.listsRecyclerView.layoutManager = layoutManager
        binding.listsRecyclerView.itemAnimator = DefaultItemAnimator()
        val listCallback = ItemTouchHelperCallback(listsAdapter)
        val listTouchHelper = ItemTouchHelper(listCallback)
        listTouchHelper.attachToRecyclerView(binding.listsRecyclerView)
    }

    private fun setupItemsRecyclerView() {
        val itemsDragDropManager = RecyclerViewDragDropManager()
        val itemsSwipeManager = RecyclerViewSwipeManager()
        var wrappedAdapter = itemsDragDropManager.createWrappedAdapter(itemsAdapter)
        wrappedAdapter = itemsSwipeManager.createWrappedAdapter(wrappedAdapter)
        binding.itemsRecyclerView.adapter = wrappedAdapter
        binding.itemsRecyclerView.layoutManager = LinearLayoutManager(context)
        val itemsTouchActionGuardManager = RecyclerViewTouchActionGuardManager()
        itemsTouchActionGuardManager.setInterceptVerticalScrollingWhileAnimationRunning(true)
        itemsTouchActionGuardManager.isEnabled = true
        itemsTouchActionGuardManager.attachRecyclerView(binding.itemsRecyclerView)
        itemsSwipeManager.attachRecyclerView(binding.itemsRecyclerView)
        itemsDragDropManager.attachRecyclerView(binding.itemsRecyclerView)
        itemsDragDropManager.setInitiateOnLongPress(true)
        itemsDragDropManager.setInitiateOnMove(false)
        itemsDragDropManager.setDraggingItemShadowDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.material_shadow_z3
            ) as NinePatchDrawable?
        )
        val animator = DraggableItemAnimator()
        animator.supportsChangeAnimations = false
        binding.itemsRecyclerView.itemAnimator = animator
        if (Config.smallScreen) {
            val dividerItemDecoration = DividerItemDecoration(
                binding.itemsRecyclerView.context,
                (binding.itemsRecyclerView.layoutManager as LinearLayoutManager).orientation
            )
            binding.itemsRecyclerView.addItemDecoration(dividerItemDecoration)
        }
    }


    // Lists handlers
    private suspend fun createList(itemList: ItemList) {
        listsAdapter.notifyItemInserted(viewModel.allLists.value.size)
        viewModel.createListFragment(itemList)
        binding.addItemEditText.requestFocus()
    }

    private fun editList() {
        editListDialog(requireContext(), viewModel.selectedList.value) { list ->
            viewModel.editList(list)
            listsAdapter
                .notifyItemChanged(viewModel.allLists.value.indexOf(viewModel.selectedList.value))
            hideEditionButtons()
        }.show()
    }


    private fun showDeleteDialog(itemList: ItemList) {
        deleteListDialog(requireContext(), itemList) { action ->
            if (action and ACTION_CLEAR != 0) {
                itemsAdapter
                    .notifyItemRangeRemoved(0, viewModel.selectedList.value.items.size)
                viewModel.clearSelectedList()
            } else {
                itemsAdapter
                    .notifyItemRangeRemoved(0, viewModel.selectedList.value.items.size)
                listsAdapter.notifyItemRemoved(viewModel.allLists.value.indexOf(itemList))
                lifecycleScope.launch {
                    try {
                        viewModel.removeList(itemList, action and ACTION_RM_FILE != 0,
                            onFileDeleted = {
                                activity?.runOnUiThread {
                                    Toast.makeText(
                                        requireContext(),
                                        getString(R.string.file_deleted),
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        )
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.error_deleting_list_file),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }



            hideEditionButtons()
        }.show()
    }

    override fun onSelectList(position: Int) {

        itemsAdapter.notifyItemRangeRemoved(
            0,
            viewModel.selectedList.value.items.size
        )

        viewModel.selectList(position)

        itemsAdapter.notifyItemRangeInserted(
            0,
            viewModel.allLists.value[position].items.size
        )

        (binding.itemsRecyclerView.itemAnimator as DraggableItemAnimator).supportsChangeAnimations =
            false
    }

    override fun onListAdapterStartDrag() = showEditionButtons()
    override fun onListMoved(fromPosition: Int, toPosition: Int) {
        if (binding.buttonRemoveList.alpha == 1F)
            hideEditionButtons()
        viewModel.moveList(fromPosition, toPosition)
        listsAdapter.notifyItemMoved(fromPosition, toPosition)
    }


    // Items handlers :
    private fun addItem(item: Item) {
        lifecycleScope.launch {
            if (item.title.isNotEmpty()) {
                if (viewModel.allLists.value.isEmpty()) {
                    createList(ItemList(title = getString(R.string.list_default_name)))
                }
                viewModel.addItem(item)
                val position = viewModel.selectedList.value.items.indexOf(item)

                itemsAdapter.notifyItemInserted(position)
                binding.itemsRecyclerView.smoothScrollToPosition(0)
                binding.addItemEditText.setText(R.string.empty)
                binding.addItemEditText.requestFocus()

                if (binding.addCommentEditText.text.isEmpty() && isAddCommentShown) {
                    switchCommentSection()
                }

            } else listOf(binding.addItemEditText, binding.validate).forEach { it.shake() }
        }
    }

    override fun onRemoveItem(item: Item) {
        viewModel.removeItem(item)
        itemsAdapter.notifyItemRemoved(viewModel.selectedList.value.items.indexOf(item))
    }

    override fun openEditItemDialog(index: Int) {
        editItemDialog(requireContext(), viewModel.selectedList.value.items[index]) { updatedItem ->
            viewModel.editItem(index, updatedItem)
            itemsAdapter.notifyItemChanged(index)
        }.show()
    }

    override fun onSwitchItemStatus(item: Item) {
        /* viewModel.switchItemStatus(item) { oldPosition, newPosition ->
             itemsAdapter.notifyItemChanged(oldPosition)
             itemsAdapter.notifyItemMoved(oldPosition, newPosition)

             val scrolledToTop =
                 (binding.itemsRecyclerView.layoutManager as LinearLayoutManager)
                     .findFirstVisibleItemPosition() == 0
             if (scrolledToTop || oldPosition == 0) binding.itemsRecyclerView
                 .scrollToPosition(0)
         }
         */

        // todo scrolls
    }

    override fun onShowOrHideComment(item: Item) {
        viewModel.switchItemCommentShown(item)
        itemsAdapter.notifyItemChanged(viewModel.selectedList.value.items.indexOf(item))
    }

    override fun onMoveItem(fromPosition: Int, toPosition: Int) {
        viewModel.moveItem(fromPosition, toPosition)
    }

    // hide keyboard when touch outside an EditText
    override fun onDispatchTouchEvent(ev: MotionEvent) {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val addItemRect = Rect()
            val addCommentRect = Rect()
            val showCommentRect = Rect()
            binding.addItemEditText.getGlobalVisibleRect(addItemRect)
            binding.addCommentEditText.getGlobalVisibleRect(addCommentRect)
            binding.buttonAddComment.getGlobalVisibleRect(showCommentRect)
            val view = activity?.currentFocus
            val rawX = ev.rawX.toInt()
            val rawY = ev.rawY.toInt()
            if (view != null && view is EditText) {
                val r = Rect()
                view.getGlobalVisibleRect(r)
                if (!addItemRect.contains(rawX, rawY) && !addCommentRect.contains(
                        rawX,
                        rawY
                    ) && !showCommentRect.contains(rawX, rawY)
                ) {
                    view.clearFocus()
                    val inputMethodManager =
                        activity?.application?.getSystemService(Activity.INPUT_METHOD_SERVICE)
                                as InputMethodManager
                    inputMethodManager
                        .hideSoftInputFromWindow(binding.rootView.windowToken, 0)
                }
            }

            val rectButtons = Rect()
            binding.buttonsLayout.getGlobalVisibleRect(rectButtons)

            if (binding.buttonRemoveList.alpha == 1F && !rectButtons.contains(rawX, rawY))
                hideEditionButtons()
        }
    }

    private fun switchCommentSection() {
        binding.addCommentEditText.measure(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        val measuredHeight = binding.addCommentEditText.measuredHeight
        val height = binding.addCommentEditText.height
        val commentSlideAnimation =
            ValueAnimator.ofInt(height, measuredHeight - height).setDuration(BUTTON_ANIM_DURATION)
        commentSlideAnimation.addUpdateListener { animation ->
            binding.addCommentEditText.layoutParams.height = animation.animatedValue as Int
            binding.addCommentEditText.requestLayout()
            binding.buttonClearComment.visibility = View.GONE
            if (animation.animatedValue == measuredHeight && animation.animatedValue as Int > 0) {
                binding.addCommentEditText.layoutParams.height =
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                if (binding.addCommentEditText.text.isNotEmpty()) {
                    binding.buttonClearComment.visibility =
                        View.VISIBLE
                }
            }
        }
        commentSlideAnimation.start()

        binding.buttonAddComment.flipX()

        val editText = if (!isAddCommentShown) binding.addCommentEditText
        else binding.addItemEditText
        editText.requestFocus()

        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun showEditionButtons() {
        binding.buttonShareList.visibility = View.GONE
        binding.buttonRemoveList.animShowFlip()
        binding.buttonAddList.animHideFlip()
        binding.buttonEditList.animShowFlip()
    }

    private fun hideEditionButtons() {
        binding.buttonShareList.visibility = View.VISIBLE
        binding.buttonAddList.animShowFlip()
        binding.buttonRemoveList.animHideFlip()
        binding.buttonEditList.animHideFlip()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}