package gr.zimagegen.ui.screens

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gr.zimagegen.api.BflRepository
import gr.zimagegen.model.ImageGenerationResult
import gr.zimagegen.model.ImageResolution
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ImageGenerationViewModel(
    private val repository: BflRepository
) : ViewModel() {

    private val _prompt = MutableStateFlow("")
    val prompt: StateFlow<String> = _prompt.asStateFlow()

    private val _selectedResolution = MutableStateFlow(ImageResolution.SQUARE)
    val selectedResolution: StateFlow<ImageResolution> = _selectedResolution.asStateFlow()

    private val _promptInteracted = MutableStateFlow(false)
    val promptInteracted: StateFlow<Boolean> = _promptInteracted.asStateFlow()

    private val _generatedBitmap = MutableStateFlow<Bitmap?>(null)
    val generatedBitmap: StateFlow<Bitmap?> = _generatedBitmap.asStateFlow()

    private val _modelDisplayName = MutableStateFlow("FLUX.2 Klein 9B")
    val modelDisplayName: StateFlow<String> = _modelDisplayName.asStateFlow()

    private var activeEndpoint: String? = null
    private var activeApiKey: String? = null

    private val _uiState = MutableStateFlow(ImageGenerationUiState())
    val uiState: StateFlow<ImageGenerationUiState> = _uiState.asStateFlow()

    fun updatePrompt(newPrompt: String) {
        _prompt.value = newPrompt
    }

    fun updateResolution(newResolution: ImageResolution) {
        _selectedResolution.value = newResolution
    }

    fun setPromptInteracted(interacted: Boolean) {
        _promptInteracted.value = interacted
    }

    fun setGeneratedBitmap(bitmap: Bitmap?) {
        _generatedBitmap.value = bitmap
    }

    fun setModelConfig(name: String, endpoint: String?, apiKey: String?) {
        if (name.isNotBlank()) {
            _modelDisplayName.value = name
        }
        activeEndpoint = endpoint.takeIf { !it.isNullOrBlank() }
        activeApiKey = apiKey.takeIf { !it.isNullOrBlank() }
    }

    fun generate() {
        if (_uiState.value.status == ImageGenerationStatus.GENERATING) {
            return
        }

        val currentPrompt = _prompt.value
        val currentResolution = _selectedResolution.value
        val endpoint = activeEndpoint
        val apiKey = activeApiKey
        val modelName = _modelDisplayName.value

        viewModelScope.launch {
            _uiState.value = ImageGenerationUiState(
                status = ImageGenerationStatus.GENERATING,
                logs = listOf("Starting generation...")
            )

            try {
                val result: ImageGenerationResult = repository.generateImage(
                    prompt = currentPrompt,
                    resolution = currentResolution,
                    endpoint = endpoint,
                    apiKeyOverride = apiKey,
                    modelName = modelName,
                    onLog = { message ->
                        _uiState.value = _uiState.value.copy(
                            logs = _uiState.value.logs + message
                        )
                    }
                )

                _uiState.value = _uiState.value.copy(
                    status = ImageGenerationStatus.GENERATED,
                    generatedImageUrl = result.imageUrl,
                    generationCost = result.cost
                )
            } catch (exception: Exception) {
                _uiState.value = _uiState.value.copy(
                    status = ImageGenerationStatus.PROMPTING,
                    generatedImageUrl = null,
                    errorMessage = exception.message ?: "Something went wrong"
                )
            }
        }
    }
}
