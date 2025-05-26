package com.app.tastybuds.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.tastybuds.R
import com.app.tastybuds.util.AppTopBar

@Preview
@Composable
fun ProfileScreen(
    onBackClick: () -> Unit = {},
    isDarkMode: Boolean = false,
    onToggleDarkMode: (Boolean) -> Unit = {},
    onSignOut: () -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        AppTopBar(title = "Profile", onBackClick = onBackClick)

        Spacer(modifier = Modifier.height(16.dp))

        // Profile header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.profile_img),
                contentDescription = "User Avatar",
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text("User name", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.width(32.dp))
                Text("example@gmail.com", color = Color.Gray, fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Divider(color = Color(0xFFE0E0E0))

        ProfileOption(icon = loadVector(R.drawable.ic_profile_setting), label = "Edit profile")
        ProfileOption(icon = loadVector(R.drawable.ic_settings), label = "Preferences")

        Divider(color = Color(0xFFE0E0E0))
        // Night Mode Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(loadVector(R.drawable.ic_night_mode), contentDescription = null, tint = Color.DarkGray)
                Spacer(modifier = Modifier.width(16.dp))
                Text("Night mode", fontSize = 16.sp)
            }
            Switch(
                checked = isDarkMode,
                onCheckedChange = onToggleDarkMode,
                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFFFF7700))
            )
        }

        Divider(color = Color(0xFFE0E0E0))

        ProfileOption(icon = loadVector(R.drawable.ic_go_pro), label = "Go Pro")
        ProfileOption(icon = loadVector(R.drawable.ic_help), label = "Help center")

        Divider(color = Color(0xFFE0E0E0))

        ProfileOption(icon = loadVector(R.drawable.ic_logout), label = "Sign out", onClick = onSignOut)
    }
}

@Composable
fun ProfileOption(icon: ImageVector, label: String, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = Color.DarkGray)
        Spacer(modifier = Modifier.width(16.dp))
        Text(label, fontSize = 16.sp)
    }
}

@Composable
fun loadVector(id: Int): ImageVector {
    return ImageVector.vectorResource(id = id)
}

