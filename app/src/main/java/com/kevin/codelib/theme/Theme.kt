package com.kevin.codelib.theme

import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density


private val AlbumColorScheme = lightColorScheme(
    primary = colorPrimary,
    onPrimary = colorTextPrimary,

    // M3 中没有 primaryVariant，通常对应 primaryContainer 或直接复用 primary
    primaryContainer = colorPrimary,
    onPrimaryContainer = colorTextPrimary,

    secondary = colorSecondary,
    onSecondary = colorTextPrimary,

    surface = colorBackgroundSecondary,
    onSurface = colorTextPrimary,

    background = colorBackground,
    onBackground = colorTextPrimary,

    error = colorRed,
    onError = colorTextPrimary,

    surfaceContainerLow = colorBackgroundSecondary,
)

@Composable
fun AlbumTheme(content: @Composable () -> Unit) {
    val density = LocalDensity.current

    val fixedDensity = remember(density) {
        Density(
            density = density.density,
            fontScale = 1f
        )
    }
    // 使用新的 LocalRippleConfiguration 替代 LocalRippleTheme
    // 设置为 null 可以禁用 Material 3 组件的默认水波纹效果
    CompositionLocalProvider(
        LocalDensity provides fixedDensity,
        LocalRippleConfiguration provides null,
        LocalIndication provides NoIndication,
        LocalOverscrollFactory provides null
    ) {
        MaterialTheme(
            colorScheme = AlbumColorScheme, typography = AlbumTypography, shapes = AlbumShapes
        ) {
            content()
        }
    }
}

object NoIndication : IndicationNodeFactory {
    override fun create(interactionSource: InteractionSource): DelegatableNode {
        return NoIndicationNode()
    }

    override fun equals(other: Any?): Boolean = other === this

    override fun hashCode(): Int = -1
}

private class NoIndicationNode : Modifier.Node(), DrawModifierNode {
    override fun ContentDrawScope.draw() {
        // 这里什么都不画，所以点击时没有任何视觉反馈
        drawContent()
    }
}
