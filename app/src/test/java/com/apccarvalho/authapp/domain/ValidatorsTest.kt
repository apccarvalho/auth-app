package com.apccarvalho.authapp.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ValidatorsTest {

    @Test fun `e-mail vazio é obrigatório`() =
        assertEquals(FieldError.REQUIRED, Validators.email("   "))

    @Test fun `e-mails inválidos são rejeitados`() {
        listOf("andreia", "andreia@", "andreia@gmail", "@gmail.com", "a b@gmail.com")
            .forEach { assertEquals(it, FieldError.INVALID_EMAIL, Validators.email(it)) }
    }

    @Test fun `e-mails válidos passam, inclusive com espaços nas pontas`() {
        listOf("andreia@gmail.com", "a.c+teste@aluno.ufu.br", "  nome@site.io  ")
            .forEach { assertNull(it, Validators.email(it)) }
    }

    @Test fun `senha de cadastro respeita o mínimo do Firebase`() {
        assertEquals(FieldError.REQUIRED, Validators.newPassword(""))
        assertEquals(FieldError.PASSWORD_TOO_SHORT, Validators.newPassword("12345"))
        assertNull(Validators.newPassword("123456"))
    }

    @Test fun `senha de login só exige preenchimento`() {
        assertEquals(FieldError.REQUIRED, Validators.loginPassword(""))
        assertNull(Validators.loginPassword("1"))
    }

    @Test fun `confirmação precisa ser igual`() {
        assertEquals(FieldError.REQUIRED, Validators.confirmPassword("abc123", ""))
        assertEquals(FieldError.PASSWORDS_DONT_MATCH, Validators.confirmPassword("abc123", "abc124"))
        assertNull(Validators.confirmPassword("abc123", "abc123"))
    }

    @Test fun `nome tem limites de tamanho`() {
        assertEquals(FieldError.REQUIRED, Validators.name("  "))
        assertEquals(FieldError.NAME_TOO_SHORT, Validators.name("A"))
        assertEquals(FieldError.NAME_TOO_LONG, Validators.name("A".repeat(41)))
        assertNull(Validators.name("Andréia"))
    }
}
