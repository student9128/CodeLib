package com.kevin.albummanager.ui

import android.content.Context
import androidx.annotation.ColorInt
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Precision
import com.kevin.albummanager.AlbumManagerConfig
import com.kevin.albummanager.R
import com.kevin.albummanager.bean.AlbumFolder
import com.kevin.albummanager.constant.AlbumTheme
import com.kevin.albummanager.util.AlbumUtils
import com.kevin.albummanager.util.AppUtils

@Composable
fun AlbumTheme(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val primary = Color(AlbumUtils.getThemeColor(AlbumManagerConfig.albumManagerConfig.theme, context))
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(
            primary = primary,
            secondary = primary,
            surface = Color.White,
            background = Color.White
        ),
        content = content
    )
}

@Composable
fun AlbumFolderOverlay(
    folders: List<AlbumFolder>,
    onFolderClick: (AlbumFolder) -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.dimBackground))
            .clickable { onDismiss() }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.66f)
                .background(Color.White)
                .clickable(enabled = false) {},
            contentPadding = PaddingValues(start = 0.dp, end = 16.dp)
        ) {
            items(folders, key = { "${it.bucketId}_${it.displayName}" }) { folder ->
                AlbumFolderRow(
                    folder = folder,
                    onClick = {
                        onFolderClick(folder)
                        onDismiss()
                    }
                )
            }
        }
    }
}

@Composable
private fun AlbumFolderRow(
    folder: AlbumFolder,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(top = 10.dp, bottom = 10.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(folder.coverUri ?: folder.coverUriString)
                .size(120)
                .precision(Precision.INEXACT)
                .build(),
            contentDescription = null,
            modifier = Modifier.size(60.dp),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.ic_image_placehodler),
            error = painterResource(id = R.drawable.ic_image_error)
        )
        Column(
            modifier = Modifier
                .padding(start = 10.dp)
                .weight(1f)
        ) {
            Text(
                text = folder.displayName,
                color = Color.Black,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = folder.count.toString(),
                color = colorResource(id = R.color.colorPrimaryDark),
                fontSize = 14.sp
            )
        }
        if (folder.checked) {
            Image(
                painter = painterResource(id = R.drawable.ic_check),
                contentDescription = null,
                modifier = Modifier
                    .size(56.dp)
                    .padding(16.dp)
            )
        } else {
            Spacer(modifier = Modifier.width(56.dp))
        }
    }
}

@Composable
fun AlbumTopBar(
    title: String,
    onBackClick: () -> Unit,
    onTitleClick: () -> Unit
) {
    val context = LocalContext.current
    val primary = Color(AlbumUtils.getThemeColor(AlbumManagerConfig.albumManagerConfig.theme, context))
    Surface(
        color = primary,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .height(56.dp)
                .padding(horizontal = 0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back_material_light),
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.padding(8.dp)
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(25.dp))
                        .background(expandBackgroundColor(context))
                        .clickable { onTitleClick() }
                        .padding(horizontal = 10.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_down),
                        contentDescription = "Expand",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(56.dp))
        }
    }
}

@Composable
fun AlbumBottomMenu(
    selectedCount: Int,
    onPreviewClick: () -> Unit,
    onSendClick: () -> Unit,
    isOriginal: Boolean,
    showOriginal: Boolean,
    onOriginalToggle: () -> Unit
) {
    val enabled = selectedCount > 0
    val primary = MaterialTheme.colorScheme.primary
    Surface(
        color = colorResource(id = R.color.alphaWhite_1),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .height(56.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = if (enabled) "预览($selectedCount)" else "预览",
                color = if (enabled) Color.Black else colorResource(id = R.color.gray),
                fontSize = 16.sp,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 15.dp)
                    .width(80.dp)
                    .fillMaxHeight()
                    .clickable(enabled = enabled) { onPreviewClick() }
                    .wrapContentHeight(Alignment.CenterVertically)
            )
            if (showOriginal) {
                Row(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .clickable { onOriginalToggle() },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (isOriginal) "原图" else "原图",
                        color = Color.Black,
                        fontSize = 16.sp
                    )
                }
            }
            Button(
                onClick = onSendClick,
                enabled = enabled,
                colors = ButtonDefaults.buttonColors(
                    containerColor = primary,
                    disabledContainerColor = colorResource(id = R.color.gray),
                    contentColor = Color.White,
                    disabledContentColor = Color.White
                ),
                shape = RoundedCornerShape(5.dp),
                contentPadding = PaddingValues(horizontal = 15.dp, vertical = 5.dp),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 15.dp)
            ) {
                Text(
                    text = if (enabled) "发送($selectedCount)" else "发送",
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
private fun expandBackgroundColor(context: Context): Color {
    @ColorInt val baseColor = when (AlbumManagerConfig.albumManagerConfig.theme) {
        AlbumTheme.Default -> context.getColorCompat(R.color.lightBlue)
        AlbumTheme.Red -> context.getColorCompat(R.color.redPrimaryAlpha)
        AlbumTheme.Pink -> context.getColorCompat(R.color.pinkPrimaryAlpha)
        AlbumTheme.Purple -> context.getColorCompat(R.color.purplePrimaryAlpha)
        AlbumTheme.DeepPurple -> context.getColorCompat(R.color.deepPurplePrimaryAlpha)
        AlbumTheme.Indigo -> context.getColorCompat(R.color.indigoPrimaryAlpha)
        AlbumTheme.LightBlue -> context.getColorCompat(R.color.lightBluePrimaryAlpha)
        AlbumTheme.Cyan -> context.getColorCompat(R.color.cyanPrimaryAlpha)
        AlbumTheme.Teal -> context.getColorCompat(R.color.tealPrimaryAlpha)
        AlbumTheme.Green -> context.getColorCompat(R.color.greenPrimaryAlpha)
        AlbumTheme.Amber -> context.getColorCompat(R.color.amberPrimaryAlpha)
        AlbumTheme.Orange -> context.getColorCompat(R.color.orangePrimaryAlpha)
        AlbumTheme.BlueGrey -> context.getColorCompat(R.color.blueGreyPrimaryAlpha)
    }
    return Color(AppUtils.addAlphaForColor(0.3f, baseColor))
}

@ColorInt
private fun Context.getColorCompat(colorRes: Int): Int = androidx.core.content.ContextCompat.getColor(this, colorRes)
