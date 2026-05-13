package com.mariope18.gymtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mariope18.gymtracker.ui.theme.GymTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GymTrackerTheme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost( navController = navController, startDestination = ("Login")) {
                        composable("Login") {
                            LoginScreen(modifier = Modifier.padding(innerPadding))
                            {
                                navController.navigate("Home")
                            }
                        }
                        composable("Home") {
                            HomeScreen(
                                modifier = Modifier.padding(innerPadding),
                                onLogoutSuccess =
                                    {
                                        navController.navigate("Login") {
                                            popUpTo("Home") { inclusive = true }
                                        }
                                    },
                                onWorkoutClick =
                                    {
                                        workoutId, workoutName ->
                                        navController.navigate("WorkoutDetail/$workoutId/$workoutName")
                                    }
                            )
                        }
                        composable("WorkoutDetail/{workoutId}/{workoutName}") { backStackEntry ->
                            val workoutId = backStackEntry.arguments?.getString("workoutId") ?: ""
                            val workoutName = backStackEntry.arguments?.getString("workoutName") ?: ""
                            WorkoutDetailScreen(
                                workoutId = workoutId,
                                workoutName = workoutName,
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}