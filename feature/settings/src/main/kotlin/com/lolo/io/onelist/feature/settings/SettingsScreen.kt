package com.lolo.io.onelist.feature.settings

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.viewinterop.AndroidViewBinding
import com.anggrayudi.storage.SimpleStorageHelper
import com.lolo.io.onelist.core.data.utils.TestTags
import com.lolo.io.onelist.feature.settings.databinding.FragmentContainerViewBinding
import com.lolo.io.onelist.feature.settings.fragment.SettingsFragment

@Composable
fun SettingsScreen(
    simpleStorageHelper: SimpleStorageHelper,
    navigateToWhatsNew: () -> Unit) {
    AndroidViewBinding(
        modifier = Modifier.navigationBarsPadding().testTag(TestTags.SettingsScreen),
        factory = FragmentContainerViewBinding::inflate) {
        fragmentContainerView.getFragment<SettingsFragment>().apply {
            onClickOnShowReleaseNote = navigateToWhatsNew
            this.storageHelper = simpleStorageHelper
        }
    }
}
