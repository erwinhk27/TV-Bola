package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.StarballCyanPrimary
import com.example.ui.theme.StarballNavySurface
import com.example.ui.theme.StarballNavySurfaceVariant
import com.example.ui.theme.WhatsAppDarkGreen
import com.example.ui.theme.WhatsAppGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareOptionsModal(
    sheetState: SheetState,
    isPreparingApk: Boolean,
    onDismissRequest: () -> Unit,
    onShareApkWhatsApp: () -> Unit,
    onShareLinkWhatsApp: () -> Unit,
    onShareApkGeneral: () -> Unit,
    onShareLinkGeneral: () -> Unit,
    onCopyLink: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = StarballNavySurface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        modifier = Modifier.testTag("share_options_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 36.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(WhatsAppGreen.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Share",
                        tint = WhatsAppGreen,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Share Starball TV",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Share the APK or stream link with friends",
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Primary WhatsApp Section
            Text(
                text = "WHATSAPP SHARING",
                color = WhatsAppGreen,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            // 1. Share APK via WhatsApp
            ShareOptionCard(
                title = "Send APK via WhatsApp",
                description = "Transfers the StarballTV.apk file directly into WhatsApp chat",
                icon = Icons.Default.Android,
                iconBg = WhatsAppGreen,
                iconTint = Color.White,
                badge = "Direct APK",
                badgeBg = WhatsAppGreen.copy(alpha = 0.25f),
                badgeColor = WhatsAppGreen,
                isLoading = isPreparingApk,
                onClick = onShareApkWhatsApp,
                testTag = "share_apk_whatsapp_option"
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 2. Share Web Link via WhatsApp
            ShareOptionCard(
                title = "Send Web Link via WhatsApp",
                description = "Shares https://starballtv.pages.dev/ link directly",
                icon = Icons.Default.Link,
                iconBg = WhatsAppDarkGreen,
                iconTint = Color.White,
                badge = "Link Only",
                badgeBg = WhatsAppDarkGreen.copy(alpha = 0.25f),
                badgeColor = WhatsAppGreen,
                onClick = onShareLinkWhatsApp,
                testTag = "share_link_whatsapp_option"
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = StarballNavySurfaceVariant)
            Spacer(modifier = Modifier.height(14.dp))

            // General Sharing Section
            Text(
                text = "OTHER WAYS TO SHARE",
                color = Color(0xFF64748B),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            // 3. Share APK to any app (Telegram, Drive, Bluetooth, etc.)
            ShareOptionCard(
                title = "Share APK File (All Apps)",
                description = "Send StarballTV.apk via Telegram, Bluetooth, Drive, or Email",
                icon = Icons.Default.Share,
                iconBg = StarballCyanPrimary.copy(alpha = 0.2f),
                iconTint = StarballCyanPrimary,
                isLoading = isPreparingApk,
                onClick = onShareApkGeneral,
                testTag = "share_apk_general_option"
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 4. Copy stream link
            ShareOptionCard(
                title = "Copy Stream Link",
                description = "Copies https://starballtv.pages.dev/ to clipboard",
                icon = Icons.Default.ContentCopy,
                iconBg = Color(0xFF334155),
                iconTint = Color.White,
                onClick = onCopyLink,
                testTag = "copy_link_option"
            )
        }
    }
}

@Composable
fun ShareOptionCard(
    title: String,
    description: String,
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    badge: String? = null,
    badgeBg: Color = Color.Transparent,
    badgeColor: Color = Color.White,
    isLoading: Boolean = false,
    onClick: () -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(StarballNavySurfaceVariant)
            .clickable(enabled = !isLoading, onClick = onClick)
            .padding(14.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = iconTint,
                    strokeWidth = 2.5.dp,
                    modifier = Modifier.size(22.dp)
                )
            } else {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                if (badge != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(badgeBg)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = badge,
                            color = badgeColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Text(
                text = description,
                color = Color(0xFF94A3B8),
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        }
    }
}
