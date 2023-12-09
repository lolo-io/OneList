package com.lolo.io.onelist

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Configuration
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
import com.lolo.io.onelist.databinding.FragmentOneListBinding
import com.lolo.io.onelist.dialogs.*
import com.lolo.io.onelist.model.Item
import com.lolo.io.onelist.model.ItemList
import com.lolo.io.onelist.updates.UpdateHelper
import com.lolo.io.onelist.util.*
import com.skydoves.powermenu.MenuAnimation
import com.skydoves.powermenu.PowerMenu
import com.skydoves.powermenu.PowerMenuItem
import com.skydoves.powermenu.kotlin.createPowerMenu
import java.util.*
import androidx.recyclerview.widget.DividerItemDecoration




class OneListFragment : Fragment(), ListsCallbacks, ItemsCallbacks, MainActivity.OnDispatchTouchEvent {

    private var _binding : FragmentOneListBinding? = null
    private val binding : FragmentOneListBinding
        get() = _binding!!


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
        get() = binding.addCommentEditText.height > 0

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
        _binding = FragmentOneListBinding.inflate(inflater, container, false)
        this.container = container
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        binding.addItemEditText.setOnEditorActionListener { _, actionId, _ -> if (actionId == EditorInfo.IME_ACTION_DONE) addItem(); true }
        binding.validate.setOnClickListener { addItem() }
        binding.buttonAddList.setOnClickListener { editListDialog(mainActivity) { list -> createList(list) }.show() }
        binding.buttonEditList.setOnClickListener { editList() }
        binding.buttonRemoveList.setOnClickListener { deleteList(selectedList) }
        binding.buttonAddComment.setOnClickListener { switchCommentSection() }
        binding.buttonClearComment.setOnClickListener { binding.addCommentEditText.text.clear() }
        binding.buttonShareList.setOnClickListener { persistence.shareList(selectedList) }

        binding.menuArrow.setOnTouchListener { v, e ->
            if (e.action == MotionEvent.ACTION_DOWN) {
                if (popupMenu?.isShowing == false)
                    popupMenu?.showAsAnchorLeftTop(v, dpToPx(12), dpToPx(12))
                else popupMenu?.dismiss()
            }
            true
        }

        binding.swipeContainer.setOnRefreshListener {
            persistence.refreshAllLists(allLists)
            listsAdapter.notifyDataSetChanged()
            itemsAdapter.notifyDataSetChanged()
            binding.swipeContainer.isRefreshing = false
        }

        binding.validate.visibility = View.INVISIBLE
        binding.buttonAddComment.visibility = View.INVISIBLE
        binding.addItemEditText.afterTextChanged {
            if (it.isNotEmpty()) {
                binding.validate.visibility = View.VISIBLE
                binding.buttonAddComment.visibility = View.VISIBLE
            } else {
                binding.validate.visibility = View.INVISIBLE
                if (!isAddCommentShown) binding.buttonAddComment.visibility = View.INVISIBLE
            }
        }

        binding.addCommentEditText.afterTextChanged {
            if (it.isNotEmpty()) binding.buttonClearComment.visibility = View.VISIBLE
            else binding.buttonClearComment.visibility = View.GONE
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

        binding.listsRecyclerView.adapter = listsAdapter

        val layoutManager = FlexboxLayoutManager(mainActivity)
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
        binding.itemsRecyclerView.layoutManager = LinearLayoutManager(mainActivity)

        val itemsTouchActionGuardManager = RecyclerViewTouchActionGuardManager()
        itemsTouchActionGuardManager.setInterceptVerticalScrollingWhileAnimationRunning(true)
        itemsTouchActionGuardManager.isEnabled = true

        itemsTouchActionGuardManager.attachRecyclerView(binding.itemsRecyclerView)
        itemsSwipeManager.attachRecyclerView(binding.itemsRecyclerView)
        itemsDragDropManager.attachRecyclerView(binding.itemsRecyclerView)
        itemsDragDropManager.setInitiateOnLongPress(true)
        itemsDragDropManager.setInitiateOnMove(false)
        itemsDragDropManager.setDraggingItemShadowDrawable(
                ContextCompat.getDrawable(mainActivity, R.drawable.material_shadow_z3) as NinePatchDrawable?)

        val animator = DraggableItemAnimator()
        animator.supportsChangeAnimations = false
        binding.itemsRecyclerView.itemAnimator = animator

        if(Config.smallScreen) {
            val dividerItemDecoration = DividerItemDecoration(
                binding.itemsRecyclerView.context,
                (binding.itemsRecyclerView.layoutManager as LinearLayoutManager).orientation
            )
            binding.itemsRecyclerView.addItemDecoration(dividerItemDecoration)
        }
    }


    private fun switchCommentSection() {
        binding.addCommentEditText.measure(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        val measuredHeight = binding.addCommentEditText.measuredHeight
        val height = binding.addCommentEditText.height
        val commentSlideAnimation = ValueAnimator.ofInt(height, measuredHeight - height).setDuration(BUTTON_ANIM_DURATION)
        commentSlideAnimation.addUpdateListener { animation ->
            binding.addCommentEditText.layoutParams.height = animation.animatedValue as Int
            binding.addCommentEditText.requestLayout()
            binding.buttonClearComment.visibility = View.GONE
            if (animation.animatedValue == measuredHeight && animation.animatedValue as Int > 0) {
                binding.addCommentEditText.layoutParams.height = RelativeLayout.LayoutParams.WRAP_CONTENT
                if (binding.addCommentEditText.text.isNotEmpty()) binding.buttonClearComment.visibility = View.VISIBLE
            }
        }
        commentSlideAnimation.start()

        binding.buttonAddComment.flipX()

        val editText = if (!isAddCommentShown) binding.addCommentEditText
        else binding.addItemEditText
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
        binding.addItemEditText.requestFocus()
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
                val position = allLists.indexOf(itemList)
                allLists.remove(itemList)
                listsAdapter.notifyItemRemoved(position)
                if (position < allLists.size) {
                    onSelectList(allLists[position])
                } else if (position > 0) {
                    onSelectList(allLists[position - 1])
                }

                if (action and ACTION_RM_FILE != 0) {
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
        if (binding.buttonRemoveList.alpha == 1F)
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
        if (binding.addItemEditText.text.toString().isNotEmpty()) {
            if (allLists.isEmpty()) createList(ItemList(getString(R.string.list_default_name)))
            val item = Item(binding.addItemEditText.text.toString())
            if (isAddCommentShown)
                item.comment = binding.addCommentEditText.text.toString()
            selectedList.items.add(0, item)
            val position = selectedList.items.indexOf(item)
            itemsAdapter.notifyItemInserted(position)
            binding.itemsRecyclerView.smoothScrollToPosition(0)
            binding.addItemEditText.setText(R.string.empty)
            binding.addItemEditText.requestFocus()
            if (binding.addCommentEditText.text.isEmpty() && isAddCommentShown) {
                switchCommentSection()
            }

            binding.addCommentEditText.setText(R.string.empty)
            persistence.saveListAsync(selectedList)
        } else listOf(binding.addItemEditText, binding.validate).forEach { it.shake() }
    }

    override fun onRemoveItem(item: Item) {
        itemsAdapter.notifyItemRemoved(selectedList.items.indexOf(item))
        selectedList.items.remove(item)
        persistence.saveListAsync(selectedList)
    }

    override fun onEditItem(item: Item) {
        editItemDialog(mainActivity, item) { updatedItem ->
            item.title = updatedItem.title
            item.comment = updatedItem.comment
            persistence.saveListAsync(selectedList)
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
        val scrolledToTop = (binding.itemsRecyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition() == 0
        if (scrolledToTop || oldPosition == 0) binding.itemsRecyclerView.scrollToPosition(0)
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
            binding.addItemEditText.getGlobalVisibleRect(addItemRect)
            binding.addCommentEditText.getGlobalVisibleRect(addCommentRect)
            binding.buttonAddComment.getGlobalVisibleRect(showCommentRect)
            val view = mainActivity.currentFocus
            val rawX = ev.rawX.toInt()
            val rawY = ev.rawY.toInt()
            if (view != null && view is EditText) {
                val r = Rect()
                view.getGlobalVisibleRect(r)
                if (!addItemRect.contains(rawX, rawY) && !addCommentRect.contains(rawX, rawY) && !showCommentRect.contains(rawX, rawY)) {
                    view.clearFocus()
                    val inputMethodManager = App.instance.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(binding.rootView.windowToken, 0)
                }
            }

            val rectButtons = Rect()
            binding.buttonsLayout.getGlobalVisibleRect(rectButtons)

            if (binding.buttonRemoveList.alpha == 1F && !rectButtons.contains(rawX, rawY))
                hideEditionButtons()
        }
    }

    private fun showEditionButtons() {
        binding.buttonShareList.visibility = View.GONE
        binding.buttonRemoveList.animShowFlip()
        binding.buttonAddList.animHideFlip(startDelay = BUTTON_ANIM_DURATION)
        binding.buttonEditList.animShowFlip()
        binding.buttonEditList.animTranslation(stopX = dpToPx(-32).toFloat(), startDelay = BUTTON_ANIM_DURATION)
    }

    private fun hideEditionButtons() {
        binding.buttonShareList.visibility = View.VISIBLE
        binding.buttonAddList.animShowFlip()
        binding.buttonRemoveList.animHideFlip(startDelay = BUTTON_ANIM_DURATION)
        binding.buttonEditList.animHideFlip()
        binding.buttonEditList.animTranslation(dpToPx(-32).toFloat())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}