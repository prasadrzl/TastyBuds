import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.tastybuds.R
import com.app.tastybuds.ui.theme.primaryColor

@Composable
fun AudioCallScreen(
    driverName: String = "Driver",
    callDuration: String = "00:29",
    onSpeakerToggle: () -> Unit = {},
    onMuteToggle: () -> Unit = {},
    onEndCall: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(text = driverName, fontWeight = FontWeight.Bold, fontSize = 24.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = callDuration, color = Color.Gray)

        Spacer(modifier = Modifier.height(40.dp))
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(primaryColor()),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_profile_person),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(48.dp)) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = onSpeakerToggle) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_speaker_high),
                        contentDescription = stringResource(R.string.speaker)
                    )
                }
                Text("Speaker")
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = onMuteToggle) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_microphone_off),
                        contentDescription = stringResource(R.string.mute)
                    )
                }
                Text("Mute")
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        IconButton(
            onClick = onEndCall,
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(primaryColor())
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_end_call),
                contentDescription = stringResource(R.string.end_call),
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
