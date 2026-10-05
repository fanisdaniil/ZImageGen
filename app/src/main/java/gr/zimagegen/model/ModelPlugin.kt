package gr.zimagegen.model

data class ModelPlugin(
    val id: String,
    val brand: String,
    val name: String,
    val description: String,
    val tag: String,
    val endpoint: String,
    val priceEstimate: Double,
    val provider: String = "Black Forest Labs",
    val isInstalled: Boolean = false,
    val isCheapAlternative: Boolean = false
)
