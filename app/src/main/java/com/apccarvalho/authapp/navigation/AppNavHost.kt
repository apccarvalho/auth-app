package com.apccarvalho.authapp.navigation

import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.apccarvalho.authapp.data.AppContainer
import com.apccarvalho.authapp.ui.auth.AuthScreen
import com.apccarvalho.authapp.ui.dashboard.DashboardScreen
import com.apccarvalho.authapp.ui.forgot.ForgotPasswordScreen

private const val FADE_MS = 250
private const val SLIDE_MS = 300

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    // Decidido uma única vez: sessão salva abre direto no Dashboard.
    // currentUser é lido do cache local do Firebase, sem rede — não há atraso.
    val startDestination: Any = remember {
        if (AppContainer.authRepository.currentUser != null) DashboardRoute else AuthRoute
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        // Padrão: troca suave por fade (login ↔ Dashboard).
        enterTransition = { fadeIn(tween(FADE_MS)) },
        exitTransition = { fadeOut(tween(FADE_MS)) },
        popEnterTransition = { fadeIn(tween(FADE_MS)) },
        popExitTransition = { fadeOut(tween(FADE_MS)) },
    ) {

        composable<AuthRoute> {
            AuthScreen(
                onAuthenticated = {
                    navController.navigate(DashboardRoute) {
                        // Remove o login da pilha: "Voltar" no Dashboard fecha o app.
                        popUpTo<AuthRoute> { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onForgotPassword = { email ->
                    navController.navigate(ForgotPasswordRoute(email)) {
                        launchSingleTop = true
                    }
                },
            )
        }

        // Recuperação de senha "empilha" sobre o login: entra deslizando da direita.
        composable<ForgotPasswordRoute>(
            enterTransition = { slideIntoContainer(SlideDirection.Start, tween(SLIDE_MS)) },
            popExitTransition = { slideOutOfContainer(SlideDirection.End, tween(SLIDE_MS)) },
        ) {
            ForgotPasswordScreen(onBack = { navController.popBackStack() })
        }

        composable<DashboardRoute> {
            DashboardScreen(
                onSignedOut = {
                    navController.navigate(AuthRoute) {
                        // Remove o Dashboard: "Voltar" no login não reabre a área logada.
                        popUpTo<DashboardRoute> { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )
        }
    }
}
