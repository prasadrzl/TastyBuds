package com.app.tastybuds.ui.orders

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.tastybuds.R
import com.app.tastybuds.ui.theme.PrimaryColor
import com.app.tastybuds.util.ui.AppTopBar

@Preview
@Composable
fun RatingScreen() {
    var rating by remember { mutableIntStateOf(4) }
    var selectedTags by remember { mutableStateOf(setOf<String>()) }
    var feedback by remember { mutableStateOf("") }

    val tags = listOf("Service", "Supportive", "Friendly", "Delivery", "Contactless")

    Column {
        AppTopBar()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(Color.White)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Profile circle with icon
            Spacer(modifier = Modifier.height(64.dp))
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(PrimaryColor),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_profile_person),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    colorFilter = ColorFilter.tint(Color.White)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Rate DurgaPrasad", fontWeight = FontWeight.Bold, fontSize = 20.sp)

            Spacer(modifier = Modifier.height(8.dp))

            val starIcon: ImageVector =
                ImageVector.vectorResource(id = R.drawable.material_starpurple500_sharp)

            Row {
                repeat(5) {
                    val icon = if (it < rating) Icons.Default.Star else starIcon
                    val tint = if (it < rating) Color(0xFFFFC107) else Color(0xFF686583)
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = tint,
                        modifier = Modifier
                            .size(28.dp)
                            .clickable { rating = it + 1 }
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text("Leave your feedback here", color = Color.Gray)

            Spacer(modifier = Modifier.height(20.dp))

            FeedbackChipGroup()

            Spacer(modifier = Modifier.height(56.dp))

            Text("Care to share more?", fontWeight = FontWeight.SemiBold)

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = feedback,
                onValueChange = { feedback = it },
                placeholder = { Text("Leave feedback about driver...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(Color(0xFFF3F4F6)),
                shape = RoundedCornerShape(4.dp)
            )

            Spacer(modifier = Modifier.height(80.dp))

            Button(
                onClick = { /* Submit logic */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
            ) {
                Text("Submit", color = Color.White)
            }
        }
    }


}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FeedbackChipGroup() {
    var selectedTags by remember { mutableStateOf(setOf<String>()) }

    val tags = listOf("Service", "Supportive", "Friendly", "Delivery", "Contactless")

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(top = 16.dp)
    ) {
        tags.forEach { tag ->
            val isSelected = tag in selectedTags

            FilterChip(
                selected = isSelected,
                onClick = {
                    selectedTags = if (isSelected) selectedTags - tag else selectedTags + tag
                },
                label = {
                    Text(
                        text = tag,
                        color = if (isSelected && tag != "Supportive" && tag != "Contactless")
                            PrimaryColor else Color.DarkGray,
                        fontSize = 14.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = if (isSelected && tag != "Supportive" && tag != "Contactless")
                            PrimaryColor else Color.DarkGray,
                        modifier = Modifier.size(16.dp)
                    )
                },
                shape = RoundedCornerShape(50),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = if (tag == "Supportive" || tag == "Contactless")
                        Color(0xFFF2F2F2) else Color(0xFFFFF3E0),
                    containerColor = Color(0xFFF2F2F2)
                )
            )
        }
    }
}


