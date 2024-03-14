package com.lolo.io.onelist.core.design

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp

@Composable
fun typography(colorScheme: ColorScheme) = Typography(
    bodyLarge = TextStyle(
        color = colorScheme.onBackground,
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
//    displayLarge = TypographyTokens.DisplayLarge,
//    displayMedium = TypographyTokens.DisplayMedium,
//    displaySmall = TypographyTokens.DisplaySmall,
//    headlineLarge = TypographyTokens.HeadlineLarge,
//    headlineMedium = TypographyTokens.HeadlineMedium,
//    headlineSmall = TypographyTokens.HeadlineSmall,
//    titleLarge = TypographyTokens.TitleLarge,
//    titleMedium = TypographyTokens.TitleMedium,
//    titleSmall = TypographyTokens.TitleSmall,
//    bodyMedium = TypographyTokens.BodyMedium,
//    bodySmall = TypographyTokens.BodySmall,
//    labelLarge = TypographyTokens.LabelLarge,
//    labelMedium = TypographyTokens.LabelMedium,
//    labelSmall = TypographyTokens.LabelSmall,
)



val Typography.app: AppTypography
    @Composable get() = appTypography(MaterialTheme.colorScheme)


data class AppTypography (
    val itemTitle: TextStyle,
    val itemComment: TextStyle,
    val itemTitleDone: TextStyle,
    val itemCommentDone: TextStyle,

)

@Composable
fun appTypography(colorScheme: ColorScheme): AppTypography {

    val itemComment = TextStyle(
        color = colorScheme.app.itemComment,
        fontFamily = FontFamily.Default,
        fontStyle = FontStyle.Italic,
        fontSize = 12.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )

    return AppTypography(
        itemTitle = MaterialTheme.typography.bodyLarge,
        itemComment = itemComment,
        itemTitleDone = MaterialTheme.typography.bodyLarge.copy(
            color = colorScheme.app.itemDone,
            textDecoration = TextDecoration.LineThrough
        ),
        itemCommentDone = itemComment.copy(
            textDecoration = TextDecoration.LineThrough
        ),
    )
}