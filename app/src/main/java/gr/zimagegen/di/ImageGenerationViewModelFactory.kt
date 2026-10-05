package gr.zimagegen.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import gr.zimagegen.api.BflApi
import gr.zimagegen.api.BflRepository
import gr.zimagegen.security.DevApiKeyProvider
import gr.zimagegen.ui.screens.ImageGenerationViewModel

/**
 * Creates the ViewModel dependency graph for now.
 *
 * We will replace this temporary construction with proper
 * dependency injection later.
 */
class ImageGenerationViewModelFactory :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        val apiKeyProvider = DevApiKeyProvider()

        val bflApi = BflApi(
            apiKeyProvider = apiKeyProvider
        )

        val repository = BflRepository(
            bflApi = bflApi
        )

        return ImageGenerationViewModel(
            repository = repository
        ) as T
    }
}