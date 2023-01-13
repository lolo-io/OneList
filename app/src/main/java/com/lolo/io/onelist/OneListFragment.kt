package com.lolo.io.onelist

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.NinePatchDrawable
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.h6ah4i.android.widget.advrecyclerview.animator.DraggableItemAnimator
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager
import com.h6ah4i.android.widget.advrecyclerview.touchguard.RecyclerViewTouchActionGuardManager
import com.lolo.io.onelist.dialogs.*
import com.lolo.io.onelist.model.Item
import com.lolo.io.onelist.model.ItemList
import com.lolo.io.onelist.updates.UpdateHelper
import com.lolo.io.onelist.util.*
import com.skydoves.powermenu.MenuAnimation
import com.skydoves.powermenu.PowerMenu
import com.skydoves.powermenu.PowerMenuItem
import com.skydoves.powermenu.kotlin.createPowerMenu
import kotlinx.android.synthetic.main.fragment_one_list.*
import java.util.*
import androidx.recyclerview.widget.DividerItemDecoration
import com.lolo.io.onelist.updates.appContext
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async


class OneListFragment : Fragment(), ListsCallbacks, ItemsCallbacks, MainActivity.OnDispatchTouchEvent {

    private var container: ViewGroup? = null
    private val mainActivity: MainActivity
        get() {
            if (activity is MainActivity) return activity as MainActivity
            else throw IllegalStateException("Activity must be MainActivity")
        }

    private val allLists: MutableList<ItemList> = arrayListOf()
    private var listsAdapter: ListsAdapter = ListsAdapter(allLists, this)
    private val itemsAdapter = ItemsAdapter(this)

    private var selectedList: ItemList = ItemList("")

    private val persistence: PersistenceHelper
        get() = mainActivity.persistence

    private val isAddCommentShown
        get() = addCommentEditText.height > 0

    var addItemsToBottom: Boolean
        get() {
            val sp = PreferenceManager.getDefaultSharedPreferences(appContext)  // for some reason, app.getPreferences(Context.MODE_PRIVATE) does not work here
            return sp.getBoolean("addItemsToBottom", false) ?: false
        }
        set(value) {
            val sp = PreferenceManager.getDefaultSharedPreferences(appContext)
            val editor = sp.edit()
            editor.putBoolean("addItemsToBottom", value)
            editor.apply()
        }

    var doneItemsToBottom: Boolean
        get() {
            val sp = PreferenceManager.getDefaultSharedPreferences(appContext)
            return sp.getBoolean("doneItemsToBottom", false) ?: false
        }
        set(value) {
            val sp = PreferenceManager.getDefaultSharedPreferences(appContext)
            val editor = sp.edit()
            editor.putBoolean("doneItemsToBottom", value)
            editor.apply()
        }

    private val popupMenu: PowerMenu? by lazy {
        context?.let {
            createPowerMenu(it) {
                addItem(PowerMenuItem(getString(R.string.settings), R.drawable.ic_settings_accent_24dp))
                setAnimation(MenuAnimation.SHOWUP_TOP_LEFT)
                setMenuRadius(10f)
                setMenuShadow(10f)
                setTextGravity(Gravity.START)
                setTextTypeface(Typeface.DEFAULT)
                setTextColor(ContextCompat.getColor(it, R.color.textColorPrimary))
                setMenuColor(ContextCompat.getColor(it, R.color.colorBackgroundPopup))
                setShowBackground(false)
                setAutoDismiss(true)
                setOnMenuItemClickListener { _, _ ->
                    parentFragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.enter_from_right, R.anim.zoom_out, R.anim.zoom_in, R.anim.exit_to_right)
                            .add(container?.id ?: 0, PreferenceFragment())
                            .hide(this@OneListFragment)
                            .addToBackStack(null).commit()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UpdateHelper.applyUpdatePatches(mainActivity)
        allLists.addAll(persistence.getAllLists())
        val ver = mainActivity.packageManager.getPackageInfo(mainActivity.packageName, 0).versionName
        if (persistence.version != ver) persistence.version = ver
    }

    @SuppressLint("InflateParams")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        this.container = container
        return inflater.inflate(R.layout.fragment_one_list, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        addItemEditText.setOnEditorActionListener { _, actionId, _ -> if (actionId == EditorInfo.IME_ACTION_DONE) addItem(); true }
        validate.setOnClickListener { addItem() }
        buttonAddList.setOnClickListener { editListDialog(mainActivity) { list -> createList(list) }.show() }
        buttonEditList.setOnClickListener { editList() }
        buttonRemoveList.setOnClickListener { deleteList(selectedList) }
        buttonAddComment.setOnClickListener { switchCommentSection() }
        buttonClearComment.setOnClickListener { addCommentEditText.text.clear() }
        buttonShareList.setOnClickListener { persistence.shareList(selectedList) }
        buttonShareAllLists.setOnClickListener { persistence.shareAllLists() }

        menu_arrow.setOnTouchListener { v, e ->
            if (e.action == MotionEvent.ACTION_DOWN) {
                if (popupMenu?.isShowing == false)
                    popupMenu?.showAsAnchorLeftTop(v, dpToPx(12), dpToPx(12))
                else popupMenu?.dismiss()
            }
            true
        }

        swipeContainer.setOnRefreshListener {
            persistence.refreshAllLists(allLists)
            listsAdapter.notifyDataSetChanged()
            itemsAdapter.notifyDataSetChanged()
            swipeContainer.isRefreshing = false
        }

        validate.visibility = View.INVISIBLE
        buttonAddComment.visibility = View.INVISIBLE
        addItemEditText.afterTextChanged {
            if (it.isNotEmpty()) {
                validate.visibility = View.VISIBLE
                buttonAddComment.visibility = View.VISIBLE
            } else {
                validate.visibility = View.INVISIBLE
                if (!isAddCommentShown) buttonAddComment.visibility = View.INVISIBLE
            }
        }

        addCommentEditText.afterTextChanged {
            if (it.isNotEmpty()) buttonClearComment.visibility = View.VISIBLE
            else buttonClearComment.visibility = View.GONE
        }

        setupListsRecyclerView()
        setupItemsRecyclerView()
    }

    override fun onResume() {
        super.onResume()

        if (arguments?.containsKey("EXT_FILE_URI") == true) { // opened an external file
            try {
                val imported = persistence.createListFromUri(arguments?.getParcelable("EXT_FILE_URI")?:throw IllegalArgumentException("uri must not be null"))
                persistence.saveListAsync(imported)
                allLists.add(imported)
                persistence.updateListIdsTableAsync(allLists)
                Toast.makeText(activity, getString(R.string.list_copied, imported.title), Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
            }
            activity?.finish()
        }

        val selectedListIndex = persistence.selectedListIndex
        if (allLists.size > selectedListIndex) {
            onSelectList(allLists[selectedListIndex])
        }
    }

    override fun onStart() {
        super.onStart()
        // in case things have changed while stopped
        persistence.refreshAndFetchNewLists(allLists)
        listsAdapter.notifyDataSetChanged()
        itemsAdapter.notifyDataSetChanged()
    }

    private fun setupListsRecyclerView() {
        listsRecyclerView.adapter = listsAdapter

        val layoutManager = FlexboxLayoutManager(mainActivity)
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.justifyContent = JustifyContent.CENTER
        layoutManager.flexWrap = FlexWrap.WRAP
        listsRecyclerView.layoutManager = layoutManager

        listsRecyclerView.itemAnimator = DefaultItemAnimator()

        val listCallback = ItemTouchHelperCallback(listsAdapter)
        val listTouchHelper = ItemTouchHelper(listCallback)

        listTouchHelper.attachToRecyclerView(listsRecyclerView)
    }

    private fun setupItemsRecyclerView() {
        val itemsDragDropManager = RecyclerViewDragDropManager()
        val itemsSwipeManager = RecyclerViewSwipeManager()
        var wrappedAdapter = itemsDragDropManager.createWrappedAdapter(itemsAdapter)
        wrappedAdapter = itemsSwipeManager.createWrappedAdapter(wrappedAdapter)
        itemsRecyclerView.adapter = wrappedAdapter
        itemsRecyclerView.layoutManager = LinearLayoutManager(mainActivity)

        val itemsTouchActionGuardManager = RecyclerViewTouchActionGuardManager()
        itemsTouchActionGuardManager.setInterceptVerticalScrollingWhileAnimationRunning(true)
        itemsTouchActionGuardManager.isEnabled = true

        itemsTouchActionGuardManager.attachRecyclerView(itemsRecyclerView)
        itemsSwipeManager.attachRecyclerView(itemsRecyclerView)
        itemsDragDropManager.attachRecyclerView(itemsRecyclerView)
        itemsDragDropManager.setInitiateOnLongPress(true)
        itemsDragDropManager.setInitiateOnMove(false)
        itemsDragDropManager.setDraggingItemShadowDrawable(
                ContextCompat.getDrawable(mainActivity, R.drawable.material_shadow_z3) as NinePatchDrawable?)

        val animator = DraggableItemAnimator()
        animator.supportsChangeAnimations = false
        itemsRecyclerView.itemAnimator = animator

        if(Config.smallScreen) {
            val dividerItemDecoration = DividerItemDecoration(
                itemsRecyclerView.context,
                (itemsRecyclerView.layoutManager as LinearLayoutManager).orientation
            )
            itemsRecyclerView.addItemDecoration(dividerItemDecoration)
        }
    }


    private fun switchCommentSection() {
        addCommentEditText.measure(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        val measuredHeight = addCommentEditText.measuredHeight
        val height = addCommentEditText.height
        val commentSlideAnimation = ValueAnimator.ofInt(height, measuredHeight - height).setDuration(BUTTON_ANIM_DURATION)
        commentSlideAnimation.addUpdateListener { animation ->
            addCommentEditText.layoutParams.height = animation.animatedValue as Int
            addCommentEditText.requestLayout()
            buttonClearComment.visibility = View.GONE
            if (animation.animatedValue == measuredHeight && animation.animatedValue as Int > 0) {
                addCommentEditText.layoutParams.height = RelativeLayout.LayoutParams.WRAP_CONTENT
                if (addCommentEditText.text.isNotEmpty()) buttonClearComment.visibility = View.VISIBLE
            }
        }
        commentSlideAnimation.start()

        buttonAddComment.flipX()

        val editText = if (!isAddCommentShown) addCommentEditText
        else addItemEditText
        editText.requestFocus()

        val imm = mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }

    // Lists handlers
    private fun createList(itemList: ItemList) {
        allLists.add(itemList)
        listsAdapter.notifyItemInserted(allLists.indexOf(itemList))
        onSelectList(itemList)
        persistence.apply {
            saveListAsync(itemList)
            updateListIdsTableAsync(allLists)
        }
        addItemEditText.requestFocus()
    }


    private fun editList() {
        editListDialog(mainActivity, selectedList) {
            listsAdapter.notifyItemChanged(allLists.indexOf(selectedList))
            persistence.apply {
                saveListAsync(selectedList)
                updateListIdsTableAsync(allLists)
            }
            hideEditionButtons()
        }.show()
    }

    private fun deleteList(itemList: ItemList) {
        deleteListDialog(mainActivity, itemList) { action ->
            itemList.items.clear()
            itemsAdapter.notifyDataSetChanged()
            if (action and ACTION_DELETE != 0) {
                // Remove list from app
                val position = allLists.indexOf(itemList)
                allLists.remove(itemList)
                listsAdapter.notifyItemRemoved(position)
                if (position < allLists.size) {
                    onSelectList(allLists[position])
                } else if (position > 0) {
                    onSelectList(allLists[position - 1])
                }

                if (action and ACTION_RM_FILE != 0) {
                    // Delete list file on disk, in addition to removing from the app display
                    persistence.removeListFile(itemList)
                }
            }
            persistence.updateListIdsTableAsync(allLists)
            hideEditionButtons()
        }.show()
    }

    override fun onSelectList(itemList: ItemList) {
        if (selectedList !== itemList) {
            itemsAdapter.items = itemList.items

            listsAdapter.selectList(itemList)

            itemsAdapter.notifyItemRangeRemoved(0, selectedList.items.size)
            itemsAdapter.notifyItemRangeInserted(0, itemList.items.size)

            selectedList = itemList

            persistence.selectedListIndex = allLists.indexOf(selectedList)
        }
    }

    override fun onListAdapterStartDrag() = showEditionButtons()

    override fun onListMoved(fromPosition: Int, toPosition: Int) {
        if (buttonRemoveList.alpha == 1F)
            hideEditionButtons()

        if (fromPosition < toPosition && toPosition < allLists.size) {
            for (i in fromPosition until toPosition) {
                Collections.swap(allLists, i, i + 1)
            }
            listsAdapter.notifyItemMoved(fromPosition, toPosition)
        } else if (toPosition < allLists.size) {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(allLists, i, i - 1)
            }
            listsAdapter.notifyItemMoved(fromPosition, toPosition)
        }
        persistence.updateListIdsTableAsync(allLists)
        persistence.selectedListIndex = allLists.indexOf(selectedList)
    }

    // Items handlers :
    private fun addItem() {
        // Add a new Item (task) in the current ItemList
        if (addItemEditText.text.toString().isNotEmpty()) {
            if (allLists.isEmpty()) createList(ItemList(getString(R.string.list_default_name)))
            val item = Item(addItemEditText.text.toString())
            if (isAddCommentShown)
                item.comment = addCommentEditText.text.toString()
            if (!addItemsToBottom) {
                // Add item to the top of the list
                selectedList.items.add(0, item)
            } else {
                // Add item to the bottom of the list (after done tasks too)
                selectedList.items.add(item)
            }
            // Refresh adapter and view by signalling that a new item was inserted
            val position = selectedList.items.indexOf(item)
            itemsAdapter.notifyItemInserted(position)
            // Scroll to the position of the new item
            itemsRecyclerView.smoothScrollToPosition(position)
            // Reset dialog inputs to empty
            addItemEditText.setText(R.string.empty)
            addItemEditText.requestFocus()
            if (addCommentEditText.text.isEmpty() && isAddCommentShown) {
                switchCommentSection()
            }
            addCommentEditText.setText(R.string.empty)
            // Save current list on disk, with the added Item
            persistence.saveListAsync(selectedList)
        } else listOf(addItemEditText, validate).forEach { it.shake() } // Else, there is an issue with one of the inputs, we shake the UI to show there is an issue
    }

    override fun onRemoveItem(item: Item) {
        itemsAdapter.notifyItemRemoved(selectedList.items.indexOf(item))
        selectedList.items.remove(item)
        persistence.saveListAsync(selectedList)
    }

    override fun onEditItem(item: Item) {
        editItemDialog(mainActivity, item) { updatedItem, targetList ->
            // Callback OnDoneEditing() when Item edit dialog is validated or cancelled
            // If user does not want to move Item to another list
            if (targetList == null) {
                // Update item in current list
                item.title = updatedItem.title
                item.comment = updatedItem.comment
                // Save current list state on file
                persistence.saveListAsync(selectedList)
                // Refresh current list view
                itemsAdapter.notifyItemChanged(selectedList.items.indexOf(item))
            } else { // If user wants to move Item to another list (targetList is not null)
                // Move updated item to another list
                val newPosition = when (updatedItem.done) {  // move to a different position whether item is done or not
                    true -> targetList.items.size // if done, move to list end (remember we add one task simultaneously, so no -1)
                    else -> if (!addItemsToBottom) 0 else targetList.items.size // if not done, move to top or bottom of target list depending on preferences
                }
                // Refresh current list view, we need to do that BEFORE removing the item from the data structures
                itemsAdapter.notifyItemRemoved(selectedList.items.indexOf(item))
                // Add new item to the target list, remove from current list
                targetList.items.add(newPosition, updatedItem)
                selectedList.items.remove(item)
                GlobalScope.async { // Process saving on disk and refreshing on UI in an Async coroutine, to get a more responsive UI. There is a slight risk the user is clicking so fast that the moved Item will not appear, but it's better than having this function blocking while saving on disk
                    persistence.apply {
                        // Save current lists states on disk in files
                        // This needs to be done atomically, ie, no Async between commands, otherwise we may end up with running concurrency issues, eg, deleting the item but not saved in the other list, or refreshing before the item was moved or partially moved
                        saveList(targetList) // this needs to be blocking, otherwise we cannot refreshList(), so this slows down UI and feels less responsive. This is circumvented by doing this in an Async coroutine.
                        saveList(selectedList) // update current list, from which we remove the Item, after having saved in the other list, to ensure we have a copy of the Item somewhere in case of a crash. The risk is that if user refresh current list fast enough, they may see a ghost of the Item.
                        // Refresh/reload all lists content (otherwise the moved updatedItem will not show up in the other list)
                        //refreshAllLists(allLists)  // refresh all lists content of items, but this is slow
                        refreshList(allLists, targetList.stableId) // refresh only target list, not current list because we already refreshed the view
                    }
                }
                // Force refresh adapters
                // Same as swipe refresh and onStart() resuming
                listsAdapter.notifyDataSetChanged()
                itemsAdapter.notifyDataSetChanged()
            }
        }.show()
    }

    override fun onSwitchItemStatus(item: Item) {
        // When an item is clicked and we need to mark it as done or undone
        item.done = !item.done
        if (doneItemsToBottom) {
            // Move item to the bottom of the list if done, or top if undone
            val oldPosition = selectedList.items.indexOf(item)
            val newPosition = when (item.done) {
                true -> selectedList.items.size - 1
                else -> 0
            }
            selectedList.items.removeAt(oldPosition)
            selectedList.items.add(newPosition, item)
            // Force refresh view
            itemsAdapter.notifyItemChanged(oldPosition)
            itemsAdapter.notifyItemMoved(oldPosition, newPosition)
            // Scroll to top
            val scrolledToTop = (itemsRecyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition() == 0
            if (scrolledToTop || oldPosition == 0) itemsRecyclerView.scrollToPosition(0)
        } else {
            // Leave item in place when done or undone, just update view
            itemsAdapter.notifyItemChanged(selectedList.items.indexOf(item))
        }
        // Save new Item state on-disk
        persistence.saveListAsync(selectedList)
    }

    override fun onShowOrHideComment(item: Item) {
        item.commentDisplayed = !item.commentDisplayed
        itemsAdapter.notifyItemChanged(selectedList.items.indexOf(item))
        persistence.saveListAsync(selectedList)
    }

    override fun onMoveItem(fromPosition: Int, toPosition: Int) {
        val fromItem = selectedList.items[fromPosition]
        selectedList.items.removeAt(fromPosition)
        selectedList.items.add(toPosition, fromItem)
        persistence.saveListAsync(selectedList)
    }

    // hide keyboard when touch outside an EditText
    override fun onDispatchTouchEvent(ev: MotionEvent) {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val addItemRect = Rect()
            val addCommentRect = Rect()
            val showCommentRect = Rect()
            addItemEditText.getGlobalVisibleRect(addItemRect)
            addCommentEditText.getGlobalVisibleRect(addCommentRect)
            buttonAddComment.getGlobalVisibleRect(showCommentRect)
            val view = mainActivity.currentFocus
            val rawX = ev.rawX.toInt()
            val rawY = ev.rawY.toInt()
            if (view != null && view is EditText) {
                val r = Rect()
                view.getGlobalVisibleRect(r)
                if (!addItemRect.contains(rawX, rawY) && !addCommentRect.contains(rawX, rawY) && !showCommentRect.contains(rawX, rawY)) {
                    view.clearFocus()
                    val inputMethodManager = App.instance.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(rootView.windowToken, 0)
                }
            }

            val rectButtons = Rect()
            buttonsLayout.getGlobalVisibleRect(rectButtons)

            if (buttonRemoveList.alpha == 1F && !rectButtons.contains(rawX, rawY))
                hideEditionButtons()
        }
    }

    private fun showEditionButtons() {
        buttonShareList.visibility = View.GONE
        buttonShareAllLists.visibility = View.GONE
        buttonRemoveList.animShowFlip()
        buttonAddList.animHideFlip(startDelay = BUTTON_ANIM_DURATION)
        buttonEditList.animShowFlip()
        buttonEditList.animTranslation(stopX = dpToPx(-32).toFloat(), startDelay = BUTTON_ANIM_DURATION)
    }

    private fun hideEditionButtons() {
        buttonShareList.visibility = View.VISIBLE
        buttonShareAllLists.visibility = View.VISIBLE
        buttonAddList.animShowFlip()
        buttonRemoveList.animHideFlip(startDelay = BUTTON_ANIM_DURATION)
        buttonEditList.animHideFlip()
        buttonEditList.animTranslation(dpToPx(-32).toFloat())
    }
}