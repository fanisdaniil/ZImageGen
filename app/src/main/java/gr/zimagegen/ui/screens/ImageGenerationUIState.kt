package gr.zimagegen.ui.screens

enum class ImageGenerationStatus {
    PROMPTING,
    GENERATING,
    GENERATED
}

data class ImageGenerationUiState(
    val status: ImageGenerationStatus = ImageGenerationStatus.PROMPTING,
    val logs: List<String> = emptyList(),
    val generatedImageUrl: String? = null,
    val generationCost: Double? = null,
    val errorMessage: String? = null
)