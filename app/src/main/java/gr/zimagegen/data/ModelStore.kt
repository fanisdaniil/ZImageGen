package gr.zimagegen.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import gr.zimagegen.model.CustomImageModel
import org.json.JSONArray
import org.json.JSONObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.modelDataStore by preferencesDataStore(
    name = "models"
)

class ModelStore(
    private val context: Context
) {

    private companion object {
        val CUSTOM_MODELS_KEY =
            stringPreferencesKey("custom_models")
    }

    val customModels: Flow<List<CustomImageModel>> =
        context.modelDataStore.data.map { preferences ->
            val json = preferences[CUSTOM_MODELS_KEY]
                ?: return@map emptyList()

            deserializeModels(json)
        }

    suspend fun saveModels(
        models: List<CustomImageModel>
    ) {
        context.modelDataStore.edit { preferences ->
            preferences[CUSTOM_MODELS_KEY] =
                serializeModels(models)
        }
    }

    suspend fun deleteModel(
        modelId: String
    ) {
        context.modelDataStore.edit { preferences ->
            val existingJson =
                preferences[CUSTOM_MODELS_KEY]
                    ?: return@edit

            val models = deserializeModels(existingJson)

            val updatedModels = models.filter {
                it.id != modelId
            }

            preferences[CUSTOM_MODELS_KEY] =
                serializeModels(updatedModels)
        }
    }

    private fun serializeModels(
        models: List<CustomImageModel>
    ): String {
        val jsonArray = JSONArray()

        models.forEach { model ->
            val jsonObject = JSONObject().apply {
                put("id", model.id)
                put("name", model.name)
                put("apiUrl", model.apiUrl)
                put("apiToken", model.apiToken)
                put(
                    "pricePerRequest",
                    model.pricePerRequest
                )
            }

            jsonArray.put(jsonObject)
        }

        return jsonArray.toString()
    }

    private fun deserializeModels(
        json: String
    ): List<CustomImageModel> {
        val jsonArray = JSONArray(json)
        val models = mutableListOf<CustomImageModel>()

        for (index in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(index)

            models += CustomImageModel(
                id = jsonObject.getString("id"),
                name = jsonObject.getString("name"),
                apiUrl = jsonObject.getString("apiUrl"),
                apiToken = jsonObject.getString("apiToken"),
                pricePerRequest =
                    if (jsonObject.isNull("pricePerRequest")) {
                        null
                    } else {
                        jsonObject.getDouble("pricePerRequest")
                    }
            )
        }

        return models
    }
}