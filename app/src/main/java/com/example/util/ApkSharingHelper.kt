package com.example.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

object ApkSharingHelper {

    private const val APK_NAME = "StarballTV.apk"
    private const val WHATSAPP_PKG = "com.whatsapp"
    private const val WHATSAPP_BIZ_PKG = "com.whatsapp.w4b"
    const val SHARE_LINK = "https://starballtv.pages.dev/"

    fun isWhatsAppInstalled(context: Context): Boolean {
        return isPackageInstalled(context, WHATSAPP_PKG) || isPackageInstalled(context, WHATSAPP_BIZ_PKG)
    }

    private fun getInstalledWhatsAppPackage(context: Context): String? {
        if (isPackageInstalled(context, WHATSAPP_PKG)) return WHATSAPP_PKG
        if (isPackageInstalled(context, WHATSAPP_BIZ_PKG)) return WHATSAPP_BIZ_PKG
        return null
    }

    private fun isPackageInstalled(context: Context, packageName: String): Boolean {
        return try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(
                    packageName,
                    PackageManager.PackageInfoFlags.of(PackageManager.GET_ACTIVITIES.toLong())
                )
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            }
            true
        } catch (_: PackageManager.NameNotFoundException) {
            false
        }
    }

    suspend fun prepareApkFile(context: Context): File? = withContext(Dispatchers.IO) {
        try {
            val sourceDir = context.applicationInfo.sourceDir ?: return@withContext null
            val sourceFile = File(sourceDir)
            if (!sourceFile.exists()) return@withContext null

            val apkDir = File(context.cacheDir, "apks").apply { mkdirs() }
            val targetFile = File(apkDir, APK_NAME)

            // Copy source APK to target share file
            FileInputStream(sourceFile).use { input ->
                FileOutputStream(targetFile).use { output ->
                    input.copyTo(output)
                }
            }
            targetFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun shareApk(
        context: Context,
        targetWhatsAppOnly: Boolean = false,
        onComplete: (Boolean, String) -> Unit
    ) {
        val apkFile = prepareApkFile(context)
        if (apkFile == null || !apkFile.exists() || apkFile.length() == 0L) {
            withContext(Dispatchers.Main) {
                onComplete(false, "Could not extract APK file for sharing")
            }
            return
        }

        try {
            val authority = "${context.packageName}.fileprovider"
            val uri: Uri = FileProvider.getUriForFile(context, authority, apkFile)

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/vnd.android.package-archive"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Starball TV App APK")
                putExtra(
                    Intent.EXTRA_TEXT,
                    "Here is the Starball TV Android app APK! ⚽ ⭐\nWatch free live football & sports streams.\nOfficial website: $SHARE_LINK"
                )
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val waPkg = getInstalledWhatsAppPackage(context)
            if (targetWhatsAppOnly && waPkg != null) {
                shareIntent.setPackage(waPkg)
                withContext(Dispatchers.Main) {
                    try {
                        context.startActivity(shareIntent)
                        onComplete(true, "Opening WhatsApp...")
                    } catch (_: Exception) {
                        // Fallback to chooser
                        val chooser = Intent.createChooser(shareIntent, "Share Starball TV APK via WhatsApp")
                        chooser.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        context.startActivity(chooser)
                        onComplete(true, "Choose an app to share the APK")
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    val chooserTitle = if (targetWhatsAppOnly) "Share APK via WhatsApp" else "Share Starball TV APK"
                    val chooser = Intent.createChooser(shareIntent, chooserTitle)
                    chooser.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    context.startActivity(chooser)
                    onComplete(true, "Choose WhatsApp or any app to send APK")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                onComplete(false, "Sharing failed: ${e.localizedMessage}")
            }
        }
    }

    fun shareLinkViaWhatsApp(context: Context) {
        val waPkg = getInstalledWhatsAppPackage(context)
        val message = "Watch Live Football & Sports on Starball TV! ⚽ ⭐\n$SHARE_LINK"
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, message)
            if (waPkg != null) {
                setPackage(waPkg)
            }
        }
        try {
            context.startActivity(intent)
        } catch (_: Exception) {
            val chooser = Intent.createChooser(intent, "Share via WhatsApp")
            context.startActivity(chooser)
        }
    }
}
