package com.apccarvalho.authapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.apccarvalho.authapp.data.AppContainer
import com.apccarvalho.authapp.ui.dashboard.DashboardScreen
import com.apccarvalho.authapp.ui.theme.auth.AuthScreen

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    // Decidido uma única vez: sessão salva abre direto no Dashboard.
    // currentUser é lido do cache local do Firebase, sem rede — não há atraso.
    val startDestination: Any = remember {
        if (AppContainer.authRepository.currentUser != null) DashboardRoute else AuthRoute
    }

    NavHost(navController = navController, startDestination = startDestination) {

        composable<AuthRoute> {
            AuthScreen(
                onAuthenticated = {
                    navController.navigate(DashboardRoute) {
                        // Remove o login da pilha: "Voltar" no Dashboard fecha o app.
                        popUpTo<AuthRoute> { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onForgotPassword = { /* etapa 6 */ },
            )
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
