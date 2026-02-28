package com.usuhackathon.trashtalk.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.usuhackathon.trashtalk.data.AuthService

object Routes {
    const val Login = "login"
    const val SignUp = "signup"
    const val Home = "home"
    const val Settings = "settings"
    const val Timeline = "timeline/{userId}/{userName}"
    
    fun timeline(userId: String, userName: String) = "timeline/$userId/$userName"
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
                },
                onUserClick = { userId, userName ->
                    nav.navigate(Routes.timeline(userId, userName))
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

        composable(
            Routes.Timeline,
            arguments = listOf(
                navArgument("userId") { type = NavType.StringType },
                navArgument("userName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            val userName = backStackEntry.arguments?.getString("userName") ?: ""
            TimelineScreen(
                userId = userId,
                userName = userName,
                onBack = { nav.popBackStack() }
            )
        }
    }
}
