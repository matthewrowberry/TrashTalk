package com.usuhackathon.trashtalk.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.usuhackathon.trashtalk.R
import com.usuhackathon.trashtalk.data.*
import com.usuhackathon.trashtalk.ui.theme.TradeWinds
import com.usuhackathon.trashtalk.ui.theme.Ubuntu
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.usuhackathon.trashtalk.ui.theme.TrashTalkTheme
import kotlinx.coroutines.tasks.await
import kotlin.random.Random

@Composable
fun HomeScreen(
    onProfileClick: () -> Unit,
    onFabClick: () -> Unit = {},
    onUserClick: (String, String) -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val state = viewModel.state
    val context = LocalContext.current
    var showCompleteChoreDialog by remember { mutableStateOf<Chore?>(null) }

    if (state.userProfile != null && state.userProfile.leagueID.isEmpty()) {
        NoLeagueView(
            onCreateLeague = { name, desc -> viewModel.createLeague(name, desc) },
            onJoinLeague = { id -> viewModel.joinLeague(id) },
            onSearch = { query -> viewModel.searchLeagues(query) },
            searchResults = state.leagueSearchResults,
            isLoading = state.isLoading
        )
    } else {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                    onClick = onFabClick,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Task",
                        modifier = Modifier.size(36.dp)
                    )                }
            },
            floatingActionButtonPosition = FabPosition.Center,
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            if (state.isLoading && state.userProfile == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
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
                            profile = state.userProfile,
                            onProfileClick = onProfileClick,
                            onPointsClick = {
                                val uid = AuthService.currentUser?.uid
                                if (uid != null) onUserClick(uid, state.userProfile?.displayName ?: "Me")
                            },
                            onRefresh = { viewModel.loadData() }
                        )
                    }

                    HorizontalDivider(color = Color.White, thickness = 2.dp)

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text("Leaderboard", fontFamily = TradeWinds, fontSize = 24.sp, modifier = Modifier.padding(vertical = 8.dp))
                        }

                        itemsIndexed(state.leaderboard) { index, entry ->
                            val displayName = entry.display_name ?: entry.user_uid
                            RoommateRow(
                                name = displayName,
                                place = "${index + 1}${getOrdinal(index + 1)}",
                                points = entry.total_points,
                                tasks = entry.completed_count,
                                isMe = entry.user_uid == AuthService.currentUser?.uid,
                                onClick = { onUserClick(entry.user_uid, displayName) }
                            )
                        }

                        item {
                            Text("Available Chores", fontFamily = TradeWinds, fontSize = 24.sp, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
                        }

                        itemsIndexed(state.chores) { _, chore ->
                            ChoreRow(chore = chore, onComplete = { showCompleteChoreDialog = chore })
                        }

                        item { Spacer(modifier = Modifier.height(80.dp)) }
                    }
                }
            }
        }
    }

    if (showCompleteChoreDialog != null) {
        CompleteChoreDialog(
            chore = showCompleteChoreDialog!!,
            onDismiss = { showCompleteChoreDialog = null },
            onComplete = { comments, imageUri ->
                viewModel.completeChore(showCompleteChoreDialog!!, comments, imageUri, context)
                showCompleteChoreDialog = null
            }
        )
    }
}

private fun getOrdinal(i: Int): String {
    val suffixes = arrayOf("th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th")
    return if (i % 100 in 11..13) "th" else suffixes[i % 10]
}

@Composable
fun NoLeagueView(
    onCreateLeague: (String, String) -> Unit,
    onJoinLeague: (String) -> Unit,
    onSearch: (String) -> Unit,
    searchResults: List<League>,
    isLoading: Boolean
) {
    var name by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
            .imePadding()
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        Text("You are not in a league!", style = MaterialTheme.typography.headlineMedium, fontFamily = TradeWinds)
        Spacer(modifier = Modifier.height(24.dp))

        Text("Create a League", style = MaterialTheme.typography.titleLarge)
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("League Name") })
        OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Description") })
        Button(
            onClick = {
                keyboardController?.hide()
                onCreateLeague(name, desc)
            },
            modifier = Modifier.padding(top = 8.dp),
            enabled = !isLoading
        ) {
            if (isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
            else Text("Create")
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text("Or Join a League", style = MaterialTheme.typography.titleLarge)
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                onSearch(it)
            },
            label = { Text("Search League Name") }
        )

        if (searchResults.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 250.dp)
                    .padding(top = 8.dp)
                    .background(Color.Gray.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
            ) {
                LazyColumn {
                    itemsIndexed(searchResults) { _, league ->
                        ListItem(
                            headlineContent = { Text(league.name) },
                            supportingContent = { Text(league.description) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    keyboardController?.hide()
                                    onJoinLeague(league.id)
                                }
                        )
                    }
                }
            }
        } else if (searchQuery.length >= 2) {
            Text("No leagues found", modifier = Modifier.padding(top = 8.dp), style = MaterialTheme.typography.bodySmall)
        }

        if (isLoading) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
        }

        Spacer(modifier = Modifier.height(48.dp))
    }
}

@Composable
fun ChoreRow(chore: Chore, onComplete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(chore.name, fontWeight = FontWeight.Bold, fontFamily = Ubuntu)
                Text(chore.description, fontSize = 12.sp, color = Color.Gray, fontFamily = Ubuntu)
            }
            Text("+${chore.points}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp))
            IconButton(onClick = onComplete) {
                Icon(Icons.Default.Check, contentDescription = "Complete", tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun CompleteChoreDialog(chore: Chore, onDismiss: () -> Unit, onComplete: (String, Uri?) -> Unit) {
    var comments by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imageUri = uri
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Complete ${chore.name}") },
        text = {
            Column {
                OutlinedTextField(value = comments, onValueChange = { comments = it }, label = { Text("Comments") })
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { launcher.launch("image/*") }) {
                    Text(if (imageUri == null) "Add Proof Image" else "Image Selected")
                }
            }
        },
        confirmButton = {
            Button(onClick = { onComplete(comments, imageUri) }) { Text("Submit") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun TopProfileSection(
    profile: UserProfile?,
    onProfileClick: () -> Unit,
    onPointsClick: () -> Unit,
    onRefresh: () -> Unit
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
            Image(
                painter = painterResource(id = R.drawable.profile),
                contentDescription = "Profile Picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .clickable { onProfileClick() }
            )

            Spacer(modifier = Modifier.width(32.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = (profile?.displayName ?: "User").uppercase(),
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

            IconButton(onClick = onRefresh) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh",
                    tint = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(label = "POINTS", value = profile?.points?.toString() ?: "0", onClick = onPointsClick)
            StatItem(label = "LEAGUE", value = if(profile?.leagueID?.isEmpty() == true) "None" else "Active")
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun StatItem(label: String, value: String, onClick: () -> Unit = {}) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
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
fun RoommateRow(name: String, place: String, points: Int, tasks: Int, isMe: Boolean = false, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.profile),
                contentDescription = "Profile Picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )

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
        HomeScreen(onProfileClick = {}, onUserClick = { _, _ -> })
    }
}