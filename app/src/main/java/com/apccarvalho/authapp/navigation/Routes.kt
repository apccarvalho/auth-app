package com.apccarvalho.authapp.navigation

import kotlinx.serialization.Serializable

/*
 * Rotas tipadas (Navigation Compose 2.8+): cada destino é uma classe
 * @Serializable em vez de uma string como "dashboard". Um erro de digitação
 * no nome da rota vira erro de compilação, não um crash em tempo de execução.
 */
@Serializable
data object AuthRoute

@Serializable
data object DashboardRoute

/** Recebe o e-mail já digitado no login, para pré-preencher o campo. */
@Serializable
data class ForgotPasswordRoute(val email: String = "")
