package com.kevin.albummanager.ui

import android.content.ContentValues
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.ImageLoader
import coil.compose.LocalImageLoader
import coil.decode.VideoFrameDecoder
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.XXPermissions
import com.hjq.permissions.permission.base.IPermission
import com.kevin.albummanager.AlbumManagerConfig
import com.kevin.albummanager.R
import com.kevin.albummanager.bean.AlbumData
import com.kevin.albummanager.constant.AlbumConstant
import com.kevin.albummanager.loader.AlbumThumbnail
import com.kevin.albummanager.loader.AlbumThumbnailFetcher
import com.kevin.albummanager.loader.AlbumThumbnailKeyer
import com.kevin.albummanager.util.AlbumUtils
import com.kevin.albummanager.util.AppUtils
import com.kevin.albummanager.util.PermissionUtils
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi

open class AlbumComposeActivity : ComponentActivity() {
    private val viewModel: AlbumViewModel by viewModels()
    private var cameraOutputPath: String = ""
    private var cameraOutputUri: Uri? = null

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q && cameraOutputPath.isNotEmpty()) {
                MediaScannerConnection.scanFile(
                    this,
                    arrayOf(cameraOutputPath),
                    null
                ) { _, _ -> runOnUiThread { viewModel.refreshAfterCamera() } }
            } else {
                viewModel.refreshAfterCamera()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppUtils.initAppUtils(this)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        window.statusBarColor = AlbumUtils.getThemeColor(AlbumManagerConfig.albumManagerConfig.theme, this)
        checkPermissions()
        setContent {
            AlbumTheme {
                val imageLoader = rememberAlbumImageLoader()
                CompositionLocalProvider(LocalImageLoader provides imageLoader) {
                    AlbumNavContent(
                        viewModel = viewModel,
                        onBack = { finish() },
                        onCameraClick = { captureMedia() },
                        onSend = { finishWithSelection() },
                        onRequestPermission = { openPermissionSettings() }
                    )
                }
            }
        }
    }

    private fun checkPermissions() {
        XXPermissions.with(this)
            .permissions(mediaPermissions())
            .request(object : OnPermissionCallback {
                override fun onResult(grantedList: List<IPermission>, deniedList: List<IPermission>) {
                    if (deniedList.isEmpty()) {
                        viewModel.loadFolders()
                    } else {
                        viewModel.markNoPermission()
                    }
                }
            })
    }

    private fun mediaPermissions(): List<IPermission> {
        return PermissionUtils.getStorageAndCameraPermissions(
            AlbumManagerConfig.albumManagerConfig.mimeType,
            AlbumManagerConfig.albumManagerConfig.camera
        )
    }

    private fun captureMedia() {
        val type = if (AlbumManagerConfig.albumManagerConfig.mimeType == AlbumConstant.TYPE_VIDEO) {
            AlbumConstant.TYPE_VIDEO
        } else {
            AlbumConstant.TYPE_IMAGE
        }
        val fileDir = File(Environment.getExternalStorageDirectory(), AppUtils.getAppName())
        if (!fileDir.exists()) fileDir.mkdir()
        val isVideo = type == AlbumConstant.TYPE_VIDEO
        val fileName = if (isVideo) {
            "VIDEO_${System.currentTimeMillis()}.mp4"
        } else {
            "IMG_${System.currentTimeMillis()}.jpg"
        }
        cameraOutputPath = "${fileDir.absolutePath}/$fileName"

        val values = ContentValues().apply {
            if (isVideo) {
                put(MediaStore.Video.Media.DISPLAY_NAME, fileName)
                put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Video.Media.RELATIVE_PATH, "DCIM/${AppUtils.getAppName()}")
                } else {
                    put(MediaStore.Video.Media.DATA, cameraOutputPath)
                }
            } else {
                put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/${AppUtils.getAppName()}")
                } else {
                    put(MediaStore.Images.Media.DATA, cameraOutputPath)
                }
            }
        }
        cameraOutputUri = contentResolver.insert(
            if (isVideo) MediaStore.Video.Media.EXTERNAL_CONTENT_URI else MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values
        )
        val intent = Intent(
            if (isVideo) MediaStore.ACTION_VIDEO_CAPTURE else MediaStore.ACTION_IMAGE_CAPTURE
        ).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, cameraOutputUri)
            if (isVideo) {
                putExtra(MediaStore.EXTRA_DURATION_LIMIT, AlbumManagerConfig.albumManagerConfig.shotVideoDuration)
                putExtra(MediaStore.EXTRA_SIZE_LIMIT, (1024 * 1024 * AlbumManagerConfig.albumManagerConfig.shotVideoSize).toLong())
                putExtra(
                    MediaStore.EXTRA_VIDEO_QUALITY,
                    if (AlbumManagerConfig.albumManagerConfig.shotVideoQuality == AlbumManagerConfig.VideoQuality.LOW) 0 else 1
                )
            }
        }
        cameraLauncher.launch(intent)
    }

    private fun finishWithSelection() {
        setResult(
            RESULT_OK,
            Intent().putParcelableArrayListExtra(
                AlbumConstant.SET_RESULT_FOR_SELECTION,
                viewModel.selectedResult()
            )
        )
        finish()
    }

    private fun openPermissionSettings() {
        XXPermissions.startPermissionActivity(this, mediaPermissions())
    }
}

@Composable
@OptIn(ExperimentalCoroutinesApi::class)
private fun rememberAlbumImageLoader(): ImageLoader {
    val context = LocalContext.current
    return remember(context) {
        val imageDispatcher = Dispatchers.IO.limitedParallelism(2)
        ImageLoader.Builder(context)
            .crossfade(false)
            .allowHardware(true)
            .bitmapFactoryMaxParallelism(2)
            .fetcherDispatcher(imageDispatcher)
            .decoderDispatcher(imageDispatcher)
            .memoryCache {
                MemoryCache.Builder(context)
                    .maxSizePercent(0.33)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve("album_image_cache"))
                    .maxSizePercent(0.05)
                    .build()
            }
            .components {
                add(AlbumThumbnailKeyer(), AlbumThumbnail::class.java)
                add(AlbumThumbnailFetcher.Factory(), AlbumThumbnail::class.java)
                add(VideoFrameDecoder.Factory())
            }
            .build()
    }
}

@Composable
private fun AlbumNavContent(
    viewModel: AlbumViewModel,
    onBack: () -> Unit,
    onCameraClick: () -> Unit,
    onSend: () -> Unit,
    onRequestPermission: () -> Unit
) {
    val navController = rememberNavController()
    val pagingItems = viewModel.pagingDataFlow.collectAsLazyPagingItems()
    val albumFolders by viewModel.albumFolders.collectAsState()
    val selectedFolder by viewModel.selectedFolder.collectAsState()
    val selectedAlbumDataList by viewModel.selectedAlbumDataList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val showFolderSheet by viewModel.showFolderSheet.collectAsState()
    val emptyMessage by viewModel.emptyMessage.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.consumeToast()
        }
    }

    NavHost(navController = navController, startDestination = "grid") {
        composable("grid") {
            BackHandler { onBack() }
            Scaffold(
                topBar = {
                    AlbumTopBar(
                        title = selectedFolder?.displayName ?: AlbumConstant.ALBUM_FOLDER_TYPE_DEFAULT,
                        onBackClick = onBack,
                        onTitleClick = {
                            if (albumFolders.isNotEmpty()) viewModel.toggleFolderSheet(true)
                        }
                    )
                },
                bottomBar = {
                    AlbumBottomMenu(
                        selectedCount = selectedAlbumDataList.size,
                        onPreviewClick = { navController.navigate("preview/true/0") },
                        onSendClick = onSend,
                        isOriginal = false,
                        showOriginal = AlbumManagerConfig.albumManagerConfig.showOriginOption,
                        onOriginalToggle = {}
                    )
                },
                snackbarHost = { SnackbarHost(snackbarHostState) }
            ) { padding ->
                Box(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                ) {
                    when {
                        isLoading && pagingItems.itemCount == 0 -> {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                        }
                        pagingItems.itemCount == 0 && pagingItems.loadState.refresh is LoadState.NotLoading -> {
                            AlbumEmptyState(
                                text = emptyMessage,
                                showPermissionButton = emptyMessage.startsWith("未获得"),
                                onRequestPermission = onRequestPermission
                            )
                        }
                        else -> {
                            AlbumGrid(
                                pagingItems = pagingItems,
                                onItemClick = { index ->
                                    val item = pagingItems[index]
                                    if (item != null && !item.showCameraPlaceholder) {
                                        // Preview needs a list. We'll load it on demand in Preview screen if needed,
                                        // or pass the initial index. 
                                        // However, Paging and Preview of "all" is tricky.
                                        // For now, let's just use the index.
                                        var previewIndex = index
                                        if (selectedFolder?.bucketId == -1L && AlbumManagerConfig.albumManagerConfig.camera) {
                                            previewIndex -= 1
                                        }
                                        navController.navigate("preview/false/$previewIndex")
                                    }
                                },
                                onSelectClick = { item -> viewModel.toggleSelection(item) },
                                onCameraClick = onCameraClick
                            )
                        }
                    }

                    if (showFolderSheet) {
                        AlbumFolderOverlay(
                            folders = albumFolders,
                            onFolderClick = { folder -> viewModel.selectFolder(folder) },
                            onDismiss = { viewModel.toggleFolderSheet(false) }
                        )
                    }
                }
            }
        }

        composable(
            route = "preview/{selectedOnly}/{index}",
            arguments = listOf(
                navArgument("selectedOnly") { type = NavType.BoolType },
                navArgument("index") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val selectedOnly = backStackEntry.arguments?.getBoolean("selectedOnly") ?: false
            val initialIndex = backStackEntry.arguments?.getInt("index") ?: 0
            val previewItems = viewModel.currentPreviewItems(selectedOnly)
            AlbumPreviewScreen(
                items = previewItems,
                selectedCount = selectedAlbumDataList.size,
                initialPage = initialIndex,
                onBackClick = { navController.popBackStack() },
                onSelectClick = { item: AlbumData -> viewModel.toggleSelection(item) },
                onSendClick = onSend
            )
        }
    }
}

@Composable
private fun AlbumEmptyState(
    text: String,
    showPermissionButton: Boolean,
    onRequestPermission: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_image),
            contentDescription = null,
            modifier = Modifier.size(80.dp)
        )
        Text(
            text = text,
            modifier = Modifier.padding(top = 10.dp),
            color = Color.Black
        )
        if (showPermissionButton) {
            androidx.compose.material3.Button(
                onClick = onRequestPermission,
                modifier = Modifier.padding(top = 10.dp)
            ) {
                Text(text = "去打开权限")
            }
        }
    }
}
