package com.lolo.io.onelist

import android.annotation.SuppressLint
import android.graphics.Paint
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemAdapter
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionDefault
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionMoveToSwipedDirection
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableSwipeableItemViewHolder
import com.lolo.io.onelist.model.Item
import com.lolo.io.onelist.updates.appContext
import com.lolo.io.onelist.util.ifVisible
import kotlinx.android.synthetic.main.list_item.view.*

class ItemsAdapter(val callback: ItemsCallbacks) : RecyclerView.Adapter<ItemsAdapter.ItemViewHolder>(),
        SwipeableItemAdapter<ItemsAdapter.ItemViewHolder>,
        DraggableItemAdapter<ItemsAdapter.ItemViewHolder> {

    var items: MutableList<Item> = arrayListOf()

    init {
        setHasStableIds(true)
    }

    var doneItemsToBottom: Boolean
        get() {
            val sp = PreferenceManager.getDefaultSharedPreferences(appContext)
            return sp.getBoolean("doneItemsToBottom", true) ?: true
        }
        set(value) {
            val sp = PreferenceManager.getDefaultSharedPreferences(appContext)
            val editor = sp.edit()
            editor.putBoolean("doneItemsToBottom", value)
            editor.apply()
        }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(App.instance.mainContext.resources.getLayout(R.layout.list_item), parent, false)
        return ItemViewHolder(v).apply {
            view.badge.isVisible = !Config.smallScreen
        }
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.view.text.text = item.title
        holder.itemView.tag = holder
        holder.view.expandImg.visibility = View.GONE

        holder.view.expandImg.visibility = View.GONE
        holder.view.comment.visibility = View.GONE
        if (item.comment.isNotEmpty()) {
            holder.view.expandImg.visibility = View.VISIBLE
            holder.view.comment.text = item.comment
            if (item.commentDisplayed) {
                holder.view.comment.visibility = View.VISIBLE
                holder.view.expandImg.rotationX = 180F
            } else holder.view.expandImg.rotationX = 0f
        }

        if (item.done) strike(holder) else unStrike(holder)
    }

    private fun strike(viewHolder: ItemViewHolder) {
        viewHolder.view.badge.ifVisible()?.setImageDrawable(ContextCompat.getDrawable(App.instance.mainContext, R.drawable.ic_bullet_outline_checked))
        viewHolder.view.text.setTextColor(ContextCompat.getColor(App.instance.mainContext, R.color.colorAccentLight))
        viewHolder.view.text.paintFlags = viewHolder.view.text.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        viewHolder.view.comment.setTextColor(ContextCompat.getColor(App.instance.mainContext, R.color.colorAccentLight))
        viewHolder.view.comment.paintFlags = viewHolder.view.text.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        viewHolder.view.expandImg.drawable.setColorFilter(ContextCompat.getColor(App.instance.mainContext, R.color.colorAccentLight), PorterDuff.Mode.SRC_ATOP)
    }


    private fun unStrike(viewHolder: ItemViewHolder) {
        viewHolder.view.badge.ifVisible()?.setImageDrawable(ContextCompat.getDrawable(App.instance.mainContext, R.drawable.ic_bullet_outline))
        viewHolder.view.text.setTextColor(ContextCompat.getColor(App.instance.mainContext, R.color.textColorPrimary))
        Paint().let {
            viewHolder.view.text.paintFlags = it.flags
            viewHolder.view.comment.paintFlags = it.flags
        }
        viewHolder.view.comment.setTextColor(ContextCompat.getColor(App.instance.mainContext, R.color.colorAccent))
        viewHolder.view.expandImg.drawable.setColorFilter(ContextCompat.getColor(App.instance.mainContext, R.color.colorAccentDark), PorterDuff.Mode.SRC_ATOP)
    }

    override fun getItemId(position: Int): Long {
        return items[position].stableId
    }

    @SuppressLint("SwitchIntDef")
    override fun onSwipeItem(holder: ItemViewHolder, position: Int, result: Int): com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultAction {
        val item = touchedItems[0]
        touchedItems.removeAt(0)
        return when (result) {
            SwipeableItemConstants.RESULT_SWIPED_RIGHT -> SwipeResultAction { holder.swipeItemHorizontalSlideAmount = 0F; callback.onEditItem(item) }
            SwipeableItemConstants.RESULT_SWIPED_LEFT -> SwipeResultAction { callback.onRemoveItem(item) }
            else -> SwipeResultActionCanceled { onSetSwipeBackground(holder, items.indexOf(item), SwipeableItemConstants.DRAWABLE_SWIPE_NEUTRAL_BACKGROUND) }
        }
    }

    override fun onGetSwipeReactionType(holder: ItemViewHolder, position: Int, x: Int, y: Int): Int {
        return SwipeableItemConstants.REACTION_CAN_SWIPE_BOTH_H
    }

    private var touchedItems = arrayListOf<Item>()
    override fun onSwipeItemStarted(holder: ItemViewHolder, position: Int) {
        holder.view.leftBar.visibility = View.VISIBLE
        holder.view.rightBar.visibility = View.VISIBLE
        touchedItems.add(items[position])
    }

    @SuppressLint("SwitchIntDef")
    override fun onSetSwipeBackground(holder: ItemViewHolder, position: Int, type: Int) {
        var bgRes = 0
        when (type) {
            SwipeableItemConstants.DRAWABLE_SWIPE_NEUTRAL_BACKGROUND -> {
                bgRes = 0
                holder.view.leftBar.visibility = View.GONE
                holder.view.rightBar.visibility = View.GONE
            }
            SwipeableItemConstants.DRAWABLE_SWIPE_LEFT_BACKGROUND -> {
                bgRes = R.drawable.bg_swipe_left
                holder.view.editIcon.visibility = View.INVISIBLE
                holder.view.deleteIcon.visibility = View.VISIBLE
            }
            SwipeableItemConstants.DRAWABLE_SWIPE_RIGHT_BACKGROUND -> {
                bgRes = R.drawable.bg_swipe_right
                holder.view.editIcon.visibility = View.VISIBLE
                holder.view.deleteIcon.visibility = View.INVISIBLE
            }
        }
        holder.itemView.setBackgroundResource(bgRes)
    }

    // Drag :
    override fun onGetItemDraggableRange(holder: ItemViewHolder, position: Int): ItemDraggableRange? {
        // Define where we can move items across the list
        val nbDone = items.count { it.done } // get the count of done tasks
        val doneLimit = itemCount - nbDone - 1 // calculate where in the list is the separation between done tasks and not done tasks
        val idr : ItemDraggableRange =
            if (!doneItemsToBottom // if items are left in place when done (expected behavior)
                    || (!items[position].done && position > doneLimit) // or a not done item is among done items (quick fix to avoid a crash, such as when user used !doneItemsBottom but now re-enabled the option again)
                    || items[position].done && position <= doneLimit) // or a done item is among not done items (quick fix to avoid a crash, such as when user used !doneItemsBottom but now re-enabled the option again)
            {
                // then we allow the item to be draggable across the entire list
                ItemDraggableRange(0, itemCount - 1)
            } else {
                // Else the item is either done or not done, we only allow it to be moved in the range of done or not done tasks respectively
                if (items[position].done) ItemDraggableRange(doneLimit + 1, itemCount - 1) // for done item, we can move from where done tasks starts up to the end of the list
                else ItemDraggableRange(0, doneLimit) // for not done tasks, we can only move from the start of the list up to where done tasks start
            }
        return idr
    }

    override fun onCheckCanStartDrag(holder: ItemViewHolder, position: Int, x: Int, y: Int): Boolean = x > 0 && y > 0 && holder.swipeableContainerView.translationX == 0f


    override fun onItemDragStarted(position: Int) {
        notifyItemChanged(position)
    }

    override fun onMoveItem(fromPosition: Int, toPosition: Int) {
        callback.onMoveItem(fromPosition, toPosition)
    }

    override fun onCheckCanDrop(draggingPosition: Int, dropPosition: Int): Boolean = true

    override fun onItemDragFinished(fromPosition: Int, toPosition: Int, result: Boolean) {
        notifyDataSetChanged()
    }

    inner class ItemViewHolder(val view: View) : AbstractDraggableSwipeableItemViewHolder(view) {

        init {
            view.setOnClickListener { callback.onSwitchItemStatus(items[layoutPosition]) }
            view.expandImg.setOnClickListener { callback.onShowOrHideComment(items[layoutPosition]) }
        }

        override fun getSwipeableContainerView(): View {
            return view.containerView
        }
    }

    private class SwipeResultAction(private val onSlideEnd: () -> Any?) : SwipeResultActionMoveToSwipedDirection() {
        override fun onSlideAnimationEnd() {
            super.onSlideAnimationEnd()
            onSlideEnd()
        }
    }

    private class SwipeResultActionCanceled(private val onSlideEnd: () -> Any?) : SwipeResultActionDefault() {
        override fun onSlideAnimationEnd() {
            super.onSlideAnimationEnd()
            onSlideEnd()
        }
    }
}