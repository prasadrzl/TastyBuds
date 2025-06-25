import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.app.tastybuds.R
import com.app.tastybuds.ui.theme.Spacing
import com.app.tastybuds.ui.theme.bodyMedium
import com.app.tastybuds.ui.theme.buttonText
import com.app.tastybuds.ui.theme.onPrimaryColor
import com.app.tastybuds.ui.theme.primaryColor
import com.app.tastybuds.ui.theme.screenTitle
import com.app.tastybuds.ui.theme.textSecondaryColor

object AudioCallDimensions {
    val profileImageSize = 120.dp
    val profileIconSize = 48.dp
    val endCallButtonSize = 64.dp
    val callControlIconSize = 24.dp
    val callControlSpacing = 48.dp
}

@Composable
fun AudioCallScreen(
    driverName: String = stringResource(R.string.user_name),
    callDuration: String = "00:29",
    onSpeakerToggle: () -> Unit = {},
    onMuteToggle: () -> Unit = {},
    onEndCall: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = Spacing.xxxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = driverName,
            style = screenTitle()
        )

        Spacer(modifier = Modifier.height(Spacing.small))

        Text(
            text = callDuration,
            style = bodyMedium(),
            color = textSecondaryColor()
        )

        Spacer(modifier = Modifier.height(Spacing.xxl))

        Box(
            modifier = Modifier
                .size(AudioCallDimensions.profileImageSize)
                .clip(CircleShape)
                .background(primaryColor()),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_profile_person),
                contentDescription = stringResource(R.string.cd_profile_image),
                tint = onPrimaryColor(),
                modifier = Modifier.size(AudioCallDimensions.profileIconSize)
            )
        }

        Spacer(modifier = Modifier.height(Spacing.xxl))

        Row(
            horizontalArrangement = Arrangement.spacedBy(AudioCallDimensions.callControlSpacing)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = onSpeakerToggle) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_speaker_high),
                        contentDescription = stringResource(R.string.speaker)
                    )
                }
                Text(
                    text = stringResource(R.string.speaker),
                    style = buttonText()
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = onMuteToggle) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_microphone_off),
                        contentDescription = stringResource(R.string.mute)
                    )
                }
                Text(
                    text = stringResource(R.string.mute),
                    style = buttonText()
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.xxl))

        IconButton(
            onClick = onEndCall,
            modifier = Modifier
                .size(AudioCallDimensions.endCallButtonSize)
                .clip(CircleShape)
                .background(primaryColor())
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_end_call),
                contentDescription = stringResource(R.string.end_call),
                tint = onPrimaryColor(),
                modifier = Modifier.size(AudioCallDimensions.callControlIconSize)
            )
        }
    }
}