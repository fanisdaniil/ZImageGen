package gr.zimagegen.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import gr.zimagegen.R
import gr.zimagegen.api.BflApi
import gr.zimagegen.api.BflRepository
import gr.zimagegen.data.ModelRepository
import gr.zimagegen.data.ModelStore
import gr.zimagegen.model.CustomImageModel
import gr.zimagegen.model.ImageModel
import gr.zimagegen.security.DevApiKeyProvider
import gr.zimagegen.ui.components.AddModelButton
import gr.zimagegen.ui.components.AppScreen
import gr.zimagegen.ui.components.BackButton
import gr.zimagegen.ui.components.CustomModelCard
import gr.zimagegen.ui.components.ModelCard
import gr.zimagegen.ui.components.PluginCatalogDialog

@Composable
fun ModelSelectionScreen(
    onBack: () -> Unit,
    onContinue: (ModelSelectionInfo) -> Unit
) {
    val context = LocalContext.current

    val viewModel: ModelSelectionViewModel = viewModel(
        factory = remember {
            ModelSelectionViewModelFactory(
                modelRepository = ModelRepository(ModelStore(context)),
                bflRepository = BflRepository(BflApi(DevApiKeyProvider()))
            )
        }
    )

    val customModels by viewModel.customModels.collectAsStateWithLifecycle()
    val plugins by viewModel.plugins.collectAsStateWithLifecycle()
    val creditsState by viewModel.creditsState.collectAsStateWithLifecycle()
    val selectedBuiltInModel by viewModel.selectedBuiltInModel.collectAsStateWithLifecycle()
    val selectedCustomModel by viewModel.selectedCustomModel.collectAsStateWithLifecycle()

    var showPluginCatalog by remember { mutableStateOf(false) }
    var modelToDelete by remember { mutableStateOf<CustomImageModel?>(null) }

    val builtInModels = listOf(ImageModel.FLUX_KLEIN_9B)

    AppScreen(
        actions = {
            BackButton(onBack = onBack)
            AddModelButton(onClick = { showPluginCatalog = true })
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.choose_your_model),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f),
                fontSize = 30.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(28.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                items(
                    items = builtInModels,
                    key = { it.name }
                ) { model ->
                    ModelCard(
                        imageModel = model,
                        selected = selectedBuiltInModel == model,
                        creditsState = creditsState,
                        onClick = { viewModel.selectBuiltInModel(model) }
                    )
                }

                items(
                    items = customModels,
                    key = { it.id }
                ) { model ->
                    CustomModelCard(
                        model = model,
                        selected = selectedCustomModel == model,
                        onClick = { viewModel.selectCustomModel(model) },
                        onDelete = { modelToDelete = model }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = {
                        onContinue(viewModel.getSelectedModelInfo())
                    },
                    enabled = selectedBuiltInModel != null || selectedCustomModel != null,
                    modifier = Modifier
                        .width(200.dp)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.30f),
                        disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.50f)
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 2.dp,
                        pressedElevation = 4.dp
                    )
                ) {
                    Text(
                        text = stringResource(R.string.continue_button),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    if (showPluginCatalog) {
        PluginCatalogDialog(
            plugins = plugins,
            onDismiss = { showPluginCatalog = false },
            onInstallPlugin = { plugin, customKey ->
                viewModel.installPlugin(plugin, customKey)
            },
            onUninstallPlugin = { pluginId ->
                viewModel.uninstallPlugin(pluginId)
            },
            onAddCustomModel = { name, url, token, price ->
                viewModel.addCustomModel(name, url, token, price)
            }
        )
    }

    modelToDelete?.let { model ->
        DeleteModelDialog(
            model = model,
            onDismiss = { modelToDelete = null },
            onConfirm = {
                viewModel.deleteModel(model.id)
                modelToDelete = null
            }
        )
    }
}

@Composable
private fun DeleteModelDialog(
    model: CustomImageModel,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(R.string.delete_model))
        },
        text = {
            Text(
                text = stringResource(
                    R.string.delete_model_confirmation,
                    model.name
                )
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = stringResource(R.string.delete))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.cancel))
            }
        }
    )
}