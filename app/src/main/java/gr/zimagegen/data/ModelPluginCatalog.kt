package gr.zimagegen.data

import gr.zimagegen.model.ModelPlugin

object ModelPluginCatalog {

    val supportedPlugins: List<ModelPlugin> = listOf(
        ModelPlugin(
            id = "bfl-flux-schnell",
            brand = "FLUX.1",
            name = "FLUX.1 Schnell",
            description = "4-step ultra fast distillation. Most budget-friendly BFL model for rapid prototyping.",
            tag = "Cheap & Fast (~$0.003)",
            endpoint = "https://api.bfl.ai/v1/flux-schnell",
            priceEstimate = 0.003,
            provider = "Black Forest Labs",
            isCheapAlternative = true
        ),
        ModelPlugin(
            id = "bfl-flux-dev",
            brand = "FLUX.1",
            name = "FLUX.1 Dev",
            description = "28-step base guidance model with exceptional prompt accuracy and photorealistic anatomy.",
            tag = "Balanced Base (~$0.025)",
            endpoint = "https://api.bfl.ai/v1/flux-dev",
            priceEstimate = 0.025,
            provider = "Black Forest Labs",
            isCheapAlternative = false
        ),
        ModelPlugin(
            id = "bfl-flux-pro-1.1",
            brand = "FLUX 1.1",
            name = "FLUX 1.1 Pro",
            description = "Next-gen flagship model. 3x faster generation speed and superior high-resolution detail.",
            tag = "Pro Flagship (~$0.040)",
            endpoint = "https://api.bfl.ai/v1/flux-pro-1.1",
            priceEstimate = 0.040,
            provider = "Black Forest Labs",
            isCheapAlternative = false
        ),
        ModelPlugin(
            id = "bfl-flux-pro",
            brand = "FLUX.1",
            name = "FLUX.1 Pro",
            description = "Studio-grade photorealism, fine material textures, and cinematic lighting synthesis.",
            tag = "Cinema Quality (~$0.050)",
            endpoint = "https://api.bfl.ai/v1/flux-pro",
            priceEstimate = 0.050,
            provider = "Black Forest Labs",
            isCheapAlternative = false
        )
    )
}
