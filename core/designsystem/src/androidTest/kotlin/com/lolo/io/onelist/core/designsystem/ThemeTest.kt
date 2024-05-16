package com.lolo.io.onelist.core.designsystem

import android.os.Build
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.lolo.io.onelist.core.designsystem.colors.darkColorScheme
import com.lolo.io.onelist.core.designsystem.colors.lightColorScheme
import com.lolo.io.onelist.core.testing.core.AbstractComposeTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ThemeTest : AbstractComposeTest() {

    @Test
    fun light() {
        composeTestRule.setContent {
            OneListTheme(
                isDynamic = false,
                isDark = false
            ) {
                val expectedColors = lightColorScheme()

                assertEquals(
                    MaterialTheme.colorScheme.toString(),
                    expectedColors.toString()
                )
            }
        }
    }

    @Test
    fun light_dynamic() {
        composeTestRule.setContent {
            OneListTheme(
                isDynamic = true,
                isDark = false
            ) {
                val expectedColors = dynamicLightColorSchemeWithFallback()

                assertEquals(
                    MaterialTheme.colorScheme.toString(),
                    expectedColors.toString()
                )
            }
        }
    }

    @Test
    fun dark() {
        composeTestRule.activityRule.scenario.onActivity {
            composeTestRule.setContent {
                OneListTheme(
                    isDynamic = false,
                    isDark = true
                ) {
                    val expectedColors = darkColorScheme()

                    assertEquals(
                        MaterialTheme.colorScheme.toString(),
                        expectedColors.toString()
                    )
                }
            }
        }
    }

    @Test
    fun dark_dynamic() {
        composeTestRule.setContent {
            OneListTheme(
                isDynamic = true,
                isDark = true
            ) {
                val expectedColors = dynamicDarkColorSchemeWithFallback()

                assertEquals(
                    MaterialTheme.colorScheme.toString(),
                    expectedColors.toString()
                )
            }
        }
    }

    @Composable
    private fun dynamicLightColorSchemeWithFallback(): ColorScheme {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            dynamicLightColorScheme(LocalContext.current)
        } else {
            lightColorScheme()
        }
    }

    @Composable
    private fun dynamicDarkColorSchemeWithFallback(): ColorScheme {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            dynamicDarkColorScheme(LocalContext.current)
        } else {
            darkColorScheme()
        }
    }
}