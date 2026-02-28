package com.usuhackathon.trashtalk.ui

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.usuhackathon.trashtalk.data.AuthService

private object Routes {
    const val Login = "login"
    const val SignUp = "signup"
    const val Home = "home"
    const val Settings = "settings"
    const val Submission = "submission"
}

@Composable
fun AppNav() {
    val nav = rememberNavController()
    val currentUser = AuthService.currentUser
    val startDestination = if (currentUser != null) Routes.Home else Routes.Login

    // Hoisted state for the captured image
    var capturedImage by remember { mutableStateOf<Bitmap?>(null) }

    // Camera launcher contract
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            capturedImage = bitmap
            nav.navigate(Routes.Submission)
        }
    }

    NavHost(navController = nav, startDestination = startDestination) {
        composable(Routes.Login) {
            LoginScreen(
                onLoginSuccessGoToHome = {
                    nav.navigate(Routes.Home) {
                        popUpTo(Routes.Login) { inclusive = true }
                    }
                },
                onSignUpGoToSignUp = {
                    nav.navigate(Routes.SignUp)
                }
            )
        }

        composable(Routes.SignUp) {
            SignUpScreen(
                onAccountCreatedGoToLogin = {
                    nav.popBackStack()
                }
            )
        }

        composable(Routes.Home) {
            HomeScreen(
                onProfileClick = {
                    nav.navigate(Routes.Settings)
                },
                onFabClick = {
                    cameraLauncher.launch(null)
                }
            )
        }

        composable(Routes.Submission) {
            SubmissionScreen(
                imageBitmap = capturedImage,
                onBack = {
                    capturedImage = null
                    nav.popBackStack()
                },
                onSubmit = {
                    // Logic for submission goes here later
                    capturedImage = null
                    nav.popBackStack()
                }
            )
        }

        composable(Routes.Settings) {
            SettingsScreen(
                onLogout = {
                    AuthService.signOut()
                    nav.navigate(Routes.Login) {
                        popUpTo(Routes.Home) { inclusive = true }
                    }
                },
                onBack = {
                    nav.popBackStack()
                }
            )
        }
    }
}