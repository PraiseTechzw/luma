package app.lumalabs.luma.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.lumalabs.luma.ui.screen.DashboardScreen
import app.lumalabs.luma.ui.screen.OnboardingScreen
import app.lumalabs.luma.ui.screen.SettingsScreen
import app.lumalabs.luma.ui.screen.SimilarPhotosScreen
import app.lumalabs.luma.ui.viewmodel.OnboardingViewModel

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Dashboard : Screen("dashboard")
    object SimilarPhotos : Screen("similar_photos")
    object Screenshots : Screen("screenshots")
    object ChatMedia : Screen("chat_media")
    object Settings : Screen("settings")
}

@Composable
fun LumaNavGraph() {
    val navController = rememberNavController()
    val onboardingViewModel: OnboardingViewModel = hiltViewModel()
    val onboardingCompleted by onboardingViewModel.onboardingCompleted.collectAsState()

    var startDestination by remember { mutableStateOf<String?>(null) }

    SideEffect {
        if (startDestination == null) {
            startDestination = if (onboardingCompleted) Screen.Dashboard.route else Screen.Onboarding.route
        }
    }

    if (startDestination != null) {
        NavHost(
            navController = navController,
            startDestination = startDestination!!
        ) {
            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    onComplete = {
                        onboardingViewModel.completeOnboarding()
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Dashboard.route) {
                DashboardScreen(navController = navController)
            }
            composable(Screen.SimilarPhotos.route) {
                SimilarPhotosScreen(navController = navController)
            }
            composable(Screen.Settings.route) {
                SettingsScreen(navController = navController)
            }
            // Additional routes for screenshots and chat media would be here
        }
    }
}
