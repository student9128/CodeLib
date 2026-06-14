package com.kevin.albummanager.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.LazyPagingItems
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Precision
import com.kevin.albummanager.AlbumManagerConfig
import com.kevin.albummanager.R
import com.kevin.albummanager.bean.AlbumData
import com.kevin.albummanager.loader.AlbumThumbnail
import com.kevin.albummanager.loader.AlbumThumbnailMemoryCache
import com.kevin.albummanager.util.AlbumUtils

@Composable
fun AlbumGrid(
    pagingItems: LazyPagingItems<AlbumData>,
    onItemClick: (Int) -> Unit,
    onSelectClick: (AlbumData) -> Unit,
    onCameraClick: () -> Unit
) {
    val gridState = rememberLazyGridState()
    val loadedImageKeys = remember { mutableStateMapOf<String, Boolean>() }
    val isScrolling by remember {
        derivedStateOf { gridState.isScrollInProgress }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(AlbumManagerConfig.albumManagerConfig.spanCount),
        modifier = Modifier.fillMaxSize(),
        state = gridState,
        contentPadding = PaddingValues(0.dp)
    ) {
        items(
            count = pagingItems.itemCount,
            key = { index ->
                val item = pagingItems.peek(index)
                if (item?.showCameraPlaceholder == true) "camera" else item?.id ?: "placeholder_$index"
            },
            contentType = { index ->
                if (pagingItems.peek(index)?.showCameraPlaceholder == true) "camera" else "album"
            }
        ) { index ->
            val item = pagingItems[index]
            if (item == null) {
                AlbumGridPlaceholder()
                return@items
            }
            if (item.showCameraPlaceholder) {
                CameraItem(onClick = onCameraClick)
            } else {
                val cacheKey = AlbumThumbnail(item.id, item.uri, item.mimeType).cacheKey
                AlbumItem(
                    data = item,
                    loadImage = !isScrolling || loadedImageKeys.containsKey(cacheKey),
                    onClick = { onItemClick(index) },
                    onSelectClick = { onSelectClick(item) },
                    onImageLoaded = { loadedImageKeys[it] = true }
                )
            }
        }
    }
}

@Composable
fun AlbumItem(
    data: AlbumData,
    loadImage: Boolean,
    onClick: () -> Unit,
    onSelectClick: () -> Unit,
    onImageLoaded: (String) -> Unit
) {
    val context = LocalContext.current
    val thumbnail = remember(data.id, data.uriString, data.mimeType) {
        AlbumThumbnail(data.id, data.uri, data.mimeType)
    }
    val cacheKey = thumbnail.cacheKey
    val placeholder = painterResource(id = R.drawable.ic_image_placehodler)
    val cachedBitmap = remember(cacheKey, loadImage) {
        AlbumThumbnailMemoryCache.get(cacheKey)
    }
    val imageModel = remember(context, cacheKey, data.uriString, data.path, loadImage) {
        if (!loadImage) {
            null
        } else {
            ImageRequest.Builder(context)
                .data(thumbnail)
                .crossfade(false)
                .size(240)
                .precision(Precision.INEXACT)
                .allowHardware(true)
                .memoryCacheKey(cacheKey)
                .diskCacheKey(cacheKey)
                .build()
        }
    }
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(1.dp)
            .clickable { onClick() }
    ) {
        if (cachedBitmap != null) {
            Image(
                bitmap = cachedBitmap.asImageBitmap(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else if (imageModel == null) {
            Image(
                painter = placeholder,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            AsyncImage(
                model = imageModel,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                placeholder = placeholder,
                error = painterResource(id = R.drawable.ic_image_error),
                onSuccess = { onImageLoaded(cacheKey) }
            )
        }

        if (data.selected) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colorResource(id = R.color.alphaBlack_1))
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(5.dp)
                .size(25.dp)
                .clickable { onSelectClick() }
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
                    text = selectionText(data),
                    color = Color.White,
                    fontSize = if (AlbumManagerConfig.albumManagerConfig.showNum) 14.sp else 10.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        if (AlbumUtils.isVideo(data.mimeType)) {
            Text(
                text = AlbumUtils.parseTime(data.duration),
                color = Color.White,
                fontSize = 12.sp,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .background(Color.Black.copy(alpha = 0.45f), RoundedCornerShape(2.dp))
                    .padding(horizontal = 4.dp, vertical = 1.dp)
            )
        }
    }
}

@Composable
private fun AlbumGridPlaceholder() {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(1.dp)
            .background(colorResource(id = R.color.gray_1))
    )
}

@Composable
fun CameraItem(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(1.dp)
            .background(colorResource(id = R.color.gray_1))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.ic_camera),
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = "拍摄", color = Color.Black, fontSize = 14.sp)
        }
    }
}

private fun selectionText(data: AlbumData): String {
    return if (AlbumManagerConfig.albumManagerConfig.showNum) {
        (data.selectedIndex + 1).toString()
    } else {
        "✓"
    }
}
