package com.kevin.codelib.activity

import android.Manifest
import android.app.AppOpsManager
import android.app.NotificationManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.View
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import android.graphics.RuntimeShader
import android.graphics.RenderEffect
import com.blankj.utilcode.util.ToastUtils
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.XXPermissions
import com.hjq.permissions.permission.PermissionLists
import com.hjq.permissions.permission.base.IPermission
import com.kevin.albummanager.util.PermissionUtils
import com.kevin.codelib.R
import com.kevin.codelib.base.BaseActivity
import com.kevin.codelib.util.LogUtils
import com.kevin.codelib.databinding.ActivityMainBinding
import java.lang.reflect.InvocationTargetException

class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding

    private var permissionList = PermissionUtils.getStorageAndCameraPermissions()

    override fun getLayoutView(): View {
        binding = ActivityMainBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun setupToolbar() {
        super.setupToolbar()
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(false)
            it.setHomeButtonEnabled(false)
        }
    }

    override fun initView() {
        binding.btnCustomView.setOnClickListener {
            startNewActivity(CustomViewActivity::class.java)
        }
        binding.btnShareAnim.setOnClickListener {
            startNewActivity(ShareActivity::class.java)
        }
        binding.btnAnimation.setOnClickListener({
            startNewActivity(AnimationActivity::class.java)
        })
        val function: (View) -> Unit = {
            LogUtils.logD(TAG, Build.BRAND)
            var x = goToSamsungMarket(this, "com.changqi.yeka_app_2c")
            ToastUtils.showShort("${x}")
            val hasAnyMarketInstalled = hasAnyMarketInstalled(this)
        }
        binding.btnGoMarket.setOnClickListener(function)
        binding.btnGetAppSignMd5.setOnClickListener {
            startNewActivity(AppSignMD5Activity::class.java)
        }
        binding.btnGetImei.setOnClickListener {
            startNewActivity(PhoneIMEIActivity::class.java)
        }
        binding.btnPhoto.setOnClickListener {
            startNewActivity(PhotoActivity::class.java)
        }
        binding.btnCamera.setOnClickListener {
            XXPermissions.with(this)
                .permissions(permissionList)
                .request(object : OnPermissionCallback {
                    override fun onResult(grantedList: List<IPermission>, deniedList: List<IPermission>) {
                        if (deniedList.isEmpty()) {
                            startNewActivity(CameraActivity::class.java)
                        } else {
                            ToastUtils.showShort("授权失败")
                        }
                    }
                })

        }
        binding.btnGoSetting.setOnClickListener {
            val intent = Intent()
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            intent.setData(Uri.parse("package:" + getPackageName()))
            startActivity(intent)
        }
        binding.btnGoNotification.setOnClickListener {
            val intent = Intent()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName())
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
                intent.putExtra("app_package", getPackageName())
                intent.putExtra("app_uid", getApplicationInfo().uid)
            } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.addCategory(Intent.CATEGORY_DEFAULT)
                intent.setData(Uri.parse("package:" + getPackageName()))
            } else {
                ///< 4.4以下没有从app跳转到应用通知设置页面的Action，可考虑跳转到应用详情页面,
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (Build.VERSION.SDK_INT >= 9) {
                    intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                    intent.setData(Uri.fromParts("package", getPackageName(), null));
                } else if (Build.VERSION.SDK_INT <= 8) {
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setClassName(
                        "com.android.settings",
                        "com.android.setting.InstalledAppDetails"
                    );
                    intent.putExtra("com.android.settings.ApplicationPkgName", getPackageName());
                }
            }
            startActivity(intent)
        }
        binding.btnGenerateExcel.setOnClickListener {
//            generateExcel()
        }
        binding.btnGetApplicationInfo.setOnClickListener {
            startNewActivity(AppInfoListActivity::class.java)
        }

        initComposeView()
    }

    private fun initComposeView() {
        binding.composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MaterialTheme {
                    var isPathAnimating by remember { mutableStateOf(false) }
                    var isShaderAnimating by remember { mutableStateOf(false) }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black) // 背景改成黑色
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        Text(
                            text = "Compose Border Beam 效果展示", 
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White // 标题改为白色以适应黑色背景
                        )

                        // 方案二：PathMeasure 实现 (兼容性好)
                        BorderBeamPathMeasure(
                            modifier = Modifier.width(200.dp).height(60.dp),
                            beamColor = Color.White,
                            borderWidth = 1.dp,
                            cornerRadius = 16.dp,
                            isAnimating = isPathAnimating
                        ) {
                            Surface(
                                onClick = { isPathAnimating = !isPathAnimating },
                                color = Color.Blue,
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(if (isPathAnimating) "停止 Path 动画" else "开始 Path 动画", color = Color.White)
                                }
                            }
                        }

                        // 方案一：AGSL Shader 实现 (API 33+)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            BorderBeamShader(
                                modifier = Modifier.width(200.dp).height(60.dp),
                                beamColor = Color.White,
                                borderWidth = 1.dp,
                                cornerRadius = 16.dp,
                                isAnimating = isShaderAnimating
                            ) {
                                Surface(
                                    onClick = { isShaderAnimating = !isShaderAnimating },
                                    color = Color.Blue,
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(if (isShaderAnimating) "停止 Shader 动画" else "开始 Shader 动画", color = Color.White)
                                    }
                                }
                            }
                        } else {
                            Text(
                                "AGSL 方案需要 Android 13+ (API 33)", 
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }

    private fun hasAnyMarketInstalled(context: Context): Boolean {
        val intent = Intent()
        intent.data = Uri.parse("market://details?id=android.browser")
        val list: List<ResolveInfo> = context.getPackageManager()
            .queryIntentActivities(
                intent,
                PackageManager.MATCH_DEFAULT_ONLY
            )
        return 0 != list.size
    }

    /**
     * 跳转三星应用商店
     * @param context [Context]
     * @param packageName 包名
     * @return `true` 跳转成功 <br></br> `false` 跳转失败
     */
    fun goToSamsungMarket(
        context: Context,
        packageName: String
    ): Boolean {
        val uri =
            Uri.parse("http://apps.samsung.com/appquery/appDetail.as?appId=" + packageName);
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("com.sec.android.app.samsungapps")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return try {
            context.startActivity(intent)
            true
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            false
        }
    }

    //判断通知权限是否打开
    private fun isEnableV19(context: Context): Boolean {
        val CHECK_OP_NO_THROW = "checkOpNoThrow"
        val OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION"
        val mAppOps = context.getSystemService(APP_OPS_SERVICE) as AppOpsManager
        val appInfo = context.applicationInfo
        val pkg = context.applicationContext.packageName
        val uid = appInfo.uid
        var appOpsClass: Class<*>? = null /* Context.APP_OPS_MANAGER */
        try {
            appOpsClass = Class.forName(AppOpsManager::class.java.name)
            val checkOpNoThrowMethod = appOpsClass.getMethod(
                CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE,
                String::class.java
            )
            val opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION)
            val value = opPostNotificationValue[Int::class.java] as Int
            return checkOpNoThrowMethod.invoke(
                mAppOps,
                value,
                uid,
                pkg
            ) as Int == AppOpsManager.MODE_ALLOWED
        } catch (e: ClassNotFoundException) {
        } catch (e: NoSuchMethodException) {
        } catch (e: NoSuchFieldException) {
        } catch (e: InvocationTargetException) {
        } catch (e: IllegalAccessException) {
        } catch (e: Exception) {
        }
        return false
    }

    //判断通知权限是否打开
    private fun isEnableV26(context: Context): Boolean {
        val appInfo = context.applicationInfo
        val pkg = context.applicationContext.packageName
        val uid = appInfo.uid
        return try {
            val notificationManager =
                context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val sServiceField = notificationManager.javaClass.getDeclaredMethod("getService")
            sServiceField.isAccessible = true
            val sService = sServiceField.invoke(notificationManager)
            val method = sService.javaClass.getDeclaredMethod(
                "areNotificationsEnabledForPackage",
                String::class.java,
                Integer.TYPE
            )
            method.isAccessible = true
            method.invoke(sService, pkg, uid) as Boolean
        } catch (e: Exception) {
            true
        }
    }
}

/**
 * 方案二：使用 PathMeasure 实现 Border Beam
 * 优点：全版本兼容，逻辑清晰
 */
@Composable
fun BorderBeamPathMeasure(
    modifier: Modifier = Modifier,
    duration: Int = 2000,
    beamColor: Color = Color.White,
    beamLength: Float = 0.2f, // 占总周长的比例
    borderWidth: Dp = 2.dp,
    cornerRadius: Dp = 8.dp,
    isAnimating: Boolean = true,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "borderBeam")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(duration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "progress"
    )

    Box(modifier = modifier) {
        // 内容
        Box(modifier = Modifier.fillMaxSize()) {
            content()
        }

        // 边框动画层
        if (isAnimating) {
            Canvas(modifier = Modifier.matchParentSize()) {
                val strokeWidthPx = borderWidth.toPx()
                val cornerRadiusPx = cornerRadius.toPx()
                
                // 修正绘制区域，使其居中在边框线上
                val rect = Rect(
                    offset = Offset(0f, 0f),
                    size = size
                )
                
                val path = Path().apply {
                    addRoundRect(RoundRect(rect, CornerRadius(cornerRadiusPx)))
                }
                
                val pathMeasure = PathMeasure()
                pathMeasure.setPath(path, false)
                val totalLength = pathMeasure.length
                
                val beamLengthPx = totalLength * beamLength
                val startDistance = progress * totalLength
                
                val segmentPath = Path()
                val endDistance = startDistance + beamLengthPx
                
                if (endDistance <= totalLength) {
                    pathMeasure.getSegment(startDistance, endDistance, segmentPath, true)
                } else {
                    pathMeasure.getSegment(startDistance, totalLength, segmentPath, true)
                    pathMeasure.getSegment(0f, endDistance - totalLength, segmentPath, false)
                }
                
                drawPath(
                    path = segmentPath,
                    color = beamColor,
                    style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
                )
            }
        }
    }
}

/**
 * 方案一：使用 AGSL 着色器实现 Border Beam
 * 优点：性能最高，效果最细腻
 * 仅支持 Android 13 (API 33) 及以上
 */
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun BorderBeamShader(
    modifier: Modifier = Modifier,
    duration: Int = 2000,
    beamColor: Color = Color.White,
    borderWidth: Dp = 2.dp,
    cornerRadius: Dp = 8.dp,
    isAnimating: Boolean = true,
    content: @Composable () -> Unit
) {
    val shaderSrc = """
        uniform float2 size;
        uniform float time;
        layout(color) uniform half4 beamColor;
        uniform float borderWidth;
        uniform float cornerRadius;

        // 计算点到圆角矩形的距离
        float sdRoundRect(float2 p, float2 b, float r) {
            float2 q = abs(p) - b + r;
            return min(max(q.x, q.y), 0.0) + length(max(q, 0.0)) - r;
        }

        half4 main(float2 fragCoord) {
            float2 halfSize = size * 0.5;
            float2 p = fragCoord - halfSize;
            float2 b = halfSize - cornerRadius;
            
            // 1. 标准的 Euclidean 距离场计算，确保拐角厚度绝对均匀
            float2 q = abs(p) - b;
            float d = length(max(q, 0.0)) + min(max(q.x, q.y), 0.0) - cornerRadius;
            
            // 2. 极精细抗锯齿与偏移调整：
            // 将流光中心向内移动，使其 98% 位于内部。
            // 偏移量 = borderWidth * (0.5 - 0.02) = borderWidth * 0.48 (向内偏移为负)
            float offset = -borderWidth * 0.48;
            float edgeDist = abs(d - offset);
            
            float halfWidth = borderWidth * 0.5;
            float edgeAlpha = smoothstep(halfWidth + 1.0, halfWidth, edgeDist);
            
            if (edgeAlpha <= 0.0) return half4(0.0);

            // 3. 连续且均匀的进度计算：使用比例修正后的 atan，消除长方形导致的流光变形
            // 通过对坐标进行长宽比缩放，确保在长方形边缘的角速度映射到周长进度上是均匀的
            float progress = fract(atan(p.y * size.x, p.x * size.y) / 6.283185 + time);
            
            // 4. Beam 效果
            float beam = smoothstep(0.0, 0.15, progress) * smoothstep(0.3, 0.15, progress);
            
            return beamColor * beam * edgeAlpha;
        }
    """.trimIndent()

    val shader = remember(shaderSrc) { RuntimeShader(shaderSrc) }
    val infiniteTransition = rememberInfiniteTransition(label = "shaderBeam")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(duration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )

    Box(modifier = modifier) {
        Box(modifier = Modifier.fillMaxSize()) {
            content()
        }
        
        if (isAnimating) {
            Canvas(modifier = Modifier.matchParentSize()) {
                shader.setFloatUniform("size", this.size.width, this.size.height)
                shader.setFloatUniform("time", time)
                shader.setFloatUniform("borderWidth", borderWidth.toPx())
                shader.setFloatUniform("cornerRadius", cornerRadius.toPx())
                shader.setColorUniform("beamColor", beamColor.toArgb())
                
                drawRect(
                    brush = ShaderBrush(shader)
                )
            }
        }
    }
}
