package com.lolo.io.onelist

import android.graphics.Color
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
import kotlinx.android.synthetic.main.fragment_settings.*

class PreferenceFragment : PreferenceFragmentCompat() {


    private val mainActivity: MainActivity
        get() {
            if (activity is MainActivity) return activity as MainActivity
            else throw IllegalStateException("Activity must be MainActivity")
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState)?.apply {
            setBackgroundColor(Color.WHITE)
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)
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
        displayDefaultPath()
        this.preferenceScreen.get<Preference>("version")?.summary = mainActivity.persistence.version
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        if(preference?.key == "storage") {
            defaultPathDialog(mainActivity) { path ->
                mainActivity.persistence.defaultPath = path
                displayDefaultPath()
            }
        } else if (preference?.key == "releaseNote") {
            ReleaseNote.releasesNotes.entries.last().value.show(mainActivity)
        }
        return true
    }

    private fun displayDefaultPath() {
        this.preferenceScreen.get<Preference>("storage")
                ?.summary =
                if (mainActivity.persistence.defaultPath.isNotBlank())
                    mainActivity.persistence.defaultPath
                else getString(R.string.app_private_storage)

    }
}

