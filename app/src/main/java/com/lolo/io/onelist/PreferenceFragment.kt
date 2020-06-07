package com.lolo.io.onelist

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.get
import com.lolo.io.onelist.dialogs.defaultPathDialog
import com.lolo.io.onelist.updates.ReleaseNote
import com.lolo.io.onelist.updates.show
import com.lolo.io.onelist.util.beautify
import com.lolo.io.onelist.util.toUri
import kotlinx.android.synthetic.main.fragment_settings.*


class PreferenceFragment : PreferenceFragmentCompat() {


    private val mainActivity: MainActivity
        get() {
            if (activity is MainActivity) return activity as MainActivity
            else throw IllegalStateException("Activity must be MainActivity")
        }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = activity as AppCompatActivity
        customToolbar?.let { activity.setSupportActionBar(it) }
        activity.supportActionBar?.apply {
            title = getString(R.string.settings)
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

       // this.preferenceScreen.get<Preference>("theme")?.summary = mainActivity.persistence.theme
        displayDefaultPath()
        this.preferenceScreen.get<Preference>("version")?.summary = mainActivity.persistence.version


        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener { _, key ->
            when(key) {
                "theme" -> activity.recreate()/*{
                    activity.finish()
                    activity.baseContext.packageManager.getLaunchIntentForPackage(activity.baseContext.packageName)?.let {
                        it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(it)
                    }
                }*/
            }
        }
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        when (preference?.key) {
            "storage" -> defaultPathDialog(mainActivity) { path ->
                mainActivity.persistence.defaultPath = path
                displayDefaultPath()
            }
            "releaseNote" -> ReleaseNote.releasesNotes.entries.last().value().show(mainActivity)
        }
        return true
    }

    private fun displayDefaultPath() {
        this.preferenceScreen.get<Preference>("storage")
                ?.summary =
                if (mainActivity.persistence.defaultPath.isNotBlank()) {
                    val uri = mainActivity.persistence.defaultPath.toUri
                    uri?.path?.beautify() ?: mainActivity.persistence.defaultPath
                }
                else getString(R.string.app_private_storage)

    }
}

