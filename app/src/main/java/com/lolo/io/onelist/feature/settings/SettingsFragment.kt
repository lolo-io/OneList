package com.lolo.io.onelist.feature.settings

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import androidx.preference.get
import com.anggrayudi.storage.file.getAbsolutePath
import com.lolo.io.onelist.R
import com.lolo.io.onelist.databinding.FragmentSettingsBinding
import com.lolo.io.onelist.feature.lists.utils.StorageHelperHolder
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.getViewModel

class SettingsFragment : PreferenceFragmentCompat() {

    private val viewModel by lazy { getViewModel<SettingsFragmentViewModel>() }

    private var storageHolder: StorageHelperHolder? = null

    private var _binding: FragmentSettingsBinding? = null
    private val binding: FragmentSettingsBinding
        get() = _binding!!

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        storageHolder = activity as? StorageHelperHolder
    }

    override fun onDetach() {
        super.onDetach()
        storageHolder = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentSettingsBinding.bind(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? AppCompatActivity)?.let { activity ->
            binding.customToolbar.let { activity.setSupportActionBar(it) }
            activity.supportActionBar?.apply {
                title = getString(R.string.settings)
                setDisplayShowHomeEnabled(true)
                setDisplayHomeAsUpEnabled(true)
            }
        }


        this.preferenceScreen.get<Preference>("version")?.summary = viewModel.version

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.backupDisplayPath.collect {
                    (this@SettingsFragment.preferenceScreen.get<Preference>("storage") as? SwitchPreference)?.isChecked =
                        it?.isNotEmpty() == true
                    displayDefaultPath()
                }
            }
        }

        preferenceManager.sharedPreferences?.registerOnSharedPreferenceChangeListener { _, key ->
            when (key) {
                "theme" -> activity?.recreate()
            }
        }
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        when (preference.key) {
            /*  "storage" -> defaultPathDialog(mainActivity) { path ->
                  mainActivity.persistence.defaultPath = path
                  displayDefaultPath()
              }*/
            "storage" -> {
                if ((preference as? SwitchPreference)?.isChecked == true) {
                    storageHolder?.storageHelper?.openFolderPicker()
                    storageHolder?.storageHelper?.onFolderSelected = { _, folder ->
                        viewModel.setBackupUri(
                            uri = folder.uri,
                            displayPath = folder.getAbsolutePath(this@SettingsFragment.requireContext())
                        )
                    }
                } else {
                    viewModel.deleteBackupUri()
                }
            }

            // "releaseNote" -> WhatsNew.releasesNotes.entries.last().value().show(mainActivity)
        }
        return true
    }

    private fun displayDefaultPath() {
        this.preferenceScreen.get<Preference>("storage")
            ?.summary = viewModel.backupDisplayPath.value?.takeIf { it.isNotEmpty() }
            ?: "Select a folder where your lists will be backed up"

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

