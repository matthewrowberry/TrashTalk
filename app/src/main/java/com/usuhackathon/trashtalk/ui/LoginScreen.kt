package com.usuhackathon.trashtalk.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.usuhackathon.trashtalk.login.LoginViewModel
import com.usuhackathon.trashtalk.storage.UserData
import com.usuhackathon.trashtalk.data.AuthService
import com.usuhackathon.trashtalk.data.FirestoreService
import com.usuhackathon.trashtalk.data.UserProfile
import com.usuhackathon.trashtalk.ui.theme.TrashTalkTheme
import com.usuhackathon.trashtalk.ui.theme.TradeWinds
import com.usuhackathon.trashtalk.ui.theme.Ubuntu
import kotlinx.coroutines.launch
import androidx.compose.material3.LocalTextStyle

@Composable
fun LoginScreen() {
    var displayName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Logo",
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "TRASH TALK",
            fontSize = 36.sp,
            fontFamily = TradeWinds,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(48.dp))

        OutlinedTextField(
            value = displayName,
            onValueChange = { displayName = it },
            label = { Text(text = "Username", fontFamily = Ubuntu) },
            textStyle = LocalTextStyle.current.copy(fontFamily = Ubuntu),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(text = "Email", fontFamily = Ubuntu) },
            textStyle = LocalTextStyle.current.copy(fontFamily = Ubuntu),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(text = "Password", fontFamily = Ubuntu) },
            textStyle = LocalTextStyle.current.copy(fontFamily = Ubuntu),
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // LOGIN button left as TODO (you asked to swap the Firestore behavior to SIGN UP)
            Button(
                onClick = { /* TODO: signInWithEmailAndPassword if you want */ },
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "LOGIN", fontFamily = Ubuntu)
            }

            OutlinedButton(
                onClick = {
                    scope.launch {
                        try {
                            // 1) Create auth user -> uid
                            val uid = AuthService.signUp(
                                email = email.trim(),
                                password = password
                            )

                            // 2) Create/overwrite Firestore doc at users/{uid}
                            val profileToSave = UserProfile(
                                displayName = displayName.trim(),
                                email = email.trim(),
                                leagueID = "",   // set later, or add UI for it
                                points = 0L
                            )
                            FirestoreService.upsertUserProfile(uid, profileToSave)

                            // 3) Read it back and show a toast (confirms Firestore connectivity + data)
                            val loaded = FirestoreService.getUserProfile(uid)

                            Toast.makeText(
                                context,
                                "Connected to Firestore. " +
                                        "${loaded.displayName} (${loaded.email}) " +
                                        "League: ${loaded.leagueID}, Points: ${loaded.points}",
                                Toast.LENGTH_LONG
                            ).show()
                        } catch (e: Exception) {
                            Toast.makeText(
                                context,
                                "Sign up / Firestore failed: ${e.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "SIGN UP", fontFamily = Ubuntu)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    TrashTalkTheme {
        LoginScreen()
    }
}