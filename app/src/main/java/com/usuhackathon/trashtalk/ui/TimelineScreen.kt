package com.usuhackathon.trashtalk.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
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

    LaunchedEffect(userId) {
        viewModel.loadTimeline(userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = userName.take(1).uppercase(),
                                fontSize = 16.sp,
                                fontFamily = Ubuntu,
                                color = Color.DarkGray,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(userName, fontFamily = TradeWinds)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (state.completions.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No history yet!", fontFamily = Ubuntu, color = Color.Gray)
                        }
                    }
                }
                items(state.completions) { completion ->
                    TimelinePostItem(completion, state.requesterUid, state.leagueId)
                }
            }
        }
    }
}

@Composable
fun TimelinePostItem(completion: UserCompletion, requesterUid: String, leagueId: String) {
    val context = LocalContext.current

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

        if (completion.has_proof && completion.proof_filename != null && leagueId.isNotEmpty() && requesterUid.isNotEmpty()) {
            val token = sha256(leagueId + requesterUid + "simple_salt")
            val imageUrl = "https://mrowberry.com/trashtalk/view_proof_image.php?f=${completion.proof_filename}&u=${requesterUid}&t=$token"
            
            // Log the construction details
            LaunchedEffect(imageUrl) {
                Log.d("TimelineScreen", "Constructing image URL for completion ${completion.completion_id}")
                Log.d("TimelineScreen", " - Filename: ${completion.proof_filename}")
                Log.d("TimelineScreen", " - Requester: $requesterUid")
                Log.d("TimelineScreen", " - Token: $token")
                Log.d("TimelineScreen", " - Full URL: $imageUrl")
            }

            val imageRequest = remember(imageUrl) {
                ImageRequest.Builder(context)
                    .data(imageUrl)
                    .listener(
                        onStart = { Log.d("TimelineScreen", "Coil: Starting request for $imageUrl") },
                        onSuccess = { _, _ -> Log.d("TimelineScreen", "Coil: Success for $imageUrl") },
                        onError = { _, result -> 
                            Log.e("TimelineScreen", "Coil: Error for $imageUrl", result.throwable)
                        }
                    )
                    .build()
            }

            AsyncImage(
                model = imageRequest,
                contentDescription = "Proof",
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray),
                contentScale = ContentScale.Crop
            )
        } else if (completion.has_proof) {
            // Log why the image isn't even attempting to load
            LaunchedEffect(completion.completion_id, leagueId, requesterUid) {
                Log.w("TimelineScreen", "Proof image exists but request NOT built: filename=${completion.proof_filename}, leagueId=$leagueId, requesterUid=$requesterUid")
            }
        }
    }
}

fun sha256(input: String): String {
    val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
}
