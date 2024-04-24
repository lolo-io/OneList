package com.lolo.io.onelist.feature.lists.tuto

import android.app.Application
import android.content.Context
import com.lolo.io.onelist.core.model.Item
import com.lolo.io.onelist.core.model.ItemList
import com.lolo.io.onelist.feature.lists.R

class FirstLaunchLists(private val application: Application) {

    private val context get() = application
    private fun firstLaunchListTutorial() = ItemList(
        id = 1,
        title = context.getString(R.string.first_list_tuto_title),
        items = mutableListOf(
            Item(
                context.getString(R.string.first_list_tuto_one),
                id = 1L
            ),
            Item(
                context.getString(R.string.first_list_tuto_two),
                id = 2L
            ),
            Item(
                context.getString(R.string.first_list_tuto_three),
                id = 3L
            ),
            Item(
                context.getString(R.string.first_list_tuto_four),
                id = 4L
            ),
            Item(
                context.getString(R.string.first_list_tuto_five),
                id = 5L
            ),
            Item(
                context.getString(R.string.first_list_tuto_six),
                id = 6L
            ),
            Item(
                context.getString(R.string.first_list_tuto_seven),
                id = 7L
            ),

            Item(
                context.getString(R.string.first_list_tuto_eight),
                id = 8L
            ),
            Item(
                context.getString(R.string.first_list_tuto_eight_comment),
                id = 9L
            ),

            Item(
                context.getString(R.string.first_list_tuto_nine),
                id = 10L
            ),
            Item(
                context.getString(R.string.first_list_tuto_nine_comment),
                id = 11L
            ),

            Item(
                context.getString(R.string.first_list_tuto_ten),
                id = 12L
            ),
        )
    )

    private fun firstLaunchListTodo() = ItemList(
        id = 2,
        title = context.getString(R.string.first_list_todo_title),
        items = mutableListOf(
            Item(
                context.getString(R.string.first_list_todo_one),
                id = 13L
            ),
        )
    )

    fun firstLaunchLists() = listOf(
        firstLaunchListTutorial(),
        firstLaunchListTodo()
    )
}
