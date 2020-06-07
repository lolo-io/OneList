package com.lolo.io.onelist.updates

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lolo.io.onelist.MainActivity
import com.lolo.io.onelist.R
import com.lolo.io.onelist.model.ItemList
import com.lolo.io.onelist.util.loadJSONFromAsset

object UpdateHelper {
    fun applyUpdatePatches(activity: MainActivity) {
        val persistence = activity.persistence
        if (persistence.compat.firstLaunchCompat) {
            val tutos : ArrayList<ItemList> = Gson().fromJson(loadJSONFromAsset(activity, "tuto-${activity.getString(R.string.locale)}.json"), object : TypeToken<List<ItemList>>() {
            }.type)
            persistence.updateListIdsTable(tutos)
            tutos.forEach { persistence.saveList(it) }
            persistence.compat.firstLaunchCompat = false
        } else if (!persistence.compat.firstLaunchCompat && !persistence.version.startsWith("1.")) {
            val lists = arrayListOf<ItemList>()
            migrateToMaj1Min1(activity, lists)
            persistence.updateListIdsTable(lists)
            lists.forEach { persistence.saveList(it) }
            ReleaseNote.releasesNotes["1.1"]?.invoke()?.show(activity)
        } else if (!persistence.version.startsWith(ReleaseNote.releasesNotes.keys.last().toString())) {
            ReleaseNote.releasesNotes.values.last().invoke().show(activity)
        }
    }

    private fun migrateToMaj1Min1(activity: MainActivity, lists: MutableList<ItemList>) = lists.addAll(activity.persistence.compat.allListsCompat)
}