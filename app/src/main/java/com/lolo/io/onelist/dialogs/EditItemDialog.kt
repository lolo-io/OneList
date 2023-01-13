package com.lolo.io.onelist.dialogs

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import com.lolo.io.onelist.MainActivity
import com.lolo.io.onelist.R
import com.lolo.io.onelist.model.Item
import com.lolo.io.onelist.model.ItemList
import com.lolo.io.onelist.util.shake
import kotlinx.android.synthetic.main.dialog_edit_item.*
import kotlinx.android.synthetic.main.dialog_edit_item.view.*


@SuppressLint("InflateParams")
fun editItemDialog(activity: Activity, item: Item, onDoneEditing: (Item, ItemList?) -> Any?): AlertDialog {
    // Get all lists
    val mainActivity: MainActivity = activity as MainActivity // to get access to persistence, we need the mainActivity
    val lists = mainActivity.persistence.getAllLists()
    val listsMap = lists.map { it.title to it }.toMap() // convert to a map of title and list object: the title will be shown in a spinner, but when the user will select it, we will be able to retrieve the list from its title

    val view = LayoutInflater.from(activity).inflate(R.layout.dialog_edit_item, null).apply {
        // Build edit task dialog
        item_title.setText(item.title)
        item_title.setSelection(item_title.text.length)
        item_comment.setText(item.comment)
        item_title.requestFocus()

        // Prepare lists titles as an Array to dynamically populate a Spinner
        val listsKeys = setOf("") union listsMap.keys  // add empty option, to mean that we do not wish to move task to another list
        val listsKeysArr = listsKeys.toTypedArray() // extract keys (lists titles) as an Array, to later bind to the Spinner via an ArrayAdapter

        // Create an ArrayAdapter from the listsKeysArr Array to later bind these dynamically generated values to the Spinner
        val spinArAd: ArrayAdapter<*> = ArrayAdapter<Any?>(
                /* context = */ activity,
                /* resource = */ android.R.layout.simple_spinner_item,
                /* objects = */ listsKeysArr)

        // Set a generic layout resource file for each item in the spinner
        spinArAd.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item)

        // Bind the ArrayAdapter, and hence the dynamically generated listsKeysArr (array of lists' titles), to the Spinner
        item_list_spinner.adapter = spinArAd
    }

    // Display the dialog
    val dialog = AlertDialog.Builder(activity).run {
        setView(view)
        create()
    }.apply {
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        setCanceledOnTouchOutside(true)
        setOnCancelListener {
            onDoneEditing(item, null)
        }
    }

    // Define callbacks when validating or cancelling dialog
    view.apply {
        // Dialog was validated by user
        validateEdit.setOnClickListener {
            if (view.item_title.text.toString().isEmpty()) {
                dialog.item_title.shake()
            } else {
                // Craft new Item from user's inputs
                val newItem = Item(view.item_title.text.toString(), view.item_comment.text.toString(), item.done, item.commentDisplayed)
                // User wants to move Item to another ItemList?
                val item_list_spinner_selection = view.item_list_spinner.selectedItem.toString()
                val targetList = if (item_list_spinner_selection.isEmpty()) {
                    // If spinner choice is empty, then we don't move
                    null
                } else {
                    // If spinner choice is not empty, then we move
                    // Note that here we do not have access to which ItemList we are in, so we can't know if user selected the same ItemList we currently are in, we check this in the callback
                    listsMap[item_list_spinner_selection] // simply fetch the ItemList object from the Map using the list's title selected by user
                }
                // Callback, supplying the new Item and also whether we move to another ItemList
                onDoneEditing(newItem, targetList)
                // Hide dialog
                dialog.dismiss()
            }
        }
        // Dialog was cancelled by user
        cancelEdit.setOnClickListener {
            onDoneEditing(item, null)
            dialog.dismiss()
        }
    }
    return dialog
}