package com.lolo.io.onelist

import android.view.*
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.lolo.io.onelist.databinding.ButtonListBinding
import com.lolo.io.onelist.model.ItemList

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
        val binding = ButtonListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun getItemId(position: Int): Long {
        return lists[position].stableId
    }

    override fun getItemCount(): Int = lists.size

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val itemList = lists[position]

        val currentView = holder.binding.textView

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

    inner class ListViewHolder(val binding: ButtonListBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnTouchListener { _, motionEvent -> onTouch(motionEvent) }
        }

        private fun onTouch(motionEvent: MotionEvent?): Boolean {
            if (motionEvent?.action == MotionEvent.ACTION_DOWN) {
                callback.onSelectList(lists[layoutPosition])
            }
            return false
        }
    }
}