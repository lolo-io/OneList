package com.lolo.io.onelist.feature.settings

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidViewBinding
import com.lolo.io.onelist.databinding.FragmentContainerViewBinding
import com.lolo.io.onelist.feature.settings.fragment.SettingsFragment

@Composable
fun SettingsScreen(navigateToWhatsNew: () -> Unit) {
    AndroidViewBinding(
        modifier = Modifier.navigationBarsPadding(),
        factory = FragmentContainerViewBinding::inflate) {
        val settingsFragment = fragmentContainerView.getFragment<SettingsFragment>()
        settingsFragment.onClickOnShowReleaseNote = navigateToWhatsNew
    }
}
