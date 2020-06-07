package com.lolo.io.onelist

import android.view.*
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.lolo.io.onelist.model.ItemList
import kotlinx.android.synthetic.main.button_list.view.*

class ListsAdapter(val lists: MutableList<ItemList>, val callback: ListsCallbacks) :
        RecyclerView.Adapter<ListsAdapter.ListViewHolder>(),
        ItemTouchHelperAdapter {

    private var selectedItemList = ItemList()

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) = callback.onListAdapterStartDrag()

    override fun onItemMoved(fromPosition: Int, toPosition: Int) {
        callback.onListMoved(fromPosition, toPosition)
        notifyDataSetChanged()
    }

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.button_list, parent, false)

        return ListViewHolder(view)
    }

    override fun getItemId(position: Int): Long {
        return lists[position].stableId
    }

    override fun getItemCount(): Int = lists.size

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val itemList = lists[position]

        val currentView = holder.itemView.textView as TextView

        when (itemList) {
            selectedItemList -> {
                currentView.setBackgroundResource(R.drawable.button_list_selected_bg)
                currentView.setTextColor(ContextCompat.getColor(App.instance.mainContext, R.color.colorPrimary))
                currentView.alpha = 1f
            }
            else -> {
                currentView.setBackgroundResource(R.drawable.button_list_bg)
                currentView.setTextColor(ContextCompat.getColor(App.instance.mainContext, R.color.colorAccentLight))
            }
        }
        currentView.text = itemList.title
    }

    fun selectList(itemList: ItemList) {
        val previousSelection = lists.indexOf(selectedItemList)
        selectedItemList = itemList
        if (previousSelection > -1) {
            notifyItemChanged(previousSelection)
        }
        notifyItemChanged(lists.indexOf(itemList))
    }

    inner class ListViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        init {
            view.setOnTouchListener { _, motionEvent -> onTouch(motionEvent) }
        }

        private fun onTouch(motionEvent: MotionEvent?): Boolean {
            if (motionEvent?.action == MotionEvent.ACTION_DOWN) {
                callback.onSelectList(lists[layoutPosition])
            }
            return false
        }
    }
}