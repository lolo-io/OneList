package com.lolo.io.onelist.core.data.persistence

import android.app.Application
import android.net.Uri
import android.widget.Toast
import androidx.documentfile.provider.DocumentFile
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.lolo.io.onelist.App
import com.lolo.io.onelist.R
import com.lolo.io.onelist.core.data.utils.toItemListEntity
import com.lolo.io.onelist.core.database.dao.ItemListDao
import com.lolo.io.onelist.core.database.model.ItemListEntity
import com.lolo.io.onelist.core.database.util.toItemListModel
import com.lolo.io.onelist.core.database.util.toItemListModels
import com.lolo.io.onelist.core.model.ItemList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream

class PersistenceHelper(
    private val app: Application,
    private val dao: ItemListDao
) {
    private val versionPref: String = "version"
    private val selectedListPref = "selectedList"
    private val backupUriPref = "backupUri"
    private val backupDisplayPathPref = "backupDisplayPath" // only for display
    private val listUrisPref = "listUris"
    private val themePref: String = "theme"
    private val firstLaunchPref = "firstLaunch"

    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val coroutineUiScope = CoroutineScope(Dispatchers.Main)

    private val gson = Gson()
    private val sharedPreferences =  PreferenceManager.getDefaultSharedPreferences(app)

    private fun getPref(key: String, default: String = ""): String {
        return sharedPreferences.getString(key, default) ?: default
    }

    private fun editPref(key: String, value: String?) {
        sharedPreferences.edit()
            .putString(key, value)
            .apply()
    }

    private fun String.getPref(default: Boolean): Boolean {
        return sharedPreferences.getBoolean(this, default)
    }

    private fun String.editPref(value: Boolean = false) {
        sharedPreferences.edit()
            .putBoolean(this, value)
            .apply()
    }

    private fun String.getPref(default: Int): Int {
        return sharedPreferences.getInt(this, default)
    }

    private fun String.editPref(value: Int) {
        sharedPreferences.edit()
            .putInt(this, value)
            .apply()
    }

    var backupDisplayPath: String?
        get() = getPref(backupDisplayPathPref)
        set(value) = editPref(backupDisplayPathPref, value)

    var backupUri: String?
        get() = getPref(backupUriPref)
        set(value) = editPref(backupUriPref, value)

    var version: String
        get() = getPref(versionPref, "0.0.0")
        set(value) = editPref(versionPref, value)

    var theme: String
        get() = getPref(themePref, "auto")
        set(value) = editPref(versionPref, value)

    var firstLaunch: Boolean
        get() = firstLaunchPref.getPref(true)
        set(value) = firstLaunchPref.editPref(value)

    var selectedListIndex: Int
        get() = selectedListPref.getPref(0)
        set(value) = selectedListPref.editPref(value)


    suspend fun getAllLists(): List<ItemList> {
        return withContext(Dispatchers.IO) {
            val allListsFromDb = dao.getAll()
            if (backupUri != "") {
                try {
                    val lists = allListsFromDb.map {
                        getListFromLocalFile(it).await()
                    }
                    lists
                } catch (e: Exception) {
                    listOf()
                }
            } else {
                allListsFromDb.toItemListModels()
            }
        }
    }

    suspend fun deleteList(list: ItemList, deleteBackupFile: Boolean = false) {
        withContext(Dispatchers.IO) {
            dao.delete(list.toItemListEntity())
        }

        if (deleteBackupFile) {
            deleteListBackupFile(list)
        }
    }


    suspend fun createListFromUri(uri: Uri): ItemList {
        return withContext(Dispatchers.IO) {
            try {
                val gson = Gson()
                val content =
                    app.contentResolver.openInputStream(uri)
                        .use { iss -> iss?.bufferedReader()?.use { it.readText() } }
                // return :
                gson.fromJson(content, ItemList::class.java).also {
                    upsertList(it)
                }
            } catch (e: IllegalArgumentException) {
                throw e
            } catch (e: Exception) {
                throw IOException(app.getString(R.string.error_opening_file))
            }
        }
    }

    fun importList(fileUri: Uri): ItemList {
        try {
            val gson = Gson()
            val list = fileUri.let { uri ->
                var ins: InputStream? = null
                try {
                    ins = app.contentResolver.openInputStream(uri)
                    gson.fromJson(ins!!.reader(), ItemList::class.java)
                } catch (e: Exception) {
                    throw Exception()
                } finally {
                    ins?.close()
                }
            }
            //  require(!listsIds.containsKey(list.stableId)) { app.getString(R.string.list_already_in_your_lists) }
            return list
        } catch (e: IOException) {
            throw IOException(app.getString(R.string.error_opening_file))
        }
    }


    /*
        listUris pref stores a mapping of listId to listUri as a string of this form : "listId;;listUri" (note the ;; separator).
        This is not stored in the file because if a file is opened on another device then the stored uri has no sense.
     */
    private fun getListFromLocalFile(list: ItemListEntity): Deferred<ItemList> =
        coroutineScope.async {
            val uris = sharedPreferences.getStringSet(listUrisPref, emptySet())
            val fileUriStr = uris?.firstOrNull { it.substringBefore(";;") == list.id.toString() }
            val fileUri = fileUriStr?.let { Uri.parse(it) }

            val listFromFile = fileUri?.let { uri ->
                var ins: InputStream? = null
                try {
                    ins = app.contentResolver.openInputStream(uri)
                    gson.fromJson(ins!!.reader(), ItemList::class.java)
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            app,
                            app.getString(R.string.error_opening_filepath, uri),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    null
                } finally {
                    ins?.close()
                }
            } ?: dao.get(list.id).toItemListModel()

            listFromFile.apply {
                uri = fileUri
            }
        }

    suspend fun upsertList(list: ItemList): ItemList {
        return withContext(Dispatchers.IO) {
            dao.upsert(list.toItemListEntity()).takeIf { it > 0 }?.let {
                list.id = it
            }
            if (list.uri != null) {
                saveListFile(list)
            }
            list
        }
    }

    private fun saveListFile(list: ItemList) {
        list.uri?.let { uri ->
            try {
                app.contentResolver.openOutputStream(uri).use { out ->
                    out?.write(
                        gson.toJson(list).toByteArray(Charsets.UTF_8)
                    ) // todo check Transient field should not be saved
                }
            } catch (e: Exception) {
                coroutineUiScope.launch {
                    Toast.makeText(
                        app,
                        app.getString(
                            R.string.error_saving_to_path,
                            list.title
                        ), // todo change to path to just error while saving list + LOG IN CRASHLYTICS
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun deleteListBackupFile(list: ItemList) {
        coroutineScope.launch {
            list.uri?.let { uri ->
                try {
                    DocumentFile.fromSingleUri(app, uri)?.delete()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            app,
                            app.getString(R.string.file_deleted),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            app,
                            app.getString(R.string.error_deleting_list_file),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}
