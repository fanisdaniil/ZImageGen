package gr.zimagegen.model

enum class ImageModel(
    val brand: String,
    val modelName: String,
    val description: String,
    val remainingTokens: String
) {
    FLUX_KLEIN_9B(
        brand = "FLUX.2",
        modelName = "Klein 9B",
        description = "Fast generation with high-quality photorealistic results.",
        remainingTokens = ""
    )
}