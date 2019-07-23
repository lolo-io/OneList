package com.lolo.io.onelist

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.NinePatchDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.h6ah4i.android.widget.advrecyclerview.animator.DraggableItemAnimator
import com.h6ah4i.android.widget.advrecyclerview.decoration.SimpleListDividerDecorator
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager
import com.h6ah4i.android.widget.advrecyclerview.touchguard.RecyclerViewTouchActionGuardManager
import kotlinx.android.synthetic.main.fragment_one_list.*
import java.util.*

class OneListFragment : Fragment(), ListsCallbacks, ItemsCallbacks, MainActivity.OnDispatchTouchEvent {

    private val mainActivity: MainActivity
        get() {
            if (activity is MainActivity) return activity as MainActivity
            else throw IllegalStateException("Activity must be MainActivity")
        }

    private val allLists: MutableList<ItemList> = arrayListOf()
    private var listsAdapter: ListsAdapterKt = ListsAdapterKt(allLists, this)
    private val itemsAdapter = ItemsAdapterKt(this)

    private var selectedList: ItemList = ItemList("")

    private lateinit var prefs: SharedPreferencesHelper

    private val itemsSwipeManager: RecyclerViewSwipeManager = RecyclerViewSwipeManager()
    private val itemsDragDropManager: RecyclerViewDragDropManager = RecyclerViewDragDropManager()

    private lateinit var dialogs: DialogsKt

    private val isAddCommentShown
        get() = addCommentEditText.height > 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = SharedPreferencesHelper(mainActivity)
        dialogs = DialogsKt(mainActivity)

        if (prefs.firstLaunch && activity != null) {
            prefs.allLists = Gson().fromJson(loadJSONFromAsset(activity!!, getString(R.string.prefix) + "-tuto.json"), object : TypeToken<List<ItemList>>() {
            }.type)
            prefs.firstLaunch = false
        }

    }

    @SuppressLint("InflateParams")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_one_list, null)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        addItemEditText.setOnEditorActionListener { _, actionId, _ -> if (actionId == EditorInfo.IME_ACTION_DONE) addItem(); true }
        validate.setOnClickListener { addItem() }
        buttonAddList.setOnClickListener { dialogs.promptAddOrEditList { title -> createList(ItemList(title)) }.show() }
        buttonEditList.setOnClickListener { editList() }
        buttonRemoveList.setOnClickListener { deleteList(selectedList) }
        buttonAddComment.setOnClickListener { switchCommentSection() }
        buttonClearComment.setOnClickListener { addCommentEditText.text.clear() }

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

        initLists()
    }

    private fun initLists() {
        setupListsRecyclerView()
        setupItemsRecyclerView()
        if (allLists.isEmpty()) {
            allLists.addAll(prefs.allLists)
        }
        val selectedListIndex = prefs.selectedListIndex
        if (allLists.size > selectedListIndex) {
            onSelectList(allLists[selectedListIndex])
        }
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

        itemsRecyclerView.addItemDecoration(SimpleListDividerDecorator(ContextCompat.getDrawable(mainActivity, R.drawable.list_divider_h), true))
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
        prefs.allLists = allLists
        addItemEditText.requestFocus()
    }

    private fun editList() {
        dialogs.promptAddOrEditList(selectedList.title) { title ->
            selectedList.title = title
            listsAdapter.notifyItemChanged(allLists.indexOf(selectedList))
            prefs.allLists = allLists
            hideEditionButtons()
        }.show()
        prefs.allLists = allLists
    }

    private fun deleteList(itemList: ItemList) {
        dialogs.promptDeleteList(itemList) { action ->
            itemList.items.clear()
            itemsAdapter.notifyDataSetChanged()
            if (action == DialogsKt.ACTION_DELETE) {
                val position = allLists.indexOf(itemList)
                allLists.remove(itemList)
                listsAdapter.notifyItemRemoved(position)
                if (position < allLists.size) {
                    onSelectList(allLists[position])
                } else if (position > 0) {
                    onSelectList(allLists[position - 1])
                }
            }
            prefs.allLists = allLists
            hideEditionButtons()
        }.show()
    }

    override fun onSelectList(itemList: ItemList) {
        if (selectedList != itemList) {
            itemsAdapter.items = itemList.items

            listsAdapter.selectList(itemList)

            itemsAdapter.notifyItemRangeRemoved(0, selectedList.items.size)
            itemsAdapter.notifyItemRangeInserted(0, itemList.items.size)

            selectedList = itemList

            prefs.selectedListIndex = allLists.indexOf(selectedList)
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
        prefs.allLists = allLists
        prefs.selectedListIndex = allLists.indexOf(selectedList)
    }

    // Items handlers :
    private fun addItem() {
        if (addItemEditText.text.toString().isNotEmpty()) {
            if (allLists.isEmpty()) createList(ItemList(getString(R.string.list_default_name)))
            val item = Item(addItemEditText.text.toString())
            if (isAddCommentShown)
                item.comment = addCommentEditText.text.toString()
            selectedList.items.add(0, item)
            val position = selectedList.items.indexOf(item)
            itemsAdapter.notifyItemInserted(position)
            itemsRecyclerView.smoothScrollToPosition(0)
            addItemEditText.setText(R.string.empty)
            addItemEditText.requestFocus()
            if (addCommentEditText.text.isEmpty() && isAddCommentShown) {
                switchCommentSection()
            }

            addCommentEditText.setText(R.string.empty)
            prefs.allLists = allLists
        } else listOf(addItemEditText, validate).forEach { it.shake() }
    }

    override fun onRemoveItem(item: Item) {
        itemsAdapter.notifyItemRemoved(selectedList.items.indexOf(item))
        selectedList.items.remove(item)
        prefs.allLists = allLists
    }

    override fun onEditItem(item: Item) {
        dialogs.promptEditItem(item) { updatedItem ->
            item.title = updatedItem.title
            item.comment = updatedItem.comment
            prefs.allLists = allLists
            itemsAdapter.notifyItemChanged(selectedList.items.indexOf(item))
        }.show()
    }

    override fun onSwitchItemStatus(item: Item) {
        item.done = !item.done
        val oldPosition = selectedList.items.indexOf(item)
        val newPosition = when (item.done) {
            true -> selectedList.items.size - 1
            else -> 0
        }
        selectedList.items.removeAt(oldPosition)
        selectedList.items.add(newPosition, item)
        itemsAdapter.notifyItemChanged(oldPosition)
        itemsAdapter.notifyItemMoved(oldPosition, newPosition)
        val scrolledToTop = (itemsRecyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition() == 0
        if (scrolledToTop || oldPosition == 0) itemsRecyclerView.scrollToPosition(0)
        prefs.allLists = allLists
    }

    override fun onShowOrHideComment(item: Item) {
        item.commentDisplayed = !item.commentDisplayed
        itemsAdapter.notifyItemChanged(selectedList.items.indexOf(item))
        prefs.allLists = allLists
    }

    override fun onMoveItem(fromPosition: Int, toPosition: Int) {
        val fromItem = selectedList.items[fromPosition]
        selectedList.items.removeAt(fromPosition)
        selectedList.items.add(toPosition, fromItem)
        prefs.allLists = allLists
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
        buttonRemoveList.animShowFlip()
        buttonAddList.animHideFlip(startDelay = BUTTON_ANIM_DURATION)
        buttonEditList.animShowFlip()
        buttonEditList.animTranslation(stopX = dpToPx(-32).toFloat(), startDelay = BUTTON_ANIM_DURATION)
    }

    private fun hideEditionButtons() {
        buttonAddList.animShowFlip()
        buttonRemoveList.animHideFlip(startDelay = BUTTON_ANIM_DURATION)
        buttonEditList.animHideFlip()
        buttonEditList.animTranslation(dpToPx(-32).toFloat())
    }
}