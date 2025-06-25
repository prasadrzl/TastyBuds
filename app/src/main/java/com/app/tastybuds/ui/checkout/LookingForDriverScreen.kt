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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.app.tastybuds.R
import com.app.tastybuds.ui.theme.primaryColor
import com.app.tastybuds.ui.theme.Spacing
import com.app.tastybuds.ui.theme.ComponentSizes
import com.app.tastybuds.ui.theme.bodyMedium
import com.app.tastybuds.ui.theme.screenTitle
import com.app.tastybuds.ui.theme.buttonText
import com.app.tastybuds.ui.theme.statusLabel
import com.app.tastybuds.ui.theme.textSecondaryColor
import com.app.tastybuds.ui.theme.surfaceColor

@Composable
fun LookingForDriverScreen(onCancel: () -> Unit, onHelp: () -> Unit) {
    val brandOrange = primaryColor()
    val stageLabels = listOf(
        stringResource(R.string.confirm_order),
        stringResource(R.string.look_for_driver),
        stringResource(R.string.prepare_food),
        stringResource(R.string.deliver),
        stringResource(R.string.arrived)
    )
    val currentStage = 1

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(surfaceColor())
            .padding(horizontal = Spacing.large),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(Spacing.xl))

        Image(
            painter = painterResource(id = R.drawable.ic_check_mark),
            contentDescription = stringResource(R.string.order_confirmed),
            modifier = Modifier.size(ComponentSizes.iconMedium)
        )

        Spacer(modifier = Modifier.height(Spacing.small))

        Text(
            text = stringResource(R.string.order_confirmed),
            style = bodyMedium(),
            color = textSecondaryColor()
        )

        Spacer(modifier = Modifier.height(Spacing.small))

        Text(
            text = stringResource(R.string.looking_for_driver),
            style = screenTitle(),
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(Spacing.xl))

        Image(
            painter = painterResource(id = R.drawable.ic_search_driver),
            contentDescription = stringResource(R.string.looking_icon_desc),
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(ComponentSizes.iconLarge)
        )

        Spacer(modifier = Modifier.height(Spacing.xxl))

        StageProgressBar(
            stageLabels = stageLabels,
            currentStage = currentStage,
            brandColor = brandOrange
        )

        Spacer(modifier = Modifier.height(Spacing.xxl))

        OutlinedButton(
            onClick = onHelp,
            modifier = Modifier
                .fillMaxWidth()
                .height(ComponentSizes.buttonHeight),
            shape = RoundedCornerShape(ComponentSizes.cornerRadius),
            border = BorderStroke(ComponentSizes.strokeWidth, brandOrange),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = brandOrange)
        ) {
            Text(
                text = stringResource(R.string.need_help),
                style = buttonText(),
                color = primaryColor()
            )
        }

        Spacer(modifier = Modifier.height(Spacing.small))

        TextButton(onClick = onCancel) {
            Text(
                text = stringResource(R.string.cancel_order),
                style = buttonText(),
                color = textSecondaryColor()
            )
        }

        Spacer(modifier = Modifier.height(Spacing.large))
    }
}

@Composable
fun StageProgressBar(
    stageLabels: List<String>,
    currentStage: Int,
    brandColor: Color
) {
    val dotSize = ComponentSizes.progressDotSize
    val lineThickness = ComponentSizes.progressLineThickness
    val lineColorPending = Color.LightGray

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.large),
            verticalAlignment = Alignment.CenterVertically
        ) {
            stageLabels.forEachIndexed { index, _ ->
                Box(contentAlignment = Alignment.Center) {
                    Canvas(modifier = Modifier.size(dotSize)) {
                        if (index <= currentStage) {
                            drawCircle(color = brandColor)
                        } else {
                            drawCircle(
                                color = Color.LightGray,
                                style = Stroke(width = 3f)
                            )
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

        Spacer(modifier = Modifier.height(Spacing.small))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.large),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            stageLabels.forEachIndexed { index, label ->
                Text(
                    text = label,
                    style = statusLabel(),
                    color = if (index <= currentStage) Color.Black else textSecondaryColor(),
                    maxLines = 2,
                    softWrap = true,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(ComponentSizes.labelWidth)
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