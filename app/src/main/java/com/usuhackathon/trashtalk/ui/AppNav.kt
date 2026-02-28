package com.usuhackathon.trashtalk.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.usuhackathon.trashtalk.data.AuthService

private object Routes {
    const val Login = "login"
    const val SignUp = "signup"
    const val Home = "home"
    const val Settings = "settings"
}

@Composable
fun AppNav() {
    val nav = rememberNavController()
    val currentUser = AuthService.currentUser
    val startDestination = if (currentUser != null) Routes.Home else Routes.Login

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
                    nav.popBackStack() // back to login
                }
            )
        }

        composable(Routes.Home) {
            HomeScreen(
                onProfileClick = {
                    nav.navigate(Routes.Settings)
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