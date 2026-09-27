package com.apccarvalho.authapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.apccarvalho.authapp.data.AppContainer
import com.apccarvalho.authapp.domain.AuthUser
import com.apccarvalho.authapp.ui.auth.AuthScreen
import com.apccarvalho.authapp.ui.theme.AuthAppTheme
import com.apccarvalho.authapp.ui.theme.Spacing

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT,
            ),
            navigationBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT,
            ),
        )
        super.onCreate(savedInstanceState)
        setContent {
            AuthAppTheme {
                Stage3Host()
            }
        }
    }
}

/*
 * troca provisória entre a tela de login e uma tela simples de
 * "logado".
 */
@Composable
private fun Stage3Host() {
    val repository = AppContainer.authRepository
    var user by remember { mutableStateOf(repository.currentUser) }

    val loggedUser = user
    if (loggedUser == null) {
        AuthScreen(
            onAuthenticated = { user = it },
            onForgotPassword = { /* implementado na etapa 6 */ },
        )
    } else {
        TemporaryHome(loggedUser) {
            repository.signOut()
            user = null
        }
    }
}

@Composable
private fun TemporaryHome(user: AuthUser, onSignOut: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            stringResource(R.string.temp_greeting, user.greetingName),
            style = MaterialTheme.typography.headlineMedium,
        )
        Text(stringResource(R.string.temp_logged_as, user.email))
        OutlinedButton(onClick = onSignOut) { Text(stringResource(R.string.action_sign_out)) }
    }
}
