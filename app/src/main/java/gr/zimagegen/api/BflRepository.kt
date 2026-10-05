package gr.zimagegen.api

import gr.zimagegen.model.ImageGenerationResult
import gr.zimagegen.model.ImageResolution

class BflRepository(
    private val bflApi: BflApi
) {

    suspend fun generateImage(
        prompt: String,
        resolution: ImageResolution,
        endpoint: String? = null,
        apiKeyOverride: String? = null,
        modelName: String? = null,
        onLog: (String) -> Unit = {}
    ): ImageGenerationResult {
        return bflApi.generateImage(
            prompt = prompt,
            width = resolution.width,
            height = resolution.height,
            endpoint = endpoint ?: BflApi.DEFAULT_GENERATION_ENDPOINT,
            customApiKey = apiKeyOverride,
            modelName = modelName ?: "FLUX.2 Klein 9B",
            onLog = onLog
        )
    }

    suspend fun getCredits(): Double {
        return bflApi.getCredits()
    }
}