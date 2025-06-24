package com.app.tastybuds.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.tastybuds.R
import com.app.tastybuds.ui.login.LoginViewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder

@Composable
fun ProfileSettingsScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    loginViewModel: LoginViewModel = hiltViewModel(),
    onDismiss: () -> Unit = {},
    onSaveChanges: () -> Unit = {}
) {
    var isPasswordVisible by remember { mutableStateOf(false) }
    val userIdFlow by loginViewModel.getUserId().collectAsState(initial = "user_001")
    val uiState by viewModel.uiState.collectAsState()
    val formState by viewModel.formState.collectAsState()

    LaunchedEffect(userIdFlow) {
        userIdFlow?.let { userId ->
            if (userId.isNotEmpty()) {
                viewModel.initialize(userId)
            }
        }
    }

    LaunchedEffect(uiState.updateSuccess) {
        if (uiState.updateSuccess) {
            onSaveChanges()
            viewModel.handleEvent(ProfileEvent.DismissSuccess)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.profile_settings),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.close),
                        tint = Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                uiState.error != null -> {
                    Column {
                        Text(
                            text = stringResource(
                                R.string.error_place_holder,
                                uiState.error ?: "error"
                            ),
                            color = Color.Red,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                viewModel.handleEvent(ProfileEvent.DismissError)
                                viewModel.handleEvent(ProfileEvent.LoadProfile)
                            }
                        ) {
                            Text(stringResource(R.string.retry))
                        }
                    }
                }

                else -> {
                    ProfileSettingsContent(
                        uiState = uiState,
                        formState = formState,
                        isPasswordVisible = isPasswordVisible,
                        onPasswordVisibilityToggle = { isPasswordVisible = !isPasswordVisible },
                        onNameChange = { viewModel.handleEvent(ProfileEvent.UpdateName(it)) },
                        onEmailChange = { viewModel.handleEvent(ProfileEvent.UpdateEmail(it)) },
                        onProfileImageChange = {
                            viewModel.handleEvent(
                                ProfileEvent.UpdateProfileImage(
                                    it
                                )
                            )
                        },
                        onSaveClick = { viewModel.handleEvent(ProfileEvent.SaveProfile) },
                        onCancelClick = {
                            viewModel.handleEvent(ProfileEvent.CancelEditing)
                            onDismiss()
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun ProfileSettingsContent(
    uiState: ProfileUiState,
    formState: EditProfileFormState,
    isPasswordVisible: Boolean,
    onPasswordVisibilityToggle: () -> Unit,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onProfileImageChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            val imageUrl = formState.profileUrl.ifBlank {
                uiState.user?.profileUrl ?: ""
            }

            GlideImage(
                model = imageUrl.ifBlank { "https://images.unsplash.com/photo-1494790108755-2616b612b1e3?w=150&h=150&fit=crop&crop=face" },
                contentDescription = formState.name,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                failure = placeholder(R.drawable.default_food),
                loading = placeholder(R.drawable.default_food)
            )

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-4).dp, y = 4.dp)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFF7700)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.edit_profile_image),
                    modifier = Modifier.size(16.dp),
                    tint = Color.White
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(32.dp))

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Name",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = formState.name,
            onValueChange = onNameChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(stringResource(R.string.enter_your_name)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFFF7700),
                unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
            ),
            singleLine = true,
            isError = uiState.validationErrors.nameError != null
        )

        uiState.validationErrors.nameError?.let { error ->
            Text(
                text = error,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.email),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = formState.email,
            onValueChange = onEmailChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(stringResource(R.string.enter_your_email)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFFF7700),
                unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
            ),
            singleLine = true,
            isError = uiState.validationErrors.emailError != null
        )

        uiState.validationErrors.emailError?.let { error ->
            Text(
                text = error,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.password),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = "************",
            onValueChange = { },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFFF7700),
                unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
            ),
            visualTransformation = if (isPasswordVisible) VisualTransformation.None
            else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = onPasswordVisibilityToggle) {
                    Icon(
                        painter = painterResource(
                            id = if (isPasswordVisible) R.drawable.ic_hide else R.drawable.ic_show
                        ),
                        contentDescription = if (isPasswordVisible) stringResource(R.string.hide_password)
                        else stringResource(R.string.show_password)
                    )
                }
            },
            readOnly = true,
            singleLine = true
        )

        Spacer(modifier = Modifier.height(48.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onCancelClick,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Gray.copy(alpha = 0.2f),
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.cancel),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            Button(
                onClick = onSaveClick,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF7700)
                ),
                shape = RoundedCornerShape(12.dp),
                enabled = !uiState.isUpdating && formState.isValid()
            ) {
                if (uiState.isUpdating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = stringResource(R.string.save_changes),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}