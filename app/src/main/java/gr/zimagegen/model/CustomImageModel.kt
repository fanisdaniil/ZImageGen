package gr.zimagegen.model

data class CustomImageModel(
    val id: String,
    val name: String,
    val apiUrl: String,
    val apiToken: String,
    val pricePerRequest: Double?
)