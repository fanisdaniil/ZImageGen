package gr.zimagegen.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import gr.zimagegen.R
import gr.zimagegen.model.ModelPlugin

@Composable
fun PluginCatalogDialog(
    plugins: List<ModelPlugin>,
    onDismiss: () -> Unit,
    onInstallPlugin: (ModelPlugin, String?) -> Unit,
    onUninstallPlugin: (String) -> Unit,
    onAddCustomModel: (name: String, apiUrl: String, apiToken: String, price: Double?) -> Unit
) {
    var showCustomModelForm by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .height(620.dp),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.model_plugins_title),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = stringResource(R.string.model_plugins_subtitle),
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.60f)
                        )
                    }

                    TextButton(onClick = onDismiss) {
                        Text(
                            text = stringResource(R.string.close),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (!showCustomModelForm) {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(items = plugins, key = { it.id }) { plugin ->
                            PluginItemCard(
                                plugin = plugin,
                                onInstall = { key -> onInstallPlugin(plugin, key) },
                                onUninstall = { onUninstallPlugin(plugin.id) }
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedButton(
                                onClick = { showCustomModelForm = true },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text(
                                    text = "+ " + stringResource(R.string.add_custom_endpoint),
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                } else {
                    CustomEndpointForm(
                        onBack = { showCustomModelForm = false },
                        onSave = { name, url, token, price ->
                            onAddCustomModel(name, url, token, price)
                            showCustomModelForm = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun PluginItemCard(
    plugin: ModelPlugin,
    onInstall: (customKey: String?) -> Unit,
    onUninstall: () -> Unit
) {
    val cardShape = RoundedCornerShape(16.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(
                MaterialTheme.colorScheme.onSurface.copy(
                    alpha = if (plugin.isInstalled) 0.09f else 0.05f
                )
            )
            .border(
                width = 1.dp,
                color = if (plugin.isInstalled) {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                },
                shape = cardShape
            )
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = plugin.name,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (plugin.isCheapAlternative) {
                        Color(0xFF00C853).copy(alpha = 0.18f)
                    } else {
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                    }
                ) {
                    Text(
                        text = plugin.tag,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (plugin.isCheapAlternative) {
                            Color(0xFF00E676)
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = plugin.description,
                fontSize = 12.sp,
                lineHeight = 17.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = plugin.provider,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
                )

                if (plugin.isInstalled) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "✓ " + stringResource(R.string.installed),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 10.dp)
                        )

                        TextButton(
                            onClick = onUninstall,
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = Color(0xFFFF6B6B)
                            )
                        ) {
                            Text(
                                text = stringResource(R.string.delete),
                                fontSize = 12.sp
                            )
                        }
                    }
                } else {
                    Button(
                        onClick = { onInstall(null) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(
                            text = "↓ " + stringResource(R.string.install_plugin),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CustomEndpointForm(
    onBack: () -> Unit,
    onSave: (name: String, apiUrl: String, apiToken: String, price: Double?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var apiUrl by remember { mutableStateOf("") }
    var apiToken by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }

    val canSave = name.isNotBlank() && apiUrl.isNotBlank()

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.custom_api),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            TextButton(onClick = onBack) {
                Text(text = "← " + stringResource(R.string.back))
            }
        }

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = { Text(stringResource(R.string.model_name)) },
            colors = formFieldColors()
        )

        OutlinedTextField(
            value = apiUrl,
            onValueChange = { apiUrl = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = { Text(stringResource(R.string.api_url)) },
            colors = formFieldColors()
        )

        OutlinedTextField(
            value = apiToken,
            onValueChange = { apiToken = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = { Text(stringResource(R.string.api_token)) },
            colors = formFieldColors()
        )

        OutlinedTextField(
            value = price,
            onValueChange = { price = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            label = { Text(stringResource(R.string.price_per_request)) },
            colors = formFieldColors()
        )

        Spacer(modifier = Modifier.height(4.dp))

        Button(
            onClick = {
                onSave(name.trim(), apiUrl.trim(), apiToken.trim(), price.toDoubleOrNull())
            },
            enabled = canSave,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(text = stringResource(R.string.save))
        }
    }
}

@Composable
private fun formFieldColors() =
    OutlinedTextFieldDefaults.colors(
        focusedContainerColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f),
        unfocusedContainerColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.04f),
        focusedTextColor = MaterialTheme.colorScheme.onBackground,
        unfocusedTextColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f),
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.35f),
        cursorColor = MaterialTheme.colorScheme.primary,
        focusedLabelColor = MaterialTheme.colorScheme.primary,
        unfocusedLabelColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.70f)
    )
