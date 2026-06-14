package com.kevin.codelib.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val AlbumShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp), // 用于 Tooltip, Menu 上的小元素
    small = RoundedCornerShape(4.dp),      // 用于 Button, TextField
    medium = RoundedCornerShape(4.dp),     // 用于 Card, Dialog
    large = RoundedCornerShape(8.dp),      // 用于 BottomSheet, Navigation Drawer
    extraLarge = RoundedCornerShape(12.dp)  // 用于大型 Modal
)
val shapeCard = RoundedCornerShape(cornerCard)
val shapeButton = RoundedCornerShape(cornerButton)
val shapeDialog = RoundedCornerShape(cornerDialog)
val shapeBottomDialog = RoundedCornerShape(cornerBottomDialog)
val shapeShare = RoundedCornerShape(cornerShare)
val shapePopup = RoundedCornerShape(cornerPopup)
val shapeCircleTab = RoundedCornerShape(cornerCircleTab)
val shapeLabel = RoundedCornerShape(cornerLabel)
val shapeTextField = RoundedCornerShape(cornerTextField)
val shapeCommon = RoundedCornerShape(cornerCommon)
val shapeSwiper = RoundedCornerShape(cornerSwiper)

