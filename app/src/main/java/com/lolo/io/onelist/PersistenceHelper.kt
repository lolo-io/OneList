package com.lolo.io.onelist

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lolo.io.onelist.model.ItemList
import com.lolo.io.onelist.util.toUri
import kotlinx.coroutines.*
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.util.*

class PersistenceHelper(private val app: Activity) {

    var versionPref: String = "version"
    private val selectedListPref = "selectedList"
    private val listIdsPref = "listsIds"
    private val defaultPathPref = "defaultPath"

    private var listsIds: Map<Long, String> = linkedMapOf()

    lateinit var context: Context

    fun setContextInsteadOfActivity(c: Context){
        context=c
    }

    private fun getPref(): SharedPreferences {
        if(::context.isInitialized){
            return context.getSharedPreferences("com.lolo.io.onelist.MainActivity", Context.MODE_PRIVATE)
        }
        return app.getPreferences(Context.MODE_PRIVATE)
    }

    var defaultPath: String
        get() {
            val sp = getPref()
            return sp.getString(defaultPathPref, "") ?: ""
        }
        set(value) {
            val sp = getPref()
            val editor = sp.edit()
            editor.putString(defaultPathPref, value)
            editor.apply()
        }

    var version: String
        get() {
            val sp = getPref()
            return sp.getString(versionPref, "0.0.0") ?: "0.0.0"
        }
        set(value) {
            val sp = getPref()
            val editor = sp.edit()
            editor.putString(versionPref, value)
            editor.apply()
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


    fun refreshAndFetchNewLists(lists: MutableList<ItemList>) {
        runBlocking {
            val newIds = getListIdsTable()
            newIds.forEach { fetchedId ->
                if (!listsIds.keys.contains(fetchedId.key)) {
                    lists.add(getListAsync(fetchedId.key).await())
                }
            }
            listsIds = newIds
            refreshAllLists(lists)
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
        val sp = getPref()
        val editor = sp.edit()
        val gson = Gson()
        val json = gson.toJson(listsIds)
        editor.putString(listIdsPref, gson.toJson(json))
        editor.apply()
    }

    private fun getListIdsTable(): Map<Long, String> {
        val sp = getPref()
        val gson = Gson()
        val json = sp.getString(listIdsPref, null)?.replace("\\", "")?.removeSurrounding("\"")
        return if (json != null) {
            gson.fromJson(json, object : TypeToken<Map<Long, String>>() {
            }.type)
        } else mapOf()
    }

    fun createListFromUri(uri: Uri): ItemList {
        try {
            val gson = Gson()
            val content = app.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
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
            val fileUri = filePath.toUri
            val list = fileUri?.let { uri ->
                var ins: InputStream? = null
                try {
                    ins = App.instance.contentResolver.openInputStream(uri)
                    gson.fromJson(ins!!.reader(), ItemList::class.java)
                } catch (e: Exception) {
                    throw Exception()
                } finally {
                    ins?.close()
                }
            } ?: filePath.takeIf { it.isNotBlank() }?.let {
                val json = File(it).readText()
                gson.fromJson(json, ItemList::class.java)
            } ?: throw Exception()

            require(!listsIds.containsKey(list.stableId)) { app.getString(R.string.list_already_in_your_lists) }
            return list
        } catch (e: IOException) {
            throw IOException(app.getString(R.string.error_opening_file))
        }
    }

    private fun getListAsync(listId: Long): Deferred<ItemList> {
        return GlobalScope.async {
            val path = listsIds[listId]
            val gson = Gson()
            val sp = getPref()
            val fileUri = path.toUri
            val list = fileUri?.let { uri ->
                var ins: InputStream? = null
                try {
                    ins = App.instance.contentResolver.openInputStream(uri)
                    gson.fromJson(ins!!.reader(), ItemList::class.java)
                } catch (e: Exception) {
                    app.runOnUiThread { Toast.makeText(App.instance, app.getString(R.string.error_opening_filepath, uri), Toast.LENGTH_LONG).show() }
                    null
                } finally {
                    ins?.close()
                }
            } ?: path.takeIf { it?.isNotBlank() == true }?.let {
                try {
                    val json = File(path).readText()
                    gson.fromJson(json, ItemList::class.java)
                } catch (e: Exception) {
                    app.runOnUiThread { Toast.makeText(App.instance, app.getString(R.string.error_opening_filepath, path), Toast.LENGTH_LONG).show() }
                    null
                }
            } ?: gson.fromJson(sp.getString(listId.toString(), ""), ItemList::class.java)

            list.apply {
                this.path = path ?: ""
            }
        }
    }

    fun saveListAsync(list: ItemList) {
        GlobalScope.launch {
            saveList(list)
        }
    }

    fun saveList(list: ItemList) {
        val sp = getPref()
        val editor = sp.edit()
        val gson = Gson()
        val json = gson.toJson(list)
        try {
            val fileUri = list.path.toUri
            fileUri?.let { uri ->
                val out = App.instance.contentResolver.openOutputStream(uri)
                try {
                    out!!.write(json.toByteArray(Charsets.UTF_8)) // NPE is catched below
                } catch (e: Exception) {
                    app.runOnUiThread { Toast.makeText(App.instance, app.getString(R.string.error_saving_to_path, list.path), Toast.LENGTH_LONG).show() }
                } finally {
                    out?.close()
                }
            } ?: if (list.path.isNotBlank()) {
                File(list.path).writeText(json)
            }
        } catch (e: Exception) {
            app.runOnUiThread { Toast.makeText(App.instance, app.getString(R.string.error_saving_to_path, list.path), Toast.LENGTH_LONG).show() }
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
            val sp = getPref()
            return sp.getInt(selectedListPref, 0)
        }
        set(value) {
            GlobalScope.launch {
                val sp = getPref()
                val editor = sp.edit()
                editor.putInt(selectedListPref, value)
                editor.apply()
            }
        }

    // Only to handle architecture updates between versions. do not use
    val compat = Compat()

    inner class Compat {

        private val firstLaunchPrefCompat = "firstLaunch"
        private val listsPrefsCompat = "lists"

        val allListsCompat: List<ItemList>
            get() {
                val sp = getPref()
                val gson = Gson()
                val json = sp.getString(listsPrefsCompat, null)
                var lists: List<ItemList> = ArrayList()
                if (json != null) {
                    lists = gson.fromJson(json, object : TypeToken<List<ItemList>>() {
                    }.type)
                }
                return lists
            }

        var firstLaunchCompat: Boolean
            get() {
                val sp = getPref()
                return sp.getBoolean(firstLaunchPrefCompat, true)
            }
            set(value) {
                val sp = getPref()
                val editor = sp.edit()
                editor.putBoolean(firstLaunchPrefCompat, value)
                editor.apply()
            }
    }
}
