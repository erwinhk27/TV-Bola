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
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.example.ui.theme.StarballGold
import com.example.ui.theme.StarballLiveRed
import com.example.ui.theme.StarballNavySurface
import com.example.ui.theme.StarballNavySurfaceVariant

data class QuickLinkItem(
    val title: String,
    val subtitle: String,
    val url: String,
    val icon: ImageVector,
    val iconTint: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickLinksBottomSheet(
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    onSelectUrl: (String) -> Unit,
    onRefresh: () -> Unit,
    onClearCache: () -> Unit,
    onOpenExternal: () -> Unit,
    onShare: () -> Unit,
    onShareApkWhatsApp: () -> Unit
) {
    val quickLinks = listOf(
        QuickLinkItem(
            title = "Starball TV Main Portal",
            subtitle = "https://starballtv.pages.dev/",
            url = "https://starballtv.pages.dev/",
            icon = Icons.Default.Home,
            iconTint = StarballCyanPrimary
        ),
        QuickLinkItem(
            title = "Starball Live Stream Hub",
            subtitle = "Live matches & sport channels",
            url = "https://starballtv012.blogspot.com/",
            icon = Icons.Default.Tv,
            iconTint = StarballLiveRed
        ),
        QuickLinkItem(
            title = "Football & Sports Channels",
            subtitle = "Premier League, Champions League, La Liga",
            url = "https://starballtv012.blogspot.com/search?q=live",
            icon = Icons.Default.SportsSoccer,
            iconTint = StarballGold
        )
    )

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = StarballNavySurface,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        modifier = Modifier.testTag("quick_links_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(StarballCyanPrimary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SportsSoccer,
                        contentDescription = "Starball TV",
                        tint = StarballCyanPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Starball TV Navigation",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Quick links and stream tools",
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = StarballNavySurfaceVariant)
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "STREAM CHANNELS & HUBS",
                color = Color(0xFF64748B),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(vertical = 6.dp)
            )

            quickLinks.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            onSelectUrl(item.url)
                            onDismissRequest()
                        }
                        .padding(vertical = 10.dp, horizontal = 8.dp)
                        .testTag("quick_link_${item.title.lowercase().replace(" ", "_")}"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(item.iconTint.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            tint = item.iconTint,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.title,
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = item.subtitle,
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = StarballNavySurfaceVariant)
            Spacer(modifier = Modifier.height(12.dp))

            // WhatsApp Quick Share Promo Card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(com.example.ui.theme.WhatsAppDarkGreen.copy(alpha = 0.35f))
                    .clickable {
                        onShareApkWhatsApp()
                        onDismissRequest()
                    }
                    .padding(12.dp)
                    .testTag("quick_share_whatsapp_btn"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(com.example.ui.theme.WhatsAppGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share APK",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Share APK via WhatsApp",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(com.example.ui.theme.WhatsAppGreen)
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "READY",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                    Text(
                        text = "Send StarballTV.apk directly in chat",
                        color = Color(0xFFCBD5E1),
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = StarballNavySurfaceVariant)
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "ACTIONS",
                color = Color(0xFF64748B),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(vertical = 6.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ActionChip(
                    title = "Reload",
                    icon = Icons.Default.Refresh,
                    onClick = {
                        onRefresh()
                        onDismissRequest()
                    },
                    modifier = Modifier.weight(1f).testTag("action_reload")
                )
                ActionChip(
                    title = "Clear Cache",
                    icon = Icons.Default.Cached,
                    onClick = {
                        onClearCache()
                        onDismissRequest()
                    },
                    modifier = Modifier.weight(1f).testTag("action_clear_cache")
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ActionChip(
                    title = "Share Link",
                    icon = Icons.Default.Share,
                    onClick = {
                        onShare()
                        onDismissRequest()
                    },
                    modifier = Modifier.weight(1f).testTag("action_share")
                )
                ActionChip(
                    title = "External Browser",
                    icon = Icons.Default.OpenInBrowser,
                    onClick = {
                        onOpenExternal()
                        onDismissRequest()
                    },
                    modifier = Modifier.weight(1f).testTag("action_open_external")
                )
            }
        }
    }
}

@Composable
fun ActionChip(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(StarballNavySurfaceVariant)
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = StarballCyanPrimary,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
