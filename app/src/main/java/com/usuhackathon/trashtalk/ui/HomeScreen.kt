package com.usuhackathon.trashtalk.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.usuhackathon.trashtalk.ui.theme.TrashTalkTheme
import com.usuhackathon.trashtalk.ui.theme.Ubuntu
import com.usuhackathon.trashtalk.ui.theme.TradeWinds

@Composable
fun HomeScreen(onProfileClick: () -> Unit = {}) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO */ },
                containerColor = MaterialTheme.colorScheme.background, // Parchment for visibility
                contentColor = MaterialTheme.colorScheme.primary, // Dark Green icon
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        containerColor = MaterialTheme.colorScheme.primary // Changed to dark green
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                TopProfileSection(onProfileClick = onProfileClick)
            }

            item {
                Divider(
                    color = MaterialTheme.colorScheme.background, // Changed to parchment
                    thickness = 3.dp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            item {
                RoommateSection(name = "Sammy", place = "1st", points = 320, tasks = 12)
            }

            item {
                RoommateSection(name = "Tommy", place = "2nd", points = 150, tasks = 5)
            }

            item {
                RoommateSection(name = "You", place = "3rd", points = 120, tasks = 4)
            }

            item {
                RoommateSection(name = "Donald", place = "4th", points = 35, tasks = 2)
            }

            item {
                RoommateSection(name = "Jeffrey Epstein", place = "5th", points = 5, tasks = 1)
            }

            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }
    }
}

@Composable
fun TopProfileSection(onProfileClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface) // White
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                        .clickable { onProfileClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Pic",
                        fontFamily = Ubuntu,
                        color = Color.DarkGray
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "JOHN DOE",
                    fontFamily = TradeWinds,
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Your Stats",
                    fontFamily = Ubuntu,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Text(
                        text = "Season ends in 3 days",
                        fontFamily = Ubuntu,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatBubble(text = "Place\n3/8")
                    StatBubble(text = "Points\n120")
                    StatBubble(text = "Tasks\n4")
                }
            }
        }
    }
}

@Composable
fun RoommateSection(name: String, place: String, points: Int, tasks: Int) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = name,
            fontFamily = Ubuntu,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.background // Changed to parchment to contrast with dark green background
        )

        Spacer(modifier = Modifier.height(4.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background) // Changed to parchment
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = place,
                    fontFamily = Ubuntu,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "$points pts",
                    fontFamily = Ubuntu,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "$tasks tasks",
                    fontFamily = Ubuntu,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun StatBubble(text: String) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.background, // Changed to parchment
        modifier = Modifier.size(64.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                fontFamily = Ubuntu,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
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