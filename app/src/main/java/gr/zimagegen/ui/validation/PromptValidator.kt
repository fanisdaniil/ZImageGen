package gr.zimagegen.ui.validation

object PromptValidator {

    private const val MINIMUM_CHARACTERS = 10

    fun characterCount(prompt: String): Int {
        return prompt.count { !it.isWhitespace() }
    }

    fun canGenerate(prompt: String): Boolean {
        return characterCount(prompt) >= MINIMUM_CHARACTERS
    }

    fun remainingCharacters(prompt: String): Int {
        return (MINIMUM_CHARACTERS - characterCount(prompt))
            .coerceAtLeast(0)
    }
}