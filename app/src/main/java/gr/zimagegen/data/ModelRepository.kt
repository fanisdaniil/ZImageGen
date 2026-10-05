package gr.zimagegen.data

import gr.zimagegen.model.CustomImageModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class ModelRepository(
    private val modelStore: ModelStore
) {

    val customModels: Flow<List<CustomImageModel>>
        get() = modelStore.customModels

    suspend fun addModel(
        model: CustomImageModel
    ) {
        val currentModels =
            modelStore.customModels.first()

        modelStore.saveModels(
            currentModels + model
        )
    }

    suspend fun deleteModel(
        modelId: String
    ) {
        modelStore.deleteModel(modelId)
    }
}