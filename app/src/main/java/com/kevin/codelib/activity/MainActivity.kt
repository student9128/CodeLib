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
import android.os.Environment
import android.provider.Settings
import android.view.View
import com.blankj.utilcode.util.ToastUtils
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.XXPermissions
import com.kevin.codelib.R
import com.kevin.codelib.base.BaseActivity
import com.kevin.codelib.util.DisplayUtils
import com.kevin.codelib.util.LogUtils
import kotlinx.android.synthetic.main.activity_main.*
//import org.apache.poi.hssf.usermodel.HSSFCellStyle
//import org.apache.poi.hssf.usermodel.HSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.lang.reflect.InvocationTargetException

class MainActivity : BaseActivity() {
    private val permissionList = arrayListOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )

    override fun getLayoutResID(): Int = R.layout.activity_main

    override fun initView() {
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        btn_custom_view.setOnClickListener {
            startNewActivity(CustomViewActivity::class.java)
        }
        btn_share_anim.setOnClickListener {
            startNewActivity(ShareActivity::class.java)
        }
//        btn_function.setOnClickListener {
//            startNewActivity(FunctionActivity::class.java)
//        }
        btn_animation.setOnClickListener({
            startNewActivity(AnimationActivity::class.java)
        })
        val function: (View) -> Unit = {
//            var intent = Intent(Intent.ACTION_VIEW)
//            intent.setData(Uri.parse("market://details?id=" + "com.changqi.yeka_app_2c"))
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            if (intent.resolveActivity(packageManager)!= null) {
//                startActivity(intent)
//            }
            LogUtils.logD(TAG, Build.BRAND)
            var x = goToSamsungMarket(this, "com.changqi.yeka_app_2c")
            ToastUtils.showShort("${x}")
            val hasAnyMarketInstalled = hasAnyMarketInstalled(this)
//            LogUtils.logD(TAG,"hasAnyMarketInstalled:$hasAnyMarketInstalled")

        }
        btn_go_market.setOnClickListener(function)
        btn_get_app_sign_md5.setOnClickListener {
            startNewActivity(AppSignMD5Activity::class.java)
        }
        btn_get_imei.setOnClickListener {
            startNewActivity(PhoneIMEIActivity::class.java)
        }
        btn_photo.setOnClickListener {
//            XXPermissions.with(this)
//                .permission(permissionList)
//                .request(object : OnPermissionCallback {
//                    override fun onGranted(permissions: MutableList<String>?, all: Boolean) {
            startNewActivity(PhotoActivity::class.java)
//                    }
//
//                    override fun onDenied(permissions: MutableList<String>?, never: Boolean) {
////                    if(never){
////                    XXPermissions.startPermissionActivity(this@PhotoActivity, permissions)
////                    }else{
////                        ToastUtils.showShort("授权失败")
////                    }
//                        val intent = Intent()
//                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                        val packageName: String = packageName
//                        intent.setAction("com.meizu.safe.security.SHOW_APPSEC");
//                        intent.addCategory(Intent.CATEGORY_DEFAULT);
//                        intent.putExtra("packageName", packageName);
//                        startActivity(intent)
//                    }
//
//                })
        }
        btn_camera.setOnClickListener {
            XXPermissions.with(this)
                .permission(permissionList)
                .request(object : OnPermissionCallback {
                    override fun onGranted(permissions: MutableList<String>?, all: Boolean) {
//                        val getImageByCamera = Intent("android.media.action.IMAGE_CAPTURE")
                        // 图片路径？照相后图片要存储的位置
                        // 图片路径？照相后图片要存储的位置
//                        picPath = getPicName()
//                        // 指定输出路径
//                        // 指定输出路径
//                        getImageByCamera.putExtra(
//                            MediaStore.EXTRA_OUTPUT,
//                            Uri.fromFile(File(picPath))
//                        )
//                        getImageByCamera.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1)
//                        startActivityForResult(getImageByCamera, 1001)
                        startNewActivity(CameraActivity::class.java)
                    }

                    override fun onDenied(permissions: MutableList<String>?, never: Boolean) {
//                    if(never){
//                    XXPermissions.startPermissionActivity(this@PhotoActivity, permissions)
//                    }else{
                        ToastUtils.showShort("授权失败")
//                    }
//                        val intent = Intent()
//                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                        val packageName: String = packageName
//                        intent.setAction("com.meizu.safe.security.SHOW_APPSEC");
//                        intent.addCategory(Intent.CATEGORY_DEFAULT);
//                        intent.putExtra("packageName", packageName);
//                        startActivity(intent)
                    }

                })

        }
        btn_go_setting.setOnClickListener {
            val intent = Intent()
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            intent.setData(Uri.parse("package:" + getPackageName()))
            startActivity(intent)
        }
        btn_go_notification.setOnClickListener {
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
        btn_generate_excel.setOnClickListener {
//            generateExcel()
        }
    }

//    fun generateExcel() {
//        val columnString = arrayListOf<String>("A", "B", "C", "D")
//        try {
//            val hssfWorkbook = HSSFWorkbook()
//            val sheet = hssfWorkbook.createSheet()
//            sheet.setColumnWidth(0, 256*10) // 第一列的宽度为2000
//            sheet.setColumnWidth(1, 250*30) // 第二列的宽度为3000
//            for (rowNum in 0..10) {
//                val createRow = sheet.createRow(rowNum)
//                for (columnIndex in 0 until columnString.size) {
//                    if (rowNum == 0) {
//                        val cell = createRow.createCell(columnIndex)
//                        cell.setCellValue(columnString[columnIndex])
//                    } else {
//                        val cell = createRow.createCell(columnIndex)
//                        cell.setCellValue("Hello=$columnIndex,rowNum=$rowNum")
//                    }
//                }
//            }
//            val xx =
//                getExternalFilesDir(null)?.absolutePath + File.separator + "AAA" + File.separator + "Hello.xls"
//            val absolutePath = Environment.getStorageDirectory()?.absolutePath
//            val absolutePath1 = Environment.getRootDirectory()?.absolutePath
//            val externalStorageDirectory =
//                Environment.getExternalStorageDirectory()?.absolutePath + File.separator + "AAA" + File.separator + "Hello.xls"
//            printD("absolutePath=$absolutePath,absolutePath1=$absolutePath1,externalStorageDirectory=$externalStorageDirectory")
//            printD("xx=$xx")
//            val file = File(xx)
//
//            printD("file.parentFile=${file.parentFile.absolutePath}")
//            if (!file.parentFile.exists()) {
//                file.parentFile.mkdirs()
//            }
//            printD("file.exists()=${file.exists()}")
//            if (!file.exists()) {
//                file.createNewFile()
//            }
//            val fileOutputStream = FileOutputStream(file)
//            hssfWorkbook.write(fileOutputStream)
//            fileOutputStream.close()
//        } catch (e: Exception) {
//
//        }
//
//    }

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
//            Uri.parse("http://www.samsungapps.com/appquery/appDetail.as?appId=$packageName")
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