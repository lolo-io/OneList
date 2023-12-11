package com.lolo.io.onelist.feature.lists.tuto

import android.app.Application
import android.content.Context
import com.lolo.io.onelist.R
import com.lolo.io.onelist.core.model.Item
import com.lolo.io.onelist.core.model.ItemList

class FirstLaunchLists(val application: Application) {

    val context get() = application
    private fun firstLaunchListTutorial() = ItemList(
        id = 1,
        title = context.getString(R.string.first_list_tuto_title),
        items = mutableListOf(
            Item(context.getString(R.string.first_list_tuto_one)),
            Item(context.getString(R.string.first_list_tuto_two)),
            Item(context.getString(R.string.first_list_tuto_three)),
            Item(context.getString(R.string.first_list_tuto_four)),
            Item(context.getString(R.string.first_list_tuto_five)),
            Item(context.getString(R.string.first_list_tuto_six)),
            Item(context.getString(R.string.first_list_tuto_seven)),

            Item(context.getString(R.string.first_list_tuto_eight)),
            Item(context.getString(R.string.first_list_tuto_eight_comment)),

            Item(context.getString(R.string.first_list_tuto_nine)),
            Item(context.getString(R.string.first_list_tuto_nine_comment)),

            Item(context.getString(R.string.first_list_tuto_ten)),
        )
    )

    private fun firstLaunchListTodo() = ItemList(
        id = 2,
        title = context.getString(R.string.first_list_todo_title),
        items = mutableListOf(
            Item(context.getString(R.string.first_list_todo_one)),
        )
    )

    fun firstLaunchLists() = listOf(
        firstLaunchListTutorial(),
        firstLaunchListTodo()
    )
}
