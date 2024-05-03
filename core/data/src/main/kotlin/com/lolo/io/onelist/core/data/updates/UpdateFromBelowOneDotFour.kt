package com.lolo.io.onelist.core.data.updates

import android.content.Context
import android.content.SharedPreferences
import androidx.fragment.app.FragmentActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lolo.io.onelist.core.data.repository.OneListRepository
import com.lolo.io.onelist.core.data.shared_preferences.SharedPreferencesHelper
import com.lolo.io.onelist.core.model.ItemList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.ref.WeakReference
import java.security.SecureRandom
import kotlin.math.abs

class UpdateFromBelowOneDotFour(
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

    fun update(activity: FragmentActivity, then: () -> Unit) {
        if (hasToMigratePrefs(activity)) {
            applyUpdatePatches(activity, then)
        } else {
            then()
        }
    }


    private fun hasToMigratePrefs(activity: FragmentActivity): Boolean {
        val activityPreferences = activity.getPreferences(Context.MODE_PRIVATE)
        return activityPreferences.getString(oldVersionPref, null) != null
    }

    private fun applyUpdatePatches(activity: FragmentActivity, then: () -> Unit) {
        val activityPreferences = activity.getPreferences(Context.MODE_PRIVATE)
        val editor = activityPreferences.edit()

        activityWR = WeakReference(activity)

        preferences.version = activityPreferences.getString(oldVersionPref, "0.0.0") ?: "0.0.0"
        editor.putString(oldVersionPref, null)

        preferences.selectedListIndex = activityPreferences.getInt(oldSelectedListPref, 0)
        editor.putString(oldSelectedListPref, null)

        preferences.theme = activityPreferences.getString(oldThemePref, "auto") ?: "auto"
        editor.putString(oldThemePref, null)

        val secureRandom = SecureRandom()

        val oldLists = getAllLists(activityPreferences)
        CoroutineScope(Dispatchers.IO).launch {
            oldLists.forEach {
                it.items.forEach {
                    it.id = abs(secureRandom.nextLong())
                }
                repository.createList(it.copy(id = 0))
            }
            then()
        }

        editor.putString(oldListIdsPref, null)
        editor.putString(oldDefaultPathPref, null)

        preferences.firstLaunch = false

        editor.apply()
    }


    private fun getAllLists(sp: SharedPreferences): List<com.lolo.io.onelist.core.model.ItemList> {
        val gson = Gson()
        return runBlocking {
            oldListsIds = getListIdsTable()
            try {
                val ret = oldListsIds.map {
                    gson.fromJson(
                        sp.getString(it.key.toString(), ""),
                        ItemList::class.java
                    )
                }
                ret
            } catch (e: Exception) {
                listOf()
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

