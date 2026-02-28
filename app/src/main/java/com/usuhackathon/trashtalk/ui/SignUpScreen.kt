package com.usuhackathon.trashtalk.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.usuhackathon.trashtalk.data.AuthService
import com.usuhackathon.trashtalk.data.FirestoreService
import com.usuhackathon.trashtalk.data.UserProfile
import com.usuhackathon.trashtalk.ui.theme.Ubuntu
import kotlinx.coroutines.launch
import androidx.compose.material3.LocalTextStyle

@Composable
fun SignUpScreen(
    onAccountCreatedGoToLogin: () -> Unit
) {
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
        Text(text = "Create Account", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = displayName,
            onValueChange = { displayName = it },
            label = { Text("Username", fontFamily = Ubuntu) },
            textStyle = LocalTextStyle.current.copy(fontFamily = Ubuntu),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email", fontFamily = Ubuntu) },
            textStyle = LocalTextStyle.current.copy(fontFamily = Ubuntu),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password", fontFamily = Ubuntu) },
            textStyle = LocalTextStyle.current.copy(fontFamily = Ubuntu),
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                scope.launch {
                    try {
                        val uid = AuthService.signUp(email.trim(), password, displayName.trim())

                        val profile = UserProfile(
                            displayName = displayName.trim(),
                            email = email.trim(),
                            leagueID = "",
                            points = 0L
                        )
                        FirestoreService.upsertUserProfile(uid, profile)

                        Toast.makeText(context, "Account created. Please log in.", Toast.LENGTH_LONG).show()
                        onAccountCreatedGoToLogin()
                    } catch (e: Exception) {
                        Toast.makeText(context, e.message ?: "Sign up failed", Toast.LENGTH_LONG).show()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("CREATE ACCOUNT", fontFamily = Ubuntu)
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onAccountCreatedGoToLogin,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("BACK TO LOGIN", fontFamily = Ubuntu)
        }
    }
}
