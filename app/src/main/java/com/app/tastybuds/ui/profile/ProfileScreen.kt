package com.app.tastybuds.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.tastybuds.LocalThemeManager
import com.app.tastybuds.R
import com.app.tastybuds.domain.model.User
import com.app.tastybuds.ui.login.LoginViewModel
import com.app.tastybuds.ui.theme.*
import com.app.tastybuds.util.ui.AppTopBar
import com.app.tastybuds.util.ui.showDevelopmentToast
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    onBackClick: () -> Unit = {},
    onSignOut: () -> Unit = {},
    onEditProfile: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel(),
    loginViewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val loginUiState by loginViewModel.uiState.collectAsState()
    val userIdFlow by loginViewModel.getUserId().collectAsState(initial = "user_001")

    val themeManager = LocalThemeManager.current
    val isDarkMode by themeManager.isDarkMode.collectAsState(false)

    LaunchedEffect(userIdFlow) {
        userIdFlow?.let { userId ->
            if (userId.isNotEmpty()) {
                viewModel.initialize(userId)
            }
        }
    }

    LaunchedEffect(loginUiState.logoutTriggered) {
        if (loginUiState.logoutTriggered) {
            loginViewModel.onLogoutNavigationHandled()
            onSignOut()
        }
    }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor())
    ) {

        AppTopBar(title = stringResource(R.string.profile), onBackClick = onBackClick)

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = primaryColor())
                }
            }

            uiState.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = stringResource(R.string.error_loading_profile),
                            color = errorColor(),
                            fontSize = 16.sp
                        )
                        Text(
                            text = uiState.error ?: "",
                            color = textSecondaryColor(),
                            fontSize = 14.sp
                        )
                    }
                }
            }

            else -> {
                ProfileContent(
                    user = uiState.user,
                    isDarkMode = isDarkMode,
                    onToggleDarkMode = {
                        scope.launch {
                            themeManager.toggleDarkMode()
                        }
                    },
                    onEditProfile = onEditProfile,
                    onLogout = {
                        loginViewModel.onLogoutNavigationHandled()
                        loginViewModel.logout()
                        onSignOut()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun ProfileContent(
    user: User?,
    isDarkMode: Boolean,
    onToggleDarkMode: (Boolean) -> Unit,
    onEditProfile: () -> Unit,
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current
    Spacer(modifier = Modifier.height(16.dp))

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 24.dp)
    ) {
        if (!user?.profileUrl.isNullOrBlank()) {
            GlideImage(
                model = user?.profileUrl,
                contentDescription = "User Avatar",
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                failure = placeholder(R.drawable.default_food),
                loading = placeholder(R.drawable.default_food)
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.default_food),
                contentDescription = "User Avatar",
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = user?.name ?: stringResource(R.string.user_name),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = onSurfaceColor()
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = user?.email ?: "example@gmail.com",
                color = textSecondaryColor(),
                fontSize = 14.sp
            )
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    HorizontalDivider(color = dividerColor())

    ProfileOption(
        icon = loadVector(R.drawable.ic_profile_setting),
        label = stringResource(R.string.edit_profile),
        onClick = { onEditProfile() }
    )
    ProfileOption(
        icon = loadVector(R.drawable.ic_settings),
        label = stringResource(R.string.preferences),
        onClick = { context.showDevelopmentToast() }
    )

    HorizontalDivider(color = dividerColor())

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = loadVector(R.drawable.ic_night_mode),
                contentDescription = stringResource(R.string.dark_mode),
                modifier = Modifier.size(24.dp),
                tint = onSurfaceVariantColor()
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = stringResource(R.string.dark_mode),
                fontSize = 16.sp,
                color = onSurfaceColor()
            )
        }
        Switch(
            checked = isDarkMode,
            onCheckedChange = onToggleDarkMode,
            colors = SwitchDefaults.colors(
                checkedThumbColor = primaryColor(),
                checkedTrackColor = primaryColor().copy(alpha = 0.5f),
                uncheckedThumbColor = onSurfaceVariantColor(),
                uncheckedTrackColor = surfaceVariantColor()
            )
        )
    }

    HorizontalDivider(color = dividerColor())

    ProfileOption(
        icon = loadVector(R.drawable.ic_help),
        label = stringResource(R.string.help_support),
        onClick = { context.showDevelopmentToast() }
    )
    ProfileOption(
        icon = loadVector(R.drawable.ic_go_pro),
        label = stringResource(R.string.go_pro),
        onClick = { context.showDevelopmentToast() }
    )
    ProfileOption(
        icon = loadVector(R.drawable.ic_help),
        label = stringResource(R.string.help_center),
        onClick = { context.showDevelopmentToast() }
    )

    HorizontalDivider(color = dividerColor())

    ProfileOption(
        icon = loadVector(R.drawable.ic_logout),
        label = stringResource(R.string.sign_out),
        onClick = {
            onLogout()
        },
        isDestructive = true
    )
}

@Composable
private fun ProfileOption(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit = {},
    isDestructive: Boolean = false
) {
    val textColor = if (isDestructive) {
        errorColor()
    } else {
        onSurfaceColor()
    }

    val iconTint = if (isDestructive) {
        errorColor()
    } else {
        onSurfaceVariantColor()
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                indication = rememberRipple(
                    color = rippleColor()
                ),
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() }
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(24.dp),
            tint = iconTint
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            fontSize = 16.sp,
            color = textColor,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun loadVector(resourceId: Int): ImageVector {
    return ImageVector.vectorResource(id = resourceId)
}

@Preview
@Composable
fun ProfileScreenPreview() {
    ProfileScreen()
}