package com.kevin.albummanager.ui

import android.widget.MediaController
import android.widget.VideoView
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Precision
import com.kevin.albummanager.AlbumManagerConfig
import com.kevin.albummanager.R
import com.kevin.albummanager.bean.AlbumData
import com.kevin.albummanager.util.AlbumUtils

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AlbumPreviewScreen(
    items: List<AlbumData>,
    selectedCount: Int,
    initialPage: Int,
    onBackClick: () -> Unit,
    onSelectClick: (AlbumData) -> Unit,
    onSendClick: () -> Unit
) {
    if (items.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "还没有数据 :(", color = Color.White)
        }
        return
    }

    val safeInitialPage = initialPage.coerceIn(0, items.lastIndex)
    val pagerState = rememberPagerState(initialPage = safeInitialPage) { items.size }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            AlbumPreviewPage(data = items[page])
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .statusBarsPadding(),
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
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(colorResource(id = R.color.alphaBlack_1))
                        .padding(5.dp)
                )
            }
            Box(modifier = Modifier.weight(1f))
            PreviewSelectButton(
                data = items[pagerState.currentPage],
                onClick = { onSelectClick(items[pagerState.currentPage]) }
            )
        }

        AlbumPreviewBottomMenu(
            selectedCount = selectedCount,
            onSendClick = onSendClick,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun AlbumPreviewPage(data: AlbumData) {
    val context = LocalContext.current
    val playingVideo = remember { mutableStateMapOf<String, Boolean>() }
    val key = data.path.orEmpty()
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (AlbumUtils.isVideo(data.mimeType) && playingVideo[key] == true) {
            AndroidView(
                factory = {
                    VideoView(it).apply {
                        setVideoPath(data.uri?.toString() ?: data.path.orEmpty())
                        setMediaController(MediaController(it))
                        start()
                    }
                },
                update = {
                    if (!it.isPlaying) it.start()
                },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(data.uri ?: data.path)
                    .crossfade(false)
                    .precision(Precision.INEXACT)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize(),
                placeholder = painterResource(id = R.drawable.ic_image_placehodler),
                error = painterResource(id = R.drawable.ic_image_error)
            )
            if (AlbumUtils.isVideo(data.mimeType)) {
                Image(
                    painter = painterResource(id = R.drawable.ic_play),
                    contentDescription = null,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(colorResource(id = R.color.alphaBlack_1))
                        .padding(15.dp)
                        .clickable { playingVideo[key] = true }
                )
            }
        }
    }
}

@Composable
private fun PreviewSelectButton(
    data: AlbumData,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(end = 15.dp)
            .size(35.dp)
            .clickable { onClick() }
            .then(
                if (data.selected) {
                    Modifier.background(colorResource(id = R.color.colorPrimary), CircleShape)
                } else {
                    Modifier
                        .border(1.dp, Color.White, CircleShape)
                        .background(colorResource(id = R.color.alphaBlack_1), CircleShape)
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        if (data.selected) {
            Text(
                text = if (AlbumManagerConfig.albumManagerConfig.showNum) {
                    (data.selectedIndex + 1).toString()
                } else {
                    "✓"
                },
                color = Color.White,
                fontSize = if (AlbumManagerConfig.albumManagerConfig.showNum) 14.sp else 10.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun AlbumPreviewBottomMenu(
    selectedCount: Int,
    onSendClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .navigationBarsPadding()
            .background(Color.White)
    ) {
        androidx.compose.material3.Button(
            onClick = onSendClick,
            enabled = selectedCount > 0,
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                disabledContainerColor = colorResource(id = R.color.gray),
                contentColor = Color.White,
                disabledContentColor = Color.White
            ),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 15.dp)
        ) {
            Text(
                text = if (selectedCount > 0) "发送($selectedCount)" else "发送",
                fontSize = 16.sp
            )
        }
    }
}
