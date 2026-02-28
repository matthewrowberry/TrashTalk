package com.usuhackathon.trashtalk.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.usuhackathon.trashtalk.data.AuthResult
import com.usuhackathon.trashtalk.data.AuthService
import com.usuhackathon.trashtalk.ui.theme.TrashTalkTheme
import com.usuhackathon.trashtalk.ui.theme.TradeWinds
import com.usuhackathon.trashtalk.ui.theme.Ubuntu
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginSuccessGoToHome: () -> Unit,
    onSignUpGoToSignUp: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

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
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(48.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it; errorMessage = null },
            label = { Text(text = "Email", fontFamily = Ubuntu) },
            textStyle = LocalTextStyle.current.copy(fontFamily = Ubuntu, color = Color.Black),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it; errorMessage = null },
            label = { Text(text = "Password", fontFamily = Ubuntu) },
            textStyle = LocalTextStyle.current.copy(fontFamily = Ubuntu, color = Color.Black),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible)
                    Icons.Filled.Visibility
                else
                    Icons.Filled.VisibilityOff

                val description = if (passwordVisible) "Hide password" else "Show password"

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = description)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                scope.launch {
                    when (val result = AuthService.signIn(email.trim(), password)) {
                        is AuthResult.Success -> onLoginSuccessGoToHome()
                        AuthResult.AccountDoesNotExist -> errorMessage = "Account does not exist"
                        AuthResult.InvalidPassword -> errorMessage = "Invalid password"
                        is AuthResult.Error -> errorMessage = result.message
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "LOG IN", fontFamily = Ubuntu)
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onSignUpGoToSignUp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "SIGN UP", fontFamily = Ubuntu)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    TrashTalkTheme {
        LoginScreen(
            onLoginSuccessGoToHome = {},
            onSignUpGoToSignUp = {}
        )
    }
}