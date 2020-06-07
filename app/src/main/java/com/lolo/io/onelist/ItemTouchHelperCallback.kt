package com.lolo.io.onelist

import android.animation.ObjectAnimator
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
import android.view.MotionEvent
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.lolo.io.onelist.util.dpToPx

class ItemTouchHelperCallback(private val adapter: ItemTouchHelperAdapter) : ItemTouchHelper.Callback(), RecyclerView.OnItemTouchListener {

    private var dragging = false

    override fun isLongPressDragEnabled(): Boolean {
        return true
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return false
    }

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        return makeMovementFlags(dragFlags, 0)
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            adapter.onStartDrag(viewHolder!!)
        }
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        adapter.onItemMoved(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        val view = viewHolder.itemView
        if (isCurrentlyActive) {

            val p = Paint()
            p.color = ContextCompat.getColor(App.instance.mainContext, R.color.colorAccentLight)
            p.style = Paint.Style.STROKE
            p.alpha = 127

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                c.drawRoundRect(view.left.toFloat(), view.top.toFloat(), view.right.toFloat(), view.bottom.toFloat(), dpToPx(5).toFloat(), dpToPx(5).toFloat(), p)
            } else {
                c.drawRect(view.left.toFloat(), view.top.toFloat(), view.right.toFloat(), view.bottom.toFloat(), p)
            }
            viewHolder.itemView.setBackgroundResource(R.drawable.button_list_dragged)
            if (!dragging) {
                val scaleUpX = ObjectAnimator.ofFloat(viewHolder.itemView, "scaleX", 1f, 1.1f)
                scaleUpX.duration = 150
                val scaleUpY = ObjectAnimator.ofFloat(viewHolder.itemView, "scaleY", 1f, 1.1f)
                scaleUpY.duration = 150
                val alpha = ObjectAnimator.ofFloat(viewHolder.itemView, "alpha", 1f, 0.8f)
                alpha.duration = 150
                scaleUpX.start()
                scaleUpY.start()
                alpha.start()
                dragging = true
            }
        } else {
            viewHolder.itemView.setBackgroundResource(R.drawable.button_list_selected_bg)
            if (dragging) {

                val scaleDownX = ObjectAnimator.ofFloat(viewHolder.itemView, "scaleX", 1.1f, 1f)
                scaleDownX.duration = 150
                val scaleDownY = ObjectAnimator.ofFloat(viewHolder.itemView, "scaleY", 1.1f, 1f)
                scaleDownY.duration = 150
                val alpha = ObjectAnimator.ofFloat(viewHolder.itemView, "alpha", 0.8f, 1f)
                alpha.duration = 150
                scaleDownX.start()
                scaleDownY.start()
                alpha.start()

                viewHolder.itemView.alpha = 1f
                viewHolder.itemView.scaleX = 1f
                viewHolder.itemView.scaleY = 1f

                dragging = false
            }
        }
    }

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        return false
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
}

interface ItemTouchHelperAdapter {
    fun onItemMoved(fromPosition: Int, toPosition: Int)
    fun onStartDrag(viewHolder: RecyclerView.ViewHolder)
}
