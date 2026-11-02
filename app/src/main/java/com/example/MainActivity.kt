package com.example

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.WindowManager
import android.webkit.WebView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.ui.components.OfflineErrorView
import com.example.ui.components.QuickLinksBottomSheet
import com.example.ui.components.ShareOptionsModal
import com.example.ui.components.StarballWebView
import com.example.ui.components.TopNavBar
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.StarballCyanPrimary
import com.example.ui.theme.StarballNavyDark
import com.example.util.ApkSharingHelper
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

  companion object {
    const val HOME_URL = "https://starballtv.pages.dev/"
    const val FALLBACK_HUB_URL = "https://starballtv012.blogspot.com/"
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    // Keep screen awake while streaming live sports
    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

    setContent {
      MyApplicationTheme {
        StarballTvApp(
          onToggleSystemBars = { hide ->
            val insetsController = WindowCompat.getInsetsController(window, window.decorView)
            insetsController.systemBarsBehavior =
              WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            if (hide) {
              insetsController.hide(WindowInsetsCompat.Type.systemBars())
            } else {
              insetsController.show(WindowInsetsCompat.Type.systemBars())
            }
          }
        )
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StarballTvApp(
  onToggleSystemBars: (Boolean) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()

  var targetUrl by remember { mutableStateOf(MainActivity.HOME_URL) }
  var currentUrl by remember { mutableStateOf(MainActivity.HOME_URL) }
  var pageTitle by remember { mutableStateOf("Starball TV") }
  var isLoading by remember { mutableStateOf(true) }
  var progress by remember { mutableIntStateOf(0) }
  var canGoBack by remember { mutableStateOf(false) }
  var canGoForward by remember { mutableStateOf(false) }
  var hasError by remember { mutableStateOf(false) }
  var isCustomViewShowing by remember { mutableStateOf(false) }
  var isImmersive by remember { mutableStateOf(false) }
  var showMenuSheet by remember { mutableStateOf(false) }
  var showShareSheet by remember { mutableStateOf(false) }
  var isPreparingApk by remember { mutableStateOf(false) }
  var webViewRef by remember { mutableStateOf<WebView?>(null) }
  var lastBackPressTime by remember { mutableLongStateOf(0L) }

  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val shareSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  // Sync system bars with immersive or custom video view state
  LaunchedEffect(isImmersive, isCustomViewShowing) {
    onToggleSystemBars(isImmersive || isCustomViewShowing)
  }

  // Handle hardware Back button
  BackHandler(enabled = !isCustomViewShowing) {
    if (canGoBack && webViewRef != null) {
      webViewRef?.goBack()
    } else {
      val now = System.currentTimeMillis()
      if (now - lastBackPressTime < 2000) {
        (context as? ComponentActivity)?.finish()
      } else {
        lastBackPressTime = now
        Toast.makeText(context, "Press back again to exit Starball TV", Toast.LENGTH_SHORT).show()
      }
    }
  }

  Scaffold(
    topBar = {
      TopNavBar(
        isVisible = !isImmersive && !isCustomViewShowing,
        pageTitle = pageTitle,
        isLoading = isLoading,
        progress = progress,
        canGoBack = canGoBack,
        canGoForward = canGoForward,
        isImmersive = isImmersive,
        onBack = { webViewRef?.goBack() },
        onForward = { webViewRef?.goForward() },
        onRefresh = {
          hasError = false
          webViewRef?.reload()
        },
        onToggleImmersive = {
          isImmersive = !isImmersive
        },
        onOpenMenu = {
          showMenuSheet = true
        },
        onShare = {
          showShareSheet = true
        }
      )
    },
    containerColor = StarballNavyDark,
    modifier = modifier.fillMaxSize().testTag("starball_app_scaffold")
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(if (isImmersive || isCustomViewShowing) androidx.compose.foundation.layout.PaddingValues(0.dp) else innerPadding)
        .background(StarballNavyDark)
    ) {
      // Main WebView
      StarballWebView(
        currentUrl = targetUrl,
        onUrlChange = { url ->
          currentUrl = url
        },
        onTitleChange = { title ->
          pageTitle = title
        },
        onLoadingChange = { loading ->
          isLoading = loading
        },
        onProgressChange = { p ->
          progress = p
        },
        onCanGoBackChange = { back ->
          canGoBack = back
        },
        onCanGoForwardChange = { forward ->
          canGoForward = forward
        },
        onErrorChange = { error ->
          hasError = error
        },
        onCustomViewChange = { customShowing ->
          isCustomViewShowing = customShowing
        },
        onWebViewCreated = { webView ->
          webViewRef = webView
        }
      )

      // Floating button to exit immersive mode when active
      if (isImmersive && !isCustomViewShowing) {
        FloatingActionButton(
          onClick = { isImmersive = false },
          containerColor = StarballCyanPrimary.copy(alpha = 0.85f),
          contentColor = StarballNavyDark,
          modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(16.dp)
            .testTag("exit_immersive_fab")
        ) {
          Icon(
            imageVector = Icons.Default.FullscreenExit,
            contentDescription = "Exit Fullscreen"
          )
        }
      }

      // Offline Error View if connection fails
      if (hasError) {
        OfflineErrorView(
          onRetry = {
            hasError = false
            webViewRef?.reload()
          },
          onFallbackServer = {
            hasError = false
            targetUrl = MainActivity.FALLBACK_HUB_URL
            webViewRef?.loadUrl(MainActivity.FALLBACK_HUB_URL)
          }
        )
      }
    }
  }

  // Quick Links & Action Sheet
  if (showMenuSheet) {
    QuickLinksBottomSheet(
      sheetState = sheetState,
      onDismissRequest = { showMenuSheet = false },
      onSelectUrl = { newUrl ->
        hasError = false
        targetUrl = newUrl
        webViewRef?.loadUrl(newUrl)
        scope.launch { sheetState.hide() }.invokeOnCompletion {
          showMenuSheet = false
        }
      },
      onRefresh = {
        hasError = false
        webViewRef?.reload()
      },
      onClearCache = {
        webViewRef?.clearCache(true)
        Toast.makeText(context, "Stream cache cleared", Toast.LENGTH_SHORT).show()
        webViewRef?.reload()
      },
      onOpenExternal = {
        val activeUrl = webViewRef?.url ?: currentUrl
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(activeUrl))
        try {
          context.startActivity(intent)
        } catch (_: Exception) {
          Toast.makeText(context, "No web browser found", Toast.LENGTH_SHORT).show()
        }
      },
      onShare = {
        showMenuSheet = false
        showShareSheet = true
      },
      onShareApkWhatsApp = {
        scope.launch {
          isPreparingApk = true
          Toast.makeText(context, "Preparing StarballTV.apk for WhatsApp...", Toast.LENGTH_SHORT).show()
          ApkSharingHelper.shareApk(context, targetWhatsAppOnly = true) { success, msg ->
            isPreparingApk = false
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
          }
        }
      }
    )
  }

  // Share Options Modal (WhatsApp APK, Link, General)
  if (showShareSheet) {
    ShareOptionsModal(
      sheetState = shareSheetState,
      isPreparingApk = isPreparingApk,
      onDismissRequest = { showShareSheet = false },
      onShareApkWhatsApp = {
        scope.launch {
          isPreparingApk = true
          Toast.makeText(context, "Preparing StarballTV.apk for WhatsApp...", Toast.LENGTH_SHORT).show()
          ApkSharingHelper.shareApk(context, targetWhatsAppOnly = true) { _, msg ->
            isPreparingApk = false
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            scope.launch { shareSheetState.hide() }.invokeOnCompletion {
              showShareSheet = false
            }
          }
        }
      },
      onShareLinkWhatsApp = {
        ApkSharingHelper.shareLinkViaWhatsApp(context)
        scope.launch { shareSheetState.hide() }.invokeOnCompletion {
          showShareSheet = false
        }
      },
      onShareApkGeneral = {
        scope.launch {
          isPreparingApk = true
          Toast.makeText(context, "Preparing StarballTV.apk...", Toast.LENGTH_SHORT).show()
          ApkSharingHelper.shareApk(context, targetWhatsAppOnly = false) { _, msg ->
            isPreparingApk = false
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            scope.launch { shareSheetState.hide() }.invokeOnCompletion {
              showShareSheet = false
            }
          }
        }
      },
      onShareLinkGeneral = {
        val shareUrl = webViewRef?.url ?: currentUrl
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
          type = "text/plain"
          putExtra(Intent.EXTRA_SUBJECT, pageTitle)
          putExtra(Intent.EXTRA_TEXT, "Watch live sports on Starball TV: $shareUrl")
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Starball TV"))
        scope.launch { shareSheetState.hide() }.invokeOnCompletion {
          showShareSheet = false
        }
      },
      onCopyLink = {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Starball TV Link", ApkSharingHelper.SHARE_LINK)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Link copied to clipboard!", Toast.LENGTH_SHORT).show()
        scope.launch { shareSheetState.hide() }.invokeOnCompletion {
          showShareSheet = false
        }
      }
    )
  }
}
