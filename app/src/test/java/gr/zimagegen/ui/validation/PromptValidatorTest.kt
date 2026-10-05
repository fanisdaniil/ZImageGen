package gr.zimagegen.ui.validation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PromptValidatorTest {

    @Test
    fun characterCount_ignoresWhitespace() {
        val count = PromptValidator.characterCount("  a b c \n \t ")
        assertEquals(3, count)
    }

    @Test
    fun canGenerate_returnsFalse_whenLessThanTenCharacters() {
        assertFalse(PromptValidator.canGenerate("123456789"))
        assertFalse(PromptValidator.canGenerate("   123   "))
        assertFalse(PromptValidator.canGenerate(""))
    }

    @Test
    fun canGenerate_returnsTrue_whenTenOrMoreCharacters() {
        assertTrue(PromptValidator.canGenerate("1234567890"))
        assertTrue(PromptValidator.canGenerate("A prompt with more than 10 characters"))
    }

    @Test
    fun remainingCharacters_calculatesCorrectRemainingCount() {
        assertEquals(10, PromptValidator.remainingCharacters(""))
        assertEquals(5, PromptValidator.remainingCharacters("12345"))
        assertEquals(0, PromptValidator.remainingCharacters("1234567890"))
        assertEquals(0, PromptValidator.remainingCharacters("1234567890 extra characters"))
    }
}
