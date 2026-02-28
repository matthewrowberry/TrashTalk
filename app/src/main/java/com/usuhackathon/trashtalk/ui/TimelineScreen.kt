package com.usuhackathon.trashtalk.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.usuhackathon.trashtalk.ui.theme.TrashTalkTheme

@Composable
fun TimelineScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "TIMELINE",
            modifier = Modifier.padding(top = 24.dp, bottom = 16.dp),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF333333)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                TimelineCard(
                    userAction = "Sammy took out the trash",
                    description = "Earned 10 points! The kitchen doesn't smell like a swamp anymore."
                )
            }
            item {
                TimelineCard(
                    userAction = "Tommy did the dishes",
                    description = "Earned 15 points! Clean plates for days."
                )
            }
        }
    }
}

@Composable
fun TimelineCard(userAction: String, description: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = userAction,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            // Placeholder for Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color(0xFFE0E0E0)),
                contentAlignment = Alignment.Center
            ) {
                Text("Image Placeholder", color = Color.Gray)
            }

            Text(
                text = description,
                modifier = Modifier.padding(top = 12.dp),
                fontSize = 14.sp
            )

            OutlinedButton(
                onClick = { /* TODO */ },
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F)),
                border = BorderStroke(1.dp, Color(0xFFD32F2F))
            ) {
                Text("DOUBT / REMOVE")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TimelineScreenPreview() {
    TrashTalkTheme {
        TimelineScreen()
    }
}
