package com.lolo.io.onelist.updates

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lolo.io.onelist.model.ItemList
import com.lolo.io.onelist.MainActivity
import com.lolo.io.onelist.R
import com.lolo.io.onelist.util.loadJSONFromAsset

object UpdateHelper {
    fun applyUpdatePatches(activity: MainActivity, allLists: MutableList<ItemList>) {
        val persistence = activity.persistence
        if (persistence.firstLaunchCompat) {
            allLists.addAll(Gson().fromJson(loadJSONFromAsset(activity, "tuto-${activity.getString(R.string.locale)}.json"), object : TypeToken<List<ItemList>>() {
            }.type))
            persistence.updateListIdsTable(allLists)
            allLists.forEach { persistence.saveList(it) }
            persistence.firstLaunchCompat = false
        } else if (!persistence.firstLaunchCompat && !persistence.version.startsWith("1.1")) {
            migrateToMaj1Min1(activity, allLists)
            persistence.updateListIdsTable(allLists)
            allLists.forEach { persistence.saveList(it) }
            ReleaseNote.releasesNotes["1.1"]?.show(activity)
        }
    }

    private fun migrateToMaj1Min1(activity: MainActivity, allLists: MutableList<ItemList>) = allLists.addAll(activity.persistence.allListsCompat)
}