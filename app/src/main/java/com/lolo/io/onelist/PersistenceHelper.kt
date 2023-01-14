package com.lolo.io.onelist

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.preference.PreferenceManager
import android.util.JsonReader
import android.util.Log
import android.widget.Toast
import androidx.documentfile.provider.DocumentFile
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import com.anggrayudi.storage.file.*
import com.google.android.material.internal.ContextUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lolo.io.onelist.model.ItemList
import com.lolo.io.onelist.updates.appContext
import com.lolo.io.onelist.util.*
import kotlinx.coroutines.*
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.StringReader
import java.util.*

class PersistenceHelper(private val app: Activity) {

    val versionPref: String = "version"
    private val selectedListPref = "selectedList"
    private val listIdsPref = "listsIds"
    private val defaultPathPref = "defaultPath"
    private val shareMarkdownPref = "shareMarkdown"
    val themePref: String = "theme"

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

    var shareMarkdown: Boolean
        get() {
            val sp = getDefaultSharedPreferences(appContext)  // for some reason, app.getPreferences(Context.MODE_PRIVATE) does not work here
            return sp.getBoolean(shareMarkdownPref, false) ?: false
        }
        set(value) {
            val sp = getDefaultSharedPreferences(appContext)
            val editor = sp.edit()
            editor.putBoolean(shareMarkdownPref, value)
            editor.apply()
        }

    fun getTheme(context: Context): String {
            val sp = PreferenceManager.getDefaultSharedPreferences(context)
            return sp.getString(themePref, "auto") ?: "auto"
        }

    fun getAllLists(): List<ItemList> {
        // Get all lists and items content from disk, returns a map of (stableId, ItemList) where stableId is a Long unique identifying number
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
        // Force reload all ItemLists and their Items contents from disk
        // This detects new lists, and also refresh their Items content
        // To only refresh ItemLists content but not scan for new ItemLists, use refreshAllLists() or refreshList()
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
        // Force reload all lists contents from disk
        // Note this does not detect new lists, it only reloads items, the content of ItemLists
        runBlocking {
            lists.forEach {
                it.items.clear()
                it.items.addAll(getListAsync(it.stableId).await().items)
            }
        }
    }

    fun refreshList(lists: List<ItemList>, listId: Long) {
        // Force reload one list content given a stableId
        // This is like refreshAllLists() but for a single list.
        lists.forEach { // TODO: maybe use lists.parallelStream().forEach to parallelize operation? Tested but did not seem to improve speed
            if (it.stableId == listId) {
                it.items.clear()
                it.items.addAll(getList(it.stableId).items)
            }
        }
    }

    fun updateListIdsTableAsync(lists: List<ItemList>) {
        GlobalScope.launch {
            updateListIdsTable(lists)
        }
    }

    fun updateListIdsTable(lists: List<ItemList>) {
        // Given a List of ItemList, update the persistent Map of (ItemList.stableId, ItemList.path)
        // Hence from this Map, given a Long stableId, we can find where the ItemList is stored on disk
        // Path will be empty if stored in app's Preferences
        // We store this Map in Preferences so that we can easily find the path to all ItemLists
        listsIds = lists.map { it.stableId to it.path }.toMap()
        val sp = app.getPreferences(Context.MODE_PRIVATE)
        val editor = sp.edit()
        val gson = Gson()
        val json = gson.toJson(listsIds)
        editor.putString(listIdsPref, gson.toJson(json))
        editor.apply()
    }

    private fun getListIdsTable(): Map<Long, String> {
        // Retrieve the Map of ItemLists paths from the Preferences
        val sp = app.getPreferences(Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sp.getString(listIdsPref, null)?.replace("\\", "")?.removeSurrounding("\"")
        return if (json != null) {
            gson.fromJson(json, object : TypeToken<Map<Long, String>>() {
            }.type)
        } else mapOf()
    }

    fun createListFromUri(uri: Uri): ItemList {
        // Import an ItemList from a file, when the file is opened directly from a file manager
        try {
            val gson = Gson()
            val content =
                    if (Build.VERSION.SDK_INT >= 29) {
                        val file = DocumentFileCompat.fromFullPath(appContext, uri.toString()!!, requiresWriteAccess = false)
                        file!!.openInputStream(appContext)?.bufferedReader()?.use { it.readText() }
                    } else {
                        app.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
                    }
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
        // Import an ItemList from inside the app, via a dedicated button
        try {
            val gson = Gson()
            val fileUri = filePath.toUri
            Log.d("OneList", "Debugv importList: path: $filePath")
            val list = fileUri?.let { uri ->
                var ins: InputStream? = null
                try {
                    Log.d("OneList", "Debugv importList: uri: " + uri.toString())
                    ins =
                        if (Build.VERSION.SDK_INT >= 29) {
                            val file = DocumentFileCompat.fromFullPath(appContext, uri.toString()!!, requiresWriteAccess=false)
                            Log.d("OneList", "Debugv Try to open inputstream")
                            file!!.openInputStream(appContext)
                        } else {
                            App.instance.contentResolver.openInputStream(uri)
                        }
                    Log.d("OneList", "Debugv importList: openInputStream successful!")
                    Log.d("OneList", "Debugv importList: openInputStream file handle: " + ins.toString())
                    val ret = gson.fromJson(ins!!.reader(), ItemList::class.java)
                    Log.d("OneList", "Debugv importList: reader successful! Returning.")
                    return ret
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

    private fun getList(listId: Long): ItemList {
        return runBlocking {
            getListAsync(listId).await()
        }
    }

    private fun getListAsync(listId: Long): Deferred<ItemList> {
        // Retrieve an ItemList content from disk
        return GlobalScope.async {
            val gson = Gson()
            val sp = app.getPreferences(Context.MODE_PRIVATE)
            val path = listsIds[listId]
            val ins =
                    if ((path == null) || path!!.isEmpty()) {
                        null
                    } else {
                        if (Build.VERSION.SDK_INT >= 29) {
                            val file = DocumentFileCompat.fromFullPath(appContext, path!!, requiresWriteAccess = false)
                            Log.d("OneList", "Debugv Try to open inputstream")
                            file?.openInputStream(appContext)
                        } else {
                            val fileUri = path?.toUri
                            if (fileUri != null) {
                                App.instance.contentResolver.openInputStream(fileUri)
                            } else {
                                null
                            }
                        }
                    }
            val list = ins?.let { ins ->
                try {
                    Log.d("OneList", "Debugv getList try to read file")
                    gson.fromJson(ins!!.reader(), ItemList::class.java)
                } catch (e: Exception) {
                    Log.d("OneList", "Debugv getList error: " + e.stackTraceToString())
                    app.runOnUiThread { Toast.makeText(App.instance, app.getString(R.string.error_opening_filepath), Toast.LENGTH_LONG).show() }
                    val grantedPaths: Map<String, Set<String>> = DocumentFileCompat.getAccessibleAbsolutePaths(appContext)
                    Log.d("OneList", "Debugv getList grantedPaths: " + grantedPaths.toString())
                    null
                    /*
                    val lenientReader = JsonReader(ins!!.reader())
                    lenientReader.isLenient = true
                    try {
                        gson.fromJson(lenientReader, ItemList::class.java)
                    } catch (e2: Exception) {
                        null
                    }
                    */
                } finally {
                    ins?.close()
                }
            } ?: path.takeIf { it?.isNotBlank() == true }?.let {
                if (Build.VERSION.SDK_INT < 29) {
                    try {
                        val json = File(path).readText()
                        gson.fromJson(json, ItemList::class.java)
                    } catch (e: Exception) {
                        Log.d("OneList", "Debugv getList error in path.takeIf: " + e.stackTraceToString())
                        app.runOnUiThread { Toast.makeText(App.instance, app.getString(R.string.error_opening_filepath, path), Toast.LENGTH_LONG).show() }
                        null
                    }
                } else {
                    null
                }
            } ?: gson.fromJson(sp.getString(listId.toString(), ""), ItemList::class.java)

            list.apply {
                this.path = path ?: ""
            }
        }
    }

    fun saveListAsync(list: ItemList) {
        Log.d("OneList", "Debugv saveListAsync")
        GlobalScope.launch {
            saveList(list)
        }
    }

    fun saveList(list: ItemList) {
        // Save an ItemList content on-disk
        val gson = Gson()
        val json = gson.toJson(list)
        try {
            val path = list.path
            Log.d("OneList", "Debugv saveList to list path: " + list.path)
            val out =
                if (Build.VERSION.SDK_INT >= 29) {
                    // If Android >= 10, need to use scoped storage permissions via SimpleStorage
                    Log.d("OneList", "Debugv saveList SDK_INT >= 29")
                    var file: DocumentFile? = DocumentFileCompat.fromFullPath(appContext, path!!, requiresWriteAccess=true, considerRawFile=true)
                    if ((file == null) || (!file.exists())) {  // if file does not exist, we create it
                        Log.d("OneList", "Debugv saveList file does not exists, create it")
                        val parentFolderPath = path.substringBeforeLast("/")
                        val folder = DocumentFileCompat.fromFullPath(appContext, parentFolderPath!!, requiresWriteAccess=true)
                        file = folder?.makeFile(appContext,path.substringAfterLast("/"), "text/json", mode=CreateMode.REPLACE) // important: the type "text/json" is what defines the file's extension as .json. If it was "text/plain", it would be a .txt. We cannot force an extension, it's Android's doing.
                    }
                    Log.d("OneList", "Debugv Try to open outputstream")
                    file = file?.recreateFile(appContext)  // erase content first by recreating file. For some reason, DocumentFileCompat.fromFullPath(requiresWriteAccess=true) and openOutputStream(append=false) only open the file in append mode, so we need to recreate the file to truncate its content first
                    file?.openOutputStream(appContext, append=false)
                } else {
                    App.instance.contentResolver.openOutputStream(path.toUri!!)
                }
            Log.d("OneList", "Debugv saveList just before let block")
            out?.let { out ->
                try {
                    Log.d("OneList", "Debugv saveList try to write")
                    out!!.write(json.toByteArray(Charsets.UTF_8))
                    Log.d("OneList", "Debugv saveList json: " + json)
                    Log.d("OneList", "Debugv savelist json.toBytoArray: " + json.toByteArray(Charsets.UTF_8).decodeToString())
                    Log.d("OneList", "Debugv saveList write successful!")
                } catch (e: Exception) {
                    app.runOnUiThread { Toast.makeText(App.instance, app.getString(R.string.error_saving_to_path), Toast.LENGTH_LONG).show() }
                    Log.d("OneList", "Debugv saveList unable to write: " + e.stackTraceToString())
                    val grantedPaths: Map<String, Set<String>> = DocumentFileCompat.getAccessibleAbsolutePaths(appContext)
                    Log.d("OneList", "Debugv saveList grantedPaths: " + grantedPaths.toString())
                } finally {
                    out?.close()
                }
            } ?: if (list.path.isNotBlank()) {
                File(list.path).writeText(json)
            }
        } catch (e: Exception) {
            app.runOnUiThread { Toast.makeText(App.instance, app.getString(R.string.error_saving_to_path, list.path), Toast.LENGTH_LONG).show() }
            Log.d("OneList", "Debugv saveList 2nd try block unable to write: " + e.stackTraceToString())
        }

        // save in prefs anyway, so we fallback on app private storage copy of the list if the other storage fails or if none is selected
        val sp = app.getPreferences(Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putString(list.stableId.toString(), json)
        editor.apply()
    }

    fun removeListFile(list: ItemList) {
        // Delete ItemList file from disk
        GlobalScope.launch {
            if (list.path.isNotBlank()) {
                try {
                    if (Build.VERSION.SDK_INT >= 29) {
                        val file = DocumentFileCompat.fromFullPath(appContext, list.path!!, requiresWriteAccess=true)
                        file!!.delete()
                    } else {
                        File(list.path).delete()
                    }
                    app.runOnUiThread { Toast.makeText(App.instance, app.getString(R.string.file_deleted), Toast.LENGTH_LONG).show() }
                } catch (e: Exception) {
                    app.runOnUiThread { Toast.makeText(App.instance, app.getString(R.string.error_deleting_list_file), Toast.LENGTH_LONG).show() }
                }
            }
        }
    }

    fun shareList(list: ItemList) {
        // Share ItemList as plaintext, e.g. by e-mail
        Log.d("OneList", "Debugv shareMardown: " + shareMarkdown.toString())
        list.markdown = shareMarkdown // set markdown template if markdown is selected in preferences
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, list.toString()) // toString() is overloaded to output the list's title, content and an ad for the software, except if toStringNoAd() is used
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        app.startActivity(shareIntent)
    }

    fun shareAllLists() {
        // Share all ItemLists as plaintext

        // Fetch list of all lists
        var lists = getAllLists()
        // Concat content of every lists
        var lists_concat = if (shareMarkdown) "# ALL LISTS\n-----\n\n" else "ALL LISTS\n-----\n\n"
        for (l in lists) {
            // set markdown template if markdown is selected in preferences
            l.markdown = shareMarkdown
            // toString() is overloaded to output the list's title, content and an ad for the software, except if toStringNoAd() is used
            lists_concat += l.toStringNoAd() + "\n\n----\n\n"
        }
        // Append the ad once at the very end
        lists_concat += lists[0].toStringOnlyAd()
        // Share dialog
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, lists_concat)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        app.startActivity(shareIntent)
    }

    fun updateAllPathsToDefault() {
        // Copy all ItemLists to the urrently selected Default Storage
        // Otherwise, each ItemList stays stored where the default storage was defined at the time of the ItemList creation, or where they are if imported
        // By using this function, we copy all currently opened ItemLists in one same storage location

        // Fetch list of all lists
        val lists = getAllLists()
        // Loop through all lists
        for (l in lists) {
            // toString() is overloaded to output the list's title, content and an ad for the software, except if toStringNoAd() is used
            l.path = "$defaultPath/${l.fileName}"
            saveList(l)
        }
        // Update listsIds immutable Map all at once using the adequate function with our new list of ItemList objects
        updateListIdsTable(lists)
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

    // Only to handle architecture updates between versions. do not use
    val compat = Compat()

    inner class Compat {

        private val firstLaunchPrefCompat = "firstLaunch"
        private val listsPrefsCompat = "lists"

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
    }
}
