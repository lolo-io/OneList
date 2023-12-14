package com.lolo.io.onelist.feature.lists.lists_adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemAdapter
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionDefault
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionMoveToSwipedDirection
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableSwipeableItemViewHolder
import com.lolo.io.onelist.App
import com.lolo.io.onelist.R
import com.lolo.io.onelist.core.model.Item
import com.lolo.io.onelist.core.ui.Config
import com.lolo.io.onelist.core.ui.util.ifVisible
import com.lolo.io.onelist.databinding.ListItemBinding

class ItemsAdapter(
    val context: Context,
    val callback: ItemsCallbacks) : RecyclerView.Adapter<ItemsAdapter.ItemViewHolder>(),
        SwipeableItemAdapter<ItemsAdapter.ItemViewHolder>,
        DraggableItemAdapter<ItemsAdapter.ItemViewHolder> {


    var items: MutableList<Item> = arrayListOf()

    init {
        setHasStableIds(true)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false).apply {
            badge.isVisible = !Config.smallScreen
        }
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.binding.text.text = item.title
        holder.itemView.tag = holder
        holder.binding.expandImg.visibility = View.GONE

        holder.binding.expandImg.visibility = View.GONE
        holder.binding.comment.visibility = View.GONE
        if (item.comment.isNotEmpty()) {
            holder.binding.expandImg.visibility = View.VISIBLE
            holder.binding.comment.text = item.comment
            if (item.commentDisplayed) {
                holder.binding.comment.visibility = View.VISIBLE
                holder.binding.expandImg.rotationX = 180F
            } else holder.binding.expandImg.rotationX = 0f
        }

        if (item.done) strike(holder) else unStrike(holder)
    }

    private fun strike(viewHolder: ItemViewHolder) {
        viewHolder.binding.badge.ifVisible()?.setImageDrawable(ContextCompat.getDrawable(
            context,
            R.drawable.ic_bullet_outline_checked
        ))
        viewHolder.binding.text.setTextColor(ContextCompat.getColor(
            context,
            R.color.colorAccentLight
        ))
        viewHolder.binding.text.paintFlags = viewHolder.binding.text.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        viewHolder.binding.comment.setTextColor(ContextCompat.getColor(
            context,
            R.color.colorAccentLight
        ))
        viewHolder.binding.comment.paintFlags = viewHolder.binding.text.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        viewHolder.binding.expandImg.drawable.setColorFilter(ContextCompat.getColor(
            context,
            R.color.colorAccentLight
        ), PorterDuff.Mode.SRC_ATOP)
    }


    private fun unStrike(viewHolder: ItemViewHolder) {
        viewHolder.binding.badge.ifVisible()?.setImageDrawable(ContextCompat.getDrawable(
            context,
            R.drawable.ic_bullet_outline
        ))
        viewHolder.binding.text.setTextColor(ContextCompat.getColor(
            context,
            R.color.textColorPrimary
        ))
        Paint().let {
            viewHolder.binding.text.paintFlags = it.flags
            viewHolder.binding.comment.paintFlags = it.flags
        }
        viewHolder.binding.comment.setTextColor(ContextCompat.getColor(
            context,
            R.color.colorAccent
        ))
        viewHolder.binding.expandImg.drawable.setColorFilter(ContextCompat.getColor(
            context,
            R.color.colorAccentDark
        ), PorterDuff.Mode.SRC_ATOP)
    }

    override fun getItemId(position: Int): Long {
        return items[position].id
    }

    @SuppressLint("SwitchIntDef")
    override fun onSwipeItem(holder: ItemViewHolder, position: Int, result: Int): com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultAction {
        val item = touchedItems[0]
        touchedItems.removeAt(0)
        return when (result) {
            SwipeableItemConstants.RESULT_SWIPED_RIGHT -> SwipeResultAction { holder.swipeItemHorizontalSlideAmount = 0F; callback.openEditItemDialog(position) }
            SwipeableItemConstants.RESULT_SWIPED_LEFT -> SwipeResultAction { callback.onRemoveItem(item) }
            else -> SwipeResultActionCanceled { onSetSwipeBackground(holder, items.indexOf(item), SwipeableItemConstants.DRAWABLE_SWIPE_NEUTRAL_BACKGROUND) }
        }
    }

    override fun onGetSwipeReactionType(holder: ItemViewHolder, position: Int, x: Int, y: Int): Int {
        return SwipeableItemConstants.REACTION_CAN_SWIPE_BOTH_H
    }

    private var touchedItems = arrayListOf<Item>()
    override fun onSwipeItemStarted(holder: ItemViewHolder, position: Int) {
        holder.binding.leftBar.visibility = View.VISIBLE
        holder.binding.rightBar.visibility = View.VISIBLE
        touchedItems.add(items[position])
    }

    @SuppressLint("SwitchIntDef")
    override fun onSetSwipeBackground(holder: ItemViewHolder, position: Int, type: Int) {
        var bgRes = 0
        when (type) {
            SwipeableItemConstants.DRAWABLE_SWIPE_NEUTRAL_BACKGROUND -> {
                bgRes = 0
                holder.binding.leftBar.visibility = View.GONE
                holder.binding.rightBar.visibility = View.GONE
            }
            SwipeableItemConstants.DRAWABLE_SWIPE_LEFT_BACKGROUND -> {
                bgRes = R.drawable.bg_swipe_left
                holder.binding.editIcon.visibility = View.INVISIBLE
                holder.binding.deleteIcon.visibility = View.VISIBLE
            }
            SwipeableItemConstants.DRAWABLE_SWIPE_RIGHT_BACKGROUND -> {
                bgRes = R.drawable.bg_swipe_right
                holder.binding.editIcon.visibility = View.VISIBLE
                holder.binding.deleteIcon.visibility = View.INVISIBLE
            }
        }
        holder.itemView.setBackgroundResource(bgRes)
    }

    // Drag :
    override fun onGetItemDraggableRange(holder: ItemViewHolder, position: Int): ItemDraggableRange? {
        val nbDone = items.count { it.done }
        return if (items[position].done) ItemDraggableRange(itemCount - nbDone, itemCount - 1)
        else ItemDraggableRange(0, itemCount - nbDone - 1)
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

    inner class ItemViewHolder(val binding: ListItemBinding) : AbstractDraggableSwipeableItemViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener { callback.onSwitchItemStatus(items[layoutPosition]) }
            binding.expandImg.setOnClickListener { callback.onShowOrHideComment(items[layoutPosition]) }
        }

        override fun getSwipeableContainerView(): View {
            return binding.containerView
        }
    }

    private class SwipeResultAction(private val onSlideEnd: () -> Any?) : SwipeResultActionMoveToSwipedDirection() {
        override fun onSlideAnimationEnd() {
            super.onSlideAnimationEnd()
            onSlideEnd()
        }

        override fun onCleanUp() {
            super.onCleanUp()
        }
    }

    private class SwipeResultActionCanceled(private val onSlideEnd: () -> Any?) : SwipeResultActionDefault() {
        override fun onSlideAnimationEnd() {
            super.onSlideAnimationEnd()
            onSlideEnd()
        }
    }
}