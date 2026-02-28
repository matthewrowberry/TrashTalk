package com.usuhackathon.trashtalk.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

private object Routes {
    const val Login = "login"
    const val SignUp = "signup"
    const val Home = "home"
}

@Composable
fun AppNav() {
    val nav = rememberNavController()

    NavHost(navController = nav, startDestination = Routes.Login) {
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
            HomeScreen()
        }
    }
}