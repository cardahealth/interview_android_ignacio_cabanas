package com.cardahealth.interviewapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.cardahealth.interviewapp.ui.detail.SensorDetailScreen
import com.cardahealth.interviewapp.ui.home.HomeScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object SensorDetail : Screen("sensor/{sensorId}") {
        const val ARG_SENSOR_ID = "sensorId"
        fun routeFor(sensorId: String): String = "sensor/$sensorId"
    }
}

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onSensorClick = { sensorId ->
                    navController.navigate(Screen.SensorDetail.routeFor(sensorId))
                },
            )
        }
        composable(
            route = Screen.SensorDetail.route,
            arguments = listOf(navArgument(Screen.SensorDetail.ARG_SENSOR_ID) { type = NavType.StringType }),
        ) { backStackEntry ->
            val sensorId = backStackEntry.arguments?.getString(Screen.SensorDetail.ARG_SENSOR_ID).orEmpty()
            SensorDetailScreen(sensorId = sensorId)
        }
    }
}