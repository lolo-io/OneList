package com.lolo.io.onelist

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lolo.io.onelist.model.ItemList
import kotlinx.coroutines.*
import java.io.File
import java.io.IOException
import java.util.*

class PersistenceHelper(private val app: Activity) {

    private val firstLaunchPrefCompat = "firstLaunch"
    private val listsPrefsCompat = "lists"

    var versionPref: String = "version"
    private val selectedListPref = "selectedList"
    private val listIdsPref = "listsIds"
    private val defaultPathPref = "defaultPath"

    private var listsIds: Map<Long, String> = linkedMapOf()

    var defaultPath: String
        get() {
            val sp = app.getPreferences(Context.MODE_PRIVATE)
            return sp.getString(defaultPathPref, "") ?: ""
        }
        set(value) {
            val sp = app.getPreferences(Context.MODE_PRIVATE)
            val editor = sp.edit()
            editor.putString(defaultPathPref, value)
            editor.apply()
        }

    var version: String
        get() {
            val sp = app.getPreferences(Context.MODE_PRIVATE)
            return sp.getString(versionPref, "0.0.0") ?: "0.0.0"
        }
        set(value) {
            val sp = app.getPreferences(Context.MODE_PRIVATE)
            val editor = sp.edit()
            editor.putString(versionPref, value)
            editor.apply()
        }

    var firstLaunchCompat: Boolean
        get() {
            val sp = app.getPreferences(Context.MODE_PRIVATE)
            return sp.getBoolean(firstLaunchPrefCompat, true)
        }
        set(value) {
            val sp = app.getPreferences(Context.MODE_PRIVATE)
            val editor = sp.edit()
            editor.putBoolean(firstLaunchPrefCompat, value)
            editor.apply()
        }

    val allListsCompat: List<ItemList>
        get() {
            val sp = app.getPreferences(Context.MODE_PRIVATE)
            val gson = Gson()
            val json = sp.getString(listsPrefsCompat, null)
            var lists: List<ItemList> = ArrayList()
            if (json != null) {
                lists = gson.fromJson(json, object : TypeToken<List<ItemList>>() {
                }.type)
            }
            return lists
        }


    fun getAllLists(): List<ItemList> {
        return runBlocking {
            listsIds = getListIdsTable()
            try {
                val ret = listsIds.map { getListAsync(it.key).await() }
                ret
            } catch (e: Exception) {
                listOf<ItemList>()
            }
        }
    }

    fun refreshAllLists(lists: List<ItemList>) {
        runBlocking {
            lists.forEach {
                it.items.clear()
                it.items.addAll(getListAsync(it.stableId).await().items)
            }
        }
    }

    fun updateListIdsTableAsync(lists: List<ItemList>) {
        GlobalScope.launch {
            updateListIdsTable(lists)
        }
    }


    fun updateListIdsTable(lists: List<ItemList>) {
        listsIds = lists.map { it.stableId to it.path }.toMap()
        val sp = app.getPreferences(Context.MODE_PRIVATE)
        val editor = sp.edit()
        val gson = Gson()
        val json = gson.toJson(listsIds)
        editor.putString(listIdsPref, gson.toJson(json))
        editor.apply()
    }

    private fun getListIdsTable(): Map<Long, String> {
        val sp = app.getPreferences(Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sp.getString(listIdsPref, null)?.replace("\\", "")?.removeSurrounding("\"")
        return if (json != null) {
            gson.fromJson(json, object : TypeToken<Map<Long, String>>() {
            }.type)
        } else mapOf()
    }

    fun createListFromUri(uri: Uri?): ItemList {
        try {
            val gson = Gson()
            val content = app.contentResolver.openInputStream(uri!!)?.bufferedReader()?.use { it.readText() }
            val list = gson.fromJson(content, ItemList::class.java)
            list.path = ""
            require(!listsIds.containsKey(list.stableId)) { app.getString(R.string.list_already_in_your_lists) }
            return list
        } catch (e: IllegalArgumentException) {
            throw e
        } catch (e: Exception) {
            throw IOException(app.getString(R.string.error_opening_file))
        }
    }


    fun importList(filePath: String): ItemList {
        try {
            val gson = Gson()
            val json = File(filePath).readText()
            val list = gson.fromJson(json, ItemList::class.java)
            require(!listsIds.containsKey(list.stableId)) { app.getString(R.string.list_already_in_your_lists) }
            return list
        } catch (e: IOException) {
            throw IOException(app.getString(R.string.error_opening_file))
        }
    }


    private fun getListAsync(listId: Long): Deferred<ItemList> {
        return GlobalScope.async {
            val filePath = listsIds[listId]
            val gson = Gson()
            val sp = app.getPreferences(Context.MODE_PRIVATE)
            val list = (if (filePath?.isNotBlank() == true) {
                try {
                    val json = File(filePath).readText()
                    gson.fromJson(json, ItemList::class.java)
                } catch (e: Exception) {
                    app.runOnUiThread { Toast.makeText(App.instance, app.getString(R.string.error_opening_filepath, filePath), Toast.LENGTH_LONG).show() }
                    null
                }
            } else null
                    ) ?: gson.fromJson(sp.getString(listId.toString(), ""), ItemList::class.java)
            list.apply { path = filePath ?: "" }
        }
    }


    fun saveListAsync(list: ItemList) {
        GlobalScope.launch {
            saveList(list)
        }
    }

    fun saveList(list: ItemList) {
        val sp = app.getPreferences(Context.MODE_PRIVATE)
        val editor = sp.edit()
        val gson = Gson()
        val json = gson.toJson(list)
        if (list.path.isNotBlank()) {
            try {
                File(list.path).writeText(json)
            } catch (e: Exception) {
                app.runOnUiThread { Toast.makeText(App.instance, app.getString(R.string.error_saving_to_path, list.path), Toast.LENGTH_LONG).show() }
            }
        }

        // save in prefs anyway
        editor.putString(list.stableId.toString(), json)
        editor.apply()
    }

    fun removeListFile(list: ItemList) {
        GlobalScope.launch {
            if (list.path.isNotBlank()) {
                try {
                    File(list.path).delete()
                    app.runOnUiThread { Toast.makeText(App.instance, app.getString(R.string.file_deleted), Toast.LENGTH_LONG).show() }
                } catch (e: Exception) {
                    app.runOnUiThread { Toast.makeText(App.instance, app.getString(R.string.error_deleting_list_file), Toast.LENGTH_LONG).show() }
                }
            }
        }
    }

    fun shareList(list: ItemList) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, list.toString())
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        app.startActivity(shareIntent)
    }

    var selectedListIndex: Int
        get() {
            val sp = app.getPreferences(Context.MODE_PRIVATE)
            return sp.getInt(selectedListPref, 0)
        }
        set(value) {
            GlobalScope.launch {
                val sp = app.getPreferences(Context.MODE_PRIVATE)
                val editor = sp.edit()
                editor.putInt(selectedListPref, value)
                editor.apply()
            }
        }
}
