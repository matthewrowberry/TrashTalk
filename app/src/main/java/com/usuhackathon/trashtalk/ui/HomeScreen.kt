package com.usuhackathon.trashtalk.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import com.usuhackathon.trashtalk.ui.theme.Ubuntu
import com.usuhackathon.trashtalk.ui.theme.TradeWinds

@Composable
fun HomeScreen() {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO */ },
                containerColor = MaterialTheme.colorScheme.primary, // Dark Green
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        containerColor = MaterialTheme.colorScheme.background // Parchment
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Top Section - Dark Green Background
            Surface(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth()
            ) {
                TopProfileSection()
            }

            // Divider Line
            HorizontalDivider(
                color = Color.White,
                thickness = 2.dp
            )

            // Bottom Section - Parchment Background with White Rows
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { Spacer(modifier = Modifier.height(8.dp)) }

                item { RoommateRow(name = "Sammy", place = "1st", points = 320, tasks = 12) }
                item { RoommateRow(name = "Tommy", place = "2nd", points = 150, tasks = 5) }
                item { RoommateRow(name = "You", place = "3rd", points = 120, tasks = 4) }
                item { RoommateRow(name = "Donald", place = "4th", points = 35, tasks = 2) }
                item { RoommateRow(name = "Jeffrey Epstein", place = "5th", points = 5, tasks = 1) }

                item { Spacer(modifier = Modifier.height(72.dp)) } // Spacing for FAB
            }
        }
    }
}

@Composable
fun TopProfileSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Pic",
                    fontFamily = Ubuntu,
                    color = Color.DarkGray
                )
            }

            // Increased padding between picture and text
            Spacer(modifier = Modifier.width(32.dp))

            Column {
                Text(
                    text = "JOHN DOE",
                    fontFamily = TradeWinds,
                    fontSize = 38.sp,
                    color = Color.White
                )
                Text(
                    text = "Season ends in 3 days",
                    fontFamily = Ubuntu,
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(label = "PLACE", value = "3rd")
            StatItem(label = "POINTS", value = "120")
            StatItem(label = "TASKS", value = "4")
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontFamily = TradeWinds, // Updated to TradeWinds
            fontSize = 28.sp,
            color = Color.White
        )
        Text(
            text = label,
            fontFamily = Ubuntu,
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun RoommateRow(name: String, place: String, points: Int, tasks: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Small Profile Picture Placeholder
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Pic",
                    fontFamily = Ubuntu,
                    fontSize = 12.sp,
                    color = Color.DarkGray
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Name and Place
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    fontFamily = Ubuntu,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray
                )
                Text(
                    text = place,
                    fontFamily = Ubuntu,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            // Stats
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$points pts",
                    fontFamily = Ubuntu,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "$tasks tasks",
                    fontFamily = Ubuntu,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    TrashTalkTheme {
        HomeScreen()
    }
}