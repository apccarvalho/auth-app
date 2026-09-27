package com.apccarvalho.authapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.apccarvalho.authapp.ui.theme.AuthAppTheme
import com.apccarvalho.authapp.ui.theme.Mint100
import com.apccarvalho.authapp.ui.theme.Mint200
import com.apccarvalho.authapp.ui.theme.Mint300
import com.apccarvalho.authapp.ui.theme.Mint400
import com.apccarvalho.authapp.ui.theme.Mint50
import com.apccarvalho.authapp.ui.theme.Sizes
import com.apccarvalho.authapp.ui.theme.Spacing
import com.google.firebase.Firebase
import com.google.firebase.auth.auth


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // O app é sempre claro: força ícones escuros nas barras do sistema,
        // mesmo com o celular em modo escuro.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT,
            ),
            navigationBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT,
            ),
        )
        super.onCreate(savedInstanceState)

        // Se o google-services.json estiver ausente ou com package errado,
        // Firebase.auth lança IllegalStateException
        val firebaseStatus = runCatching { Firebase.auth }.fold(
            onSuccess = { auth ->
                val user = auth.currentUser
                if (user == null) "Firebase conectado. Nenhum usuário logado."
                else "Firebase conectado. Sessão ativa: ${user.email}"
            },
            onFailure = { "Falha ao iniciar o Firebase: ${it.message}" },
        )

        setContent {
            AuthAppTheme {
                ThemeCheckScreen(firebaseStatus)
            }
        }
    }
}

@Composable
private fun ThemeCheckScreen(firebaseStatus: String) {
    var email by remember { mutableStateOf("") }

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.TopCenter,
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = Sizes.formMaxWidth)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                Text("Verificação do tema", style = MaterialTheme.typography.displaySmall)

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    ),
                    shape = MaterialTheme.shapes.large,
                ) {
                    Text(
                        text = firebaseStatus,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(Spacing.md),
                    )
                }

                Text("Paleta", style = MaterialTheme.typography.titleMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                    listOf(Mint50, Mint100, Mint200, Mint300, Mint400).forEach { Swatch(it) }
                }

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("E-mail") },
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth(),
                )

                // Botão principal: usa primaryContainer (#56DDA7) com texto escuro
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth().height(Sizes.buttonHeight),
                ) { Text("Entrar") }

                // Mesmo botão em estado de carregamento (prévia da etapa 5)
                Button(
                    onClick = {},
                    enabled = false,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth().height(Sizes.buttonHeight),
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(Sizes.inlineLoader),
                        strokeWidth = Sizes.loaderStroke,
                    )
                }

                TextButton(onClick = {}) { Text("Esqueci minha senha") }
            }
        }
    }
}

@Composable
private fun Swatch(color: Color) {
    Box(
        Modifier
            .size(48.dp)
            .background(color, MaterialTheme.shapes.small)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.small),
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFEBFBF4)
@Composable
private fun ThemeCheckPreview() {
    AuthAppTheme { ThemeCheckScreen("Pré-visualização (Firebase não roda no Preview)") }
}
