package com.example.alpinistapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.alpinistapp.ui.theme.AlpinistAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AlpinistAppTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val currentRouteName = currentRoute(navController)

    Scaffold(
        // Movemos el FAB al slot oficial del Scaffold para un posicionamiento perfecto
        floatingActionButton = {
            if (currentRouteName != "login" && currentRouteName != "register") {
                ExpandableFab(navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("login") { LoginScreen(navController) }
            composable("register") { RegisterScreen(navController) }
            composable("home") { HomeScreen(navController) }
            composable("search") { SearchTrailsScreen(navController) }
            composable("profile") { ProfileScreen(navController) }

            // Ruta de detalles de Senderos (Nube - Neon / Render)
            composable(
                route = "detail/{routeTitle}/{location}/{imageUrl}/{difficulty}/{rating}",
                arguments = listOf(
                    navArgument("routeTitle") { type = NavType.StringType },
                    navArgument("location") { type = NavType.StringType },
                    navArgument("imageUrl") { type = NavType.StringType },
                    navArgument("difficulty") { type = NavType.StringType },
                    navArgument("rating") { type = NavType.FloatType }
                )
            ) { backStackEntry ->
                val routeTitle = backStackEntry.arguments?.getString("routeTitle") ?: ""
                val location = backStackEntry.arguments?.getString("location") ?: ""
                val imageUrl = backStackEntry.arguments?.getString("imageUrl") ?: ""
                val difficulty = backStackEntry.arguments?.getString("difficulty") ?: ""
                val rating = backStackEntry.arguments?.getFloat("rating")?.toDouble() ?: 0.0

                TrailScreen(
                    routeTitle = routeTitle,
                    location = location,
                    imageUrl = imageUrl,
                    difficulty = difficulty,
                    rating = rating,
                    navController = navController
                )
            }

            composable(
                route = "expedition_detail/{title}/{date}/{imageUrl}?routeTitle={routeTitle}&location={location}&trailImage={trailImage}&rating={rating}",
                arguments = listOf(
                    navArgument("title") { type = NavType.StringType },
                    navArgument("date") { type = NavType.StringType },
                    navArgument("imageUrl") { type = NavType.StringType },
                    navArgument("routeTitle") { type = NavType.StringType; defaultValue = "" },
                    navArgument("location") { type = NavType.StringType; defaultValue = "" },
                    navArgument("trailImage") { type = NavType.StringType; defaultValue = "" },
                    navArgument("rating") { type = NavType.FloatType; defaultValue = 4.0f }
                )
            ) { backStackEntry ->
                val title = backStackEntry.arguments?.getString("title") ?: ""
                val date = backStackEntry.arguments?.getString("date") ?: ""
                val imageUrl = backStackEntry.arguments?.getString("imageUrl") ?: ""

                // Parámetros dinámicos del Trail mapeados desde la estructura de tu BD
                val routeTitle = backStackEntry.arguments?.getString("routeTitle") ?: title
                val location = backStackEntry.arguments?.getString("location") ?: "Ubicación"
                val trailImage = backStackEntry.arguments?.getString("trailImage") ?: imageUrl
                val rating = backStackEntry.arguments?.getFloat("rating")?.toDouble() ?: 4.0

                ExpeditionScreen(
                    title = title,
                    date = date,
                    imageUrl = imageUrl,
                    routeTitle = routeTitle,   // Mapeado de 'route'
                    location = location,       // Mapeado de 'location'
                    trailImage = trailImage,   // Mapeado de 'image'
                    rating = rating,           // Mapeado de 'rating'
                    navController = navController
                )
            }
        }
    }
}

@Composable
fun currentRoute(navController: NavController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}