package com.lolo.io.onelist.core.data.migration

import android.content.Context
import android.content.SharedPreferences
import android.health.connect.datatypes.units.Length
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lolo.io.onelist.App
import com.lolo.io.onelist.core.data.reporitory.OneListRepository
import com.lolo.io.onelist.core.data.shared_preferences.SharedPreferencesHelper
import com.lolo.io.onelist.core.database.dao.ItemListDao
import com.lolo.io.onelist.core.model.ItemList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.ref.WeakReference

class UpdateHelper(
    private val preferences: SharedPreferencesHelper,
    private val repository: OneListRepository
) {

    private var oldListsIds: Map<Long, String> = linkedMapOf()

    private var activityWR: WeakReference<FragmentActivity>? = null

    private val oldVersionPref: String = "version"
    private val oldSelectedListPref = "selectedList"
    private val oldListIdsPref = "listsIds"
    private val oldDefaultPathPref = "defaultPath"
    private val oldThemePref: String = "theme"

    fun hasToMigratePrefs(activity: FragmentActivity): Boolean {
        val activityPreferences = activity.getPreferences(Context.MODE_PRIVATE)
        return activityPreferences.getString(oldVersionPref, null) != null
    }

    fun applyUpdatePatches(activity: FragmentActivity, then: () -> Unit) {
        val activityPreferences = activity.getPreferences(Context.MODE_PRIVATE)
        val editor = activityPreferences.edit()

        activityWR = WeakReference(activity)

        preferences.version = activityPreferences.getString(oldVersionPref, "0.0.0") ?: "0.0.0"
        editor.putString(oldVersionPref, null)

        preferences.selectedListIndex = activityPreferences.getInt(oldSelectedListPref, 0)
        editor.putString(oldSelectedListPref, null)

        preferences.theme = activityPreferences.getString(oldThemePref, "auto") ?: "auto"
        editor.putString(oldThemePref, null)

        val oldLists = getAllLists(activityPreferences)
        CoroutineScope(Dispatchers.IO).launch {
            oldLists.forEach {
                repository.createList(it.copy(id = 0))
            }
            then()
        }

        editor.putString(oldListIdsPref, null)
        editor.putString(oldDefaultPathPref, null)

        preferences.firstLaunch = false

        editor.apply()
    }


    private fun getAllLists(sp: SharedPreferences): List<ItemList> {
        val gson = Gson()
        return runBlocking {
            oldListsIds = getListIdsTable()
            try {
                val ret = oldListsIds.map {
                    gson.fromJson(sp.getString(it.key.toString(), ""), ItemList::class.java)
                }
                ret
            } catch (e: Exception) {
                listOf<ItemList>()
            }
        }
    }

    private fun getListIdsTable(): Map<Long, String> {
        return activityWR?.get()?.let { act ->
            val sp = act.getPreferences(Context.MODE_PRIVATE)
            val gson = Gson()
            val json =
                sp.getString(oldListIdsPref, null)?.replace("\\", "")?.removeSurrounding("\"")
            return if (json != null) {
                gson.fromJson(json, object : TypeToken<Map<Long, String>>() {
                }.type)
            } else mapOf()
        } ?: mapOf()
    }

}