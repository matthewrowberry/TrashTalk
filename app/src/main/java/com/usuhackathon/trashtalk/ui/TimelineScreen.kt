package com.usuhackathon.trashtalk.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.usuhackathon.trashtalk.data.AuthService
import com.usuhackathon.trashtalk.data.UserCompletion
import com.usuhackathon.trashtalk.ui.theme.TradeWinds
import com.usuhackathon.trashtalk.ui.theme.Ubuntu
import java.security.MessageDigest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimelineScreen(
    userId: String,
    userName: String,
    onBack: () -> Unit,
    viewModel: TimelineViewModel = viewModel()
) {
    val state = viewModel.state
    val requesterUid = AuthService.currentUser?.uid ?: ""

    LaunchedEffect(userId) {
        viewModel.loadTimeline(userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(userName, fontFamily = TradeWinds) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(state.completions) { completion ->
                    TimelinePostItem(completion, requesterUid, state.leagueId)
                }
            }
        }
    }
}

@Composable
fun TimelinePostItem(completion: UserCompletion, requesterUid: String, leagueId: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.weight(1f).fillMaxHeight()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = completion.chore_name, fontWeight = FontWeight.Bold, fontFamily = Ubuntu)
                    if (!completion.comments.isNullOrEmpty()) {
                        Text(text = completion.comments, fontSize = 14.sp, fontFamily = Ubuntu)
                    }
                    Text(text = completion.completed_at, fontSize = 10.sp, color = Color.Gray)
                }
            }

            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFA8E6CF)),
                modifier = Modifier.width(64.dp).fillMaxHeight()
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "+${completion.points_earned}", fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
                }
            }
        }

        if (completion.has_proof && completion.proof_filename != null && leagueId.isNotEmpty()) {
            val token = sha256(leagueId + requesterUid + "simple_salt")
            val imageUrl = "https://mrowberry.com/trashtalk/view_proof_image.php?f=${completion.proof_filename}&u=${requesterUid}&t=$token"
            
            AsyncImage(
                model = imageUrl,
                contentDescription = "Proof",
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray),
                contentScale = ContentScale.Crop
            )
        }
    }
}

fun sha256(input: String): String {
    val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
}
