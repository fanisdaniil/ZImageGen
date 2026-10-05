package gr.zimagegen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import gr.zimagegen.ui.components.AuroraBackground
import gr.zimagegen.ui.screens.ImageGenerationScreen
import gr.zimagegen.ui.screens.ModelSelectionScreen
import gr.zimagegen.ui.screens.WelcomeScreen
import gr.zimagegen.ui.theme.ZImageGenTheme
import java.net.URLDecoder
import java.net.URLEncoder

private object Routes {
    const val WELCOME = "welcome"
    const val MODEL_SELECTION = "model_selection"
    const val IMAGE_GENERATION = "image_generation?modelName={modelName}&endpoint={endpoint}&apiKey={apiKey}"
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            ZImageGenTheme {
                val navController = rememberNavController()

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .safeDrawingPadding()
                ) {

                    AuroraBackground(
                        modifier = Modifier.fillMaxSize()
                    )

                    NavHost(
                        navController = navController,
                        startDestination = Routes.WELCOME
                    ) {

                        composable(Routes.WELCOME) {
                            WelcomeScreen(
                                onContinue = {
                                    navController.navigate(Routes.MODEL_SELECTION)
                                }
                            )
                        }

                        composable(Routes.MODEL_SELECTION) {
                            ModelSelectionScreen(
                                onBack = {
                                    navController.popBackStack()
                                },
                                onContinue = { info ->
                                    val encodedName = URLEncoder.encode(info.name, "UTF-8")
                                    val encodedEndpoint = URLEncoder.encode(info.endpoint ?: "", "UTF-8")
                                    val encodedApiKey = URLEncoder.encode(info.apiKey ?: "", "UTF-8")
                                    navController.navigate("image_generation?modelName=$encodedName&endpoint=$encodedEndpoint&apiKey=$encodedApiKey")
                                }
                            )
                        }

                        composable(
                            route = Routes.IMAGE_GENERATION,
                            arguments = listOf(
                                navArgument("modelName") {
                                    type = NavType.StringType
                                    defaultValue = ""
                                },
                                navArgument("endpoint") {
                                    type = NavType.StringType
                                    defaultValue = ""
                                },
                                navArgument("apiKey") {
                                    type = NavType.StringType
                                    defaultValue = ""
                                }
                            )
                        ) { backStackEntry ->
                            val rawModelName = backStackEntry.arguments?.getString("modelName") ?: ""
                            val rawEndpoint = backStackEntry.arguments?.getString("endpoint") ?: ""
                            val rawApiKey = backStackEntry.arguments?.getString("apiKey") ?: ""

                            val decodedModelName = if (rawModelName.isNotEmpty()) URLDecoder.decode(rawModelName, "UTF-8") else ""
                            val decodedEndpoint = if (rawEndpoint.isNotEmpty()) URLDecoder.decode(rawEndpoint, "UTF-8") else ""
                            val decodedApiKey = if (rawApiKey.isNotEmpty()) URLDecoder.decode(rawApiKey, "UTF-8") else ""

                            ImageGenerationScreen(
                                onBack = {
                                    navController.popBackStack()
                                },
                                modelName = decodedModelName,
                                endpoint = decodedEndpoint,
                                apiKey = decodedApiKey
                            )
                        }
                    }
                }
            }
        }
    }
}