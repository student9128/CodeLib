package com.kevin.codelib.theme
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp


val AlbumTypography = Typography(
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
//        lineHeight = 24.sp,
        letterSpacing = 0.25.sp,
        color = colorTextPrimary
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
//        lineHeight = 20.sp,
        letterSpacing = 0.25.sp,
        color = colorTextPrimary
    ),
    bodySmall = TextStyle(
        
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
//        lineHeight = 16.sp,
        letterSpacing = 0.25.sp,
        color = colorTextPrimary
    ),
    titleLarge = TextStyle(
        
        fontWeight = FontWeight.W600,
        fontSize = 18.sp,
//        lineHeight = 20.sp,
        letterSpacing = 0.25.sp,
        color = colorTextPrimary
    )
    // 其他 M3 槽位（如 display, label 等）如果不写，将使用系统默认值
)
