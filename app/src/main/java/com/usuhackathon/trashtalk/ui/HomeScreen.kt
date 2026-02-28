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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.usuhackathon.trashtalk.ui.theme.TrashTalkTheme
import com.usuhackathon.trashtalk.ui.theme.TradeWinds
import com.usuhackathon.trashtalk.ui.theme.Ubuntu
import kotlinx.coroutines.tasks.await
import kotlin.random.Random

/**
 * NOTE: This file fixes the HomeScreen() issues:
 * - HomeScreenPreview now compiles (onProfileClick has a default)
 * - TopProfileSection receives the data it needs (instead of hardcoded)
 * - Firestore load runs safely and shows loading/error states
 * - List of other users comes from Firestore instead of hardcoded rows
 *
 * Firestore expectations (adjust field names if needed):
 * collection: "users"
 * fields: displayName (String), points (Long), tasksCompleted (Long, optional)
 */

data class AppUser(
    val id: String,
    val displayName: String,
    val points: Int,
    val tasksCompleted: Int = 0,
    val photoUrl: String? = null, // reserved for later
)

private fun QuerySnapshot.toUsers(): List<AppUser> =
    documents.mapNotNull { doc ->
        val displayName = doc.getString("displayName") ?: return@mapNotNull null
        val points = (doc.getLong("points") ?: 0L).toInt()
        val tasksCompleted = (doc.getLong("tasksCompleted") ?: 0L).toInt()
        val photoUrl = doc.getString("photoUrl")

        AppUser(
            id = doc.id,
            displayName = displayName,
            points = points,
            tasksCompleted = tasksCompleted,
            photoUrl = photoUrl
        )
    }

@Composable
fun HomeScreen(
    onProfileClick: () -> Unit = {},
    firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    auth: FirebaseAuth = FirebaseAuth.getInstance(),
) {
    val currentUid = auth.currentUser?.uid

    // Load users once. (If you want realtime updates later, use addSnapshotListener instead.)
    val usersResult by produceState<Result<List<AppUser>>?>(initialValue = null, key1 = currentUid) {
        value = runCatching {
            firestore.collection("users").get().await().toUsers()
        }
    }

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

        when (val r = usersResult) {
            null -> {
                // Loading
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }

            else -> {
                val users = r.getOrNull().orEmpty()
                val currentUser = users.firstOrNull { it.id == currentUid }
                val otherUsers = users.filterNot { it.id == currentUid }

                // Generate random places for the other users.
                val randomPlaces = remember(otherUsers) {
                    val labels = listOf(
                        "1st", "2nd", "3rd", "4th", "5th",
                        "6th", "7th", "8th", "9th", "10th", "11th", "12th"
                    )
                    otherUsers.associate { it.id to labels.random(Random(System.currentTimeMillis())) }
                }

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
                        TopProfileSection(
                            onProfileClick = onProfileClick,
                            displayName = currentUser?.displayName ?: "Unknown User",
                            points = currentUser?.points ?: 0,
                            tasks = currentUser?.tasksCompleted ?: 0,
                            place = "—" // can compute actual rank later
                        )
                    }

                    // Divider Line
                    HorizontalDivider(
                        color = Color.White,
                        thickness = 2.dp
                    )

                    // Bottom Section
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item { Spacer(modifier = Modifier.height(8.dp)) }

                        if (r.isFailure) {
                            item {
                                Text(
                                    text = "Failed to load users: ${r.exceptionOrNull()?.message ?: "Unknown error"}",
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }

                        items(otherUsers.size) { index ->
                            val u = otherUsers[index]
                            RoommateRow(
                                name = u.displayName,
                                place = randomPlaces[u.id] ?: "—",
                                points = u.points,
                                tasks = u.tasksCompleted
                            )
                        }

                        item { Spacer(modifier = Modifier.height(72.dp)) } // Spacing for FAB
                    }
                }
            }
        }
    }
}

@Composable
fun TopProfileSection(
    onProfileClick: () -> Unit,
    displayName: String,
    points: Int,
    tasks: Int,
    place: String,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Placeholder profile pic (clickable for future)
            Box(
                modifier = Modifier
                    .size(72.dp)
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

            Spacer(modifier = Modifier.width(32.dp))

            Column {
                Text(
                    text = displayName.uppercase(),
                    fontFamily = TradeWinds,
                    fontSize = 38.sp,
                    lineHeight = 46.sp, // adds space between lines if the name wraps
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
            StatItem(label = "PLACE", value = place)
            StatItem(label = "POINTS", value = points.toString())
            StatItem(label = "TASKS", value = tasks.toString())
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontFamily = TradeWinds,
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
        HomeScreen(onProfileClick = {})
    }
}