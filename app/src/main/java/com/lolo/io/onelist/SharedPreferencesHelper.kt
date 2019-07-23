package com.lolo.io.onelist

import android.app.Activity
import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class SharedPreferencesHelper(private val activity: Activity) {

    private val firstLaunchPref = "firstLaunch"
    private val listsPrefs = "lists"
    private val selectedListPref = "selectedList"

    var firstLaunch: Boolean
        get() {
            val sp = activity.getPreferences(Context.MODE_PRIVATE)
            return sp.getBoolean(firstLaunchPref, true)
        }
    set(value) {
        val sp = activity.getPreferences(Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putBoolean(firstLaunchPref, value)
        editor.apply()
    }

    var allLists: List<ItemList>
        get() {
            val sp = activity.getPreferences(Context.MODE_PRIVATE)
            val gson = Gson()
            val json = sp.getString(listsPrefs, null)
            var lists: List<ItemList> = ArrayList()
            if (json != null) {
                lists = gson.fromJson(json, object : TypeToken<List<ItemList>>() {
                }.type)
            }
            return lists
        }
        set(value) {
            GlobalScope.launch {
                val sp = activity.getPreferences(Context.MODE_PRIVATE)
                val editor = sp.edit()
                val gson = Gson()
                val json = gson.toJson(value)
                editor.putString(listsPrefs, json)
                editor.apply()
            }
        }

    var selectedListIndex: Int
        get() {
            val sp = activity.getPreferences(Context.MODE_PRIVATE)
            return sp.getInt(selectedListPref, 0)
        }
        set(value) {
            GlobalScope.launch {
                val sp = activity.getPreferences(Context.MODE_PRIVATE)
                val editor = sp.edit()
                editor.putInt(selectedListPref, value)
                editor.apply()
            }
        }
}
