import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.tastybuds.R

@Composable
fun LookingForDriverScreen(onCancel: () -> Unit, onHelp: () -> Unit) {
    val brandOrange = Color(0xFFFF7700)
    val stageLabels =
        listOf("Confirm order", "Look for driver", "Prepare food", "Deliver", "Arrived")
    val currentStage = 1

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Image(
            painter = painterResource(id = R.drawable.ic_check_mark), // <-- your icon
            contentDescription = "Order confirmed",
            modifier = Modifier.size(32.dp)
        )


        Spacer(modifier = Modifier.height(8.dp))

        Text("Order confirmed", color = Color.Gray, fontSize = 16.sp)

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "Looking for driver",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(32.dp))

        Image(
            painter = painterResource(id = R.drawable.ic_search_driver),
            contentDescription = "Looking icon",
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(96.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        StageProgressBar(
            stageLabels = stageLabels,
            currentStage = currentStage,
            brandColor = brandOrange
        )

        Spacer(modifier = Modifier.height(40.dp))

        OutlinedButton(
            onClick = onHelp,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, brandOrange),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = brandOrange)
        ) {
            Text("Need help?", fontWeight = FontWeight.Medium)
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = onCancel) {
            Text("\u2715 Cancel", color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun StageProgressBar(
    stageLabels: List<String>,
    currentStage: Int,
    brandColor: Color
) {
    val dotSize = 16.dp
    val lineThickness = 3.dp
    val lineColorPending = Color.LightGray

    Column(modifier = Modifier.fillMaxWidth()) {
        // Top row: Dots with lines
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            stageLabels.forEachIndexed { index, _ ->
                Box(contentAlignment = Alignment.Center) {
                    // Draw circle
                    Canvas(modifier = Modifier.size(dotSize)) {
                        if (index <= currentStage) {
                            drawCircle(color = brandColor)
                        } else {
                            drawCircle(color = Color.LightGray, style = Stroke(width = 3f))
                        }
                    }
                }

                if (index < stageLabels.lastIndex) {
                    Canvas(
                        modifier = Modifier
                            .weight(1f)
                            .height(lineThickness)
                    ) {
                        drawLine(
                            color = if (index < currentStage) brandColor else lineColorPending,
                            start = Offset(0f, size.height / 2),
                            end = Offset(size.width, size.height / 2),
                            strokeWidth = size.height
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            stageLabels.forEachIndexed { index, label ->
                Text(
                    text = label,
                    fontSize = 11.sp,
                    color = if (index <= currentStage) Color.Black else Color.Gray,
                    fontWeight = if (index <= currentStage) FontWeight.Medium else FontWeight.Normal,
                    maxLines = 2,
                    softWrap = true,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(60.dp)
                )

            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewLookingForDriverScreen() {
    LookingForDriverScreen(onCancel = {}, onHelp = {})
}
