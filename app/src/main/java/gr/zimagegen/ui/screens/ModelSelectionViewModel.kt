package gr.zimagegen.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import gr.zimagegen.api.BflRepository
import gr.zimagegen.data.ModelPluginCatalog
import gr.zimagegen.data.ModelRepository
import gr.zimagegen.model.CreditsUiState
import gr.zimagegen.model.CustomImageModel
import gr.zimagegen.model.ImageModel
import gr.zimagegen.model.ModelPlugin
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

data class ModelSelectionInfo(
    val name: String,
    val endpoint: String? = null,
    val apiKey: String? = null
)

class ModelSelectionViewModel(
    private val modelRepository: ModelRepository,
    private val bflRepository: BflRepository
) : ViewModel() {

    val customModels: StateFlow<List<CustomImageModel>> =
        modelRepository.customModels.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val plugins: StateFlow<List<ModelPlugin>> =
        customModels.map { installedList ->
            val installedIds = installedList.map { it.id }.toSet()
            ModelPluginCatalog.supportedPlugins.map { plugin ->
                plugin.copy(isInstalled = installedIds.contains(plugin.id))
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ModelPluginCatalog.supportedPlugins
        )

    private val _creditsState = MutableStateFlow<CreditsUiState>(CreditsUiState.NotFetched)
    val creditsState: StateFlow<CreditsUiState> = _creditsState.asStateFlow()

    private val _selectedBuiltInModel = MutableStateFlow<ImageModel?>(ImageModel.FLUX_KLEIN_9B)
    val selectedBuiltInModel: StateFlow<ImageModel?> = _selectedBuiltInModel.asStateFlow()

    private val _selectedCustomModel = MutableStateFlow<CustomImageModel?>(null)
    val selectedCustomModel: StateFlow<CustomImageModel?> = _selectedCustomModel.asStateFlow()

    init {
        loadCredits()
    }

    fun loadCredits() {
        if (_creditsState.value is CreditsUiState.Loading) return

        viewModelScope.launch {
            _creditsState.value = CreditsUiState.Loading
            try {
                val credits = bflRepository.getCredits()
                _creditsState.value = CreditsUiState.Available(amount = credits)
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (_: Exception) {
                _creditsState.value = CreditsUiState.Unavailable
            }
        }
    }

    fun selectBuiltInModel(model: ImageModel) {
        _selectedBuiltInModel.value = model
        _selectedCustomModel.value = null
    }

    fun selectCustomModel(model: CustomImageModel) {
        _selectedCustomModel.value = model
        _selectedBuiltInModel.value = null
    }

    fun installPlugin(plugin: ModelPlugin, customApiKey: String? = null) {
        viewModelScope.launch {
            val model = CustomImageModel(
                id = plugin.id,
                name = plugin.name,
                apiUrl = plugin.endpoint,
                apiToken = customApiKey ?: "",
                pricePerRequest = plugin.priceEstimate
            )
            modelRepository.addModel(model)
        }
    }

    fun uninstallPlugin(pluginId: String) {
        deleteModel(pluginId)
    }

    fun addCustomModel(name: String, apiUrl: String, apiToken: String, price: Double?) {
        viewModelScope.launch {
            val model = CustomImageModel(
                id = UUID.randomUUID().toString(),
                name = name,
                apiUrl = apiUrl,
                apiToken = apiToken,
                pricePerRequest = price
            )
            modelRepository.addModel(model)
        }
    }

    fun deleteModel(modelId: String) {
        viewModelScope.launch {
            modelRepository.deleteModel(modelId)
            if (_selectedCustomModel.value?.id == modelId) {
                _selectedCustomModel.value = null
                _selectedBuiltInModel.value = ImageModel.FLUX_KLEIN_9B
            }
        }
    }

    fun getSelectedModelInfo(): ModelSelectionInfo {
        val custom = _selectedCustomModel.value
        if (custom != null) {
            return ModelSelectionInfo(
                name = custom.name,
                endpoint = custom.apiUrl,
                apiKey = custom.apiToken.takeIf { it.isNotBlank() }
            )
        }
        val builtIn = _selectedBuiltInModel.value
        return ModelSelectionInfo(
            name = builtIn?.let { "${it.brand} ${it.modelName}" } ?: "FLUX.2 Klein 9B",
            endpoint = null,
            apiKey = null
        )
    }
}

class ModelSelectionViewModelFactory(
    private val modelRepository: ModelRepository,
    private val bflRepository: BflRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ModelSelectionViewModel(
            modelRepository = modelRepository,
            bflRepository = bflRepository
        ) as T
    }
}
