package com.apccarvalho.authapp.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PasswordStrengthTest {

    private fun level(p: String) = PasswordStrength.evaluate(p).level

    @Test fun `vazia`() = assertEquals(StrengthLevel.EMPTY, level(""))

    @Test fun `abaixo de 6 caracteres é sempre fraca`() =
        assertEquals(StrengthLevel.WEAK, level("Ab1!"))

    @Test fun `só números é fraca`() = assertEquals(StrengthLevel.WEAK, level("12345678"))

    @Test fun `três critérios é média`() = assertEquals(StrengthLevel.FAIR, level("abcdef12"))

    @Test fun `quatro critérios é boa`() = assertEquals(StrengthLevel.GOOD, level("Abcdef12"))

    @Test fun `todos os critérios é forte`() = assertEquals(StrengthLevel.STRONG, level("Abcdef1!"))

    @Test fun `critérios atendidos são reportados`() {
        val met = PasswordStrength.evaluate("abc1").met
        assertTrue(PasswordCriterion.LOWERCASE in met)
        assertTrue(PasswordCriterion.DIGIT in met)
        assertEquals(2, met.size)
    }
}
