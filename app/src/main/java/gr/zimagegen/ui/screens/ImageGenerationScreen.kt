package gr.zimagegen.ui.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import gr.zimagegen.BuildConfig
import gr.zimagegen.R
import gr.zimagegen.di.ImageGenerationViewModelFactory
import gr.zimagegen.ui.components.AppScreen
import gr.zimagegen.ui.components.BackButton
import gr.zimagegen.ui.components.DebugLog
import gr.zimagegen.ui.components.ImageActions
import gr.zimagegen.ui.components.ImagePreview
import gr.zimagegen.ui.components.PromptInput
import gr.zimagegen.ui.components.ResolutionSelector
import gr.zimagegen.ui.validation.PromptValidator
import kotlinx.coroutines.launch

@Composable
fun ImageGenerationScreen(
    onBack: () -> Unit,
    modelName: String = "",
    endpoint: String = "",
    apiKey: String = ""
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val viewModel: ImageGenerationViewModel = viewModel(
        factory = ImageGenerationViewModelFactory()
    )

    LaunchedEffect(modelName, endpoint, apiKey) {
        viewModel.setModelConfig(modelName, endpoint, apiKey)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val prompt by viewModel.prompt.collectAsStateWithLifecycle()
    val selectedResolution by viewModel.selectedResolution.collectAsStateWithLifecycle()
    val promptInteracted by viewModel.promptInteracted.collectAsStateWithLifecycle()
    val generatedBitmap by viewModel.generatedBitmap.collectAsStateWithLifecycle()
    val currentModelName by viewModel.modelDisplayName.collectAsStateWithLifecycle()

    val characterCount = PromptValidator.characterCount(prompt)
    val canGenerate = PromptValidator.canGenerate(prompt)
    val remainingCharacters = PromptValidator.remainingCharacters(prompt)
    val showPromptError = promptInteracted && !canGenerate

    val imeBottom = WindowInsets.ime.getBottom(LocalDensity.current)
    val keyboardOpen = imeBottom > 0

    val previewHeight by animateDpAsState(
        targetValue = if (keyboardOpen) 100.dp else 260.dp,
        label = "previewHeight"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        AppScreen(
            actions = {
                BackButton(onBack = onBack)
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.create_image),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f),
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Medium
                )

                if (currentModelName.isNotBlank()) {
                    Text(
                        text = currentModelName,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                ImagePreview(
                    height = previewHeight,
                    status = uiState.status,
                    imageUrl = uiState.generatedImageUrl,
                    onBitmapLoaded = { viewModel.setGeneratedBitmap(it) }
                )

                if (uiState.status == ImageGenerationStatus.GENERATED && uiState.generationCost != null) {
                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.72f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.generation_cost),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 14.sp
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            Text(
                                text = stringResource(R.string.tokens_format, uiState.generationCost!!),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                if (uiState.status == ImageGenerationStatus.GENERATED) {
                    Spacer(modifier = Modifier.height(12.dp))

                    generatedBitmap?.let { bitmap ->
                        ImageActions(
                            bitmap = bitmap,
                            onShowMessage = { message ->
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(message)
                                }
                            }
                        )
                    }
                }

                if (BuildConfig.DEBUG_ENABLED_FOR_APIS.toBoolean() && uiState.logs.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    DebugLog(logs = uiState.logs)
                }

                if (uiState.errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = uiState.errorMessage!!,
                        color = Color(0xFFFF8585),
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Column(modifier = Modifier.fillMaxWidth()) {
                    PromptInput(
                        value = prompt,
                        onValueChange = { viewModel.updatePrompt(it) },
                        showError = showPromptError,
                        onInteraction = { viewModel.setPromptInteracted(true) }
                    )

                    if (showPromptError) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (characterCount == 0) {
                                stringResource(R.string.prompt_min_characters)
                            } else {
                                stringResource(R.string.prompt_more_characters_needed, remainingCharacters)
                            },
                            color = Color(0xFFFF8585),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                ResolutionSelector(
                    selectedResolution = selectedResolution,
                    onResolutionSelected = { viewModel.updateResolution(it) }
                )

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        keyboardController?.hide()
                        viewModel.generate()
                    },
                    enabled = canGenerate && uiState.status != ImageGenerationStatus.GENERATING,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
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
                        text = if (uiState.status == ImageGenerationStatus.GENERATING) {
                            stringResource(R.string.generating)
                        } else {
                            stringResource(R.string.generate)
                        },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp)
        )
    }
}