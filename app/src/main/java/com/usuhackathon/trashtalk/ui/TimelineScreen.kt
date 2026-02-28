package com.usuhackathon.trashtalk.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.usuhackathon.trashtalk.ui.theme.TradeWinds
import com.usuhackathon.trashtalk.ui.theme.TrashTalkTheme
import com.usuhackathon.trashtalk.ui.theme.Ubuntu

// Dummy data class representing a single timeline entry
data class TimelineEntry(
    val id: Int,
    val description: String,
    val points: Int // Added points value
)

@Composable
fun TimelineScreen(
    userName: String = "JOHN DOE",
    //eventually add a onProfileClick here
) {
    // Dummy list state management for UI-only deletion
    val dummyPosts = remember {
        mutableStateListOf(
            TimelineEntry(1, "did the dishes and wiped down the counters.", 10),
            TimelineEntry(2, "I took out the trash and recycling. Cleaned the bin.", 15),
            TimelineEntry(3, "vacuumed the common area and the hallway for clean checks", 20)
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background // Parchment
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 1. Fixed Top Banner - Patterned after HomeScreen header
            Surface(
                color = MaterialTheme.colorScheme.primary, // Dark Green
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back Arrow Button
                    IconButton(
                        onClick = { /* TODO: Add back logic later */ },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    // Profile Picture Placeholder
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray)
                            .clickable {}, // Added clickable modifier just in case
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Pic",
                            fontFamily = Ubuntu,
                            color = Color.DarkGray
                        )
                    }

                    // Increased padding left of the name
                    Spacer(modifier = Modifier.width(32.dp))

                    Text(
                        text = userName,
                        fontFamily = TradeWinds,
                        fontSize = 32.sp,
                        color = Color.White
                    )
                }
            }

            // Divider
            HorizontalDivider(color = Color.White, thickness = 2.dp)

            // 2. Scrolling Timeline List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                items(dummyPosts, key = { it.id }) { post ->
                    TimelinePostItem(
                        entry = post,
                        onDeleteClicked = { dummyPosts.remove(post) }
                    )
                }
            }
        }
    }
}

@Composable
fun TimelinePostItem(
    entry: TimelineEntry,
    onDeleteClicked: () -> Unit
) {
    // State to track if the delete icon should overlay the image
    var isDeleteOverlayVisible by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {

        // A. Left-aligned Textbox & Points Box Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min), // Makes both cards stretch to the same height
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Description Box
            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .weight(1f) // Takes up remaining horizontal space
                    .fillMaxHeight()
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = entry.description,
                        fontFamily = Ubuntu,
                        fontSize = 16.sp,
                        color = Color.Black,
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth()
                    )
                }
            }

            // Points Box (Light Green)
            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFA8E6CF)), // Light green
                modifier = Modifier
                    .width(64.dp)
                    .fillMaxHeight()
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "+${entry.points}",
                        fontFamily = Ubuntu,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20) // Darker green text for contrast
                    )
                }
            }
        }

        // B. Attached Image Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(top = 8.dp) // Slight separation from text box row
                .clickable {
                    // Clicking image toggles visibility of the delete icon
                    isDeleteOverlayVisible = !isDeleteOverlayVisible
                },
            contentAlignment = Alignment.TopEnd // Position for delete icon
        ) {
            // The Task Image Placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp) // Fixed height for UI consistency
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Build,
                    contentDescription = "Placeholder Image",
                    modifier = Modifier.size(64.dp),
                    tint = Color.DarkGray
                )
            }

            // C. Conditionally displayed Delete Button
            if (isDeleteOverlayVisible) {
                Surface(
                    onClick = onDeleteClicked,
                    shape = CircleShape,
                    color = Color.Red,
                    modifier = Modifier
                        .padding(8.dp) // Padding from edge of image
                        .size(36.dp),
                    border = BorderStroke(2.dp, Color.White),
                    tonalElevation = 4.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Permanently Remove",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
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