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

    Scaffold { innerPadding ->
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
            composable("expedition") { ActiveExpedition(navController) }


            // Trail details route
            composable(
                route = "detail/{title}/{location}/{imageRes}",
                arguments = listOf(
                    navArgument("title") { type = NavType.StringType },
                    navArgument("location") { type = NavType.StringType },
                    navArgument("imageRes") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val title = backStackEntry.arguments?.getString("title") ?: ""
                val location = backStackEntry.arguments?.getString("location") ?: ""
                val imageRes = backStackEntry.arguments?.getInt("imageRes") ?: 0

                TrailScreen(title, location, imageRes, navController)
            }

            // Expedition details route
            composable(
                route = "expedition_detail/{title}/{date}/{imageRes}",
                arguments = listOf(
                    navArgument("title") { type = NavType.StringType },
                    navArgument("date") { type = NavType.StringType },
                    navArgument("imageRes") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val title = backStackEntry.arguments?.getString("title") ?: ""
                val date = backStackEntry.arguments?.getString("date") ?: ""
                val imageRes = backStackEntry.arguments?.getInt("imageRes") ?: 0

                ExpeditionScreen(title, date, imageRes, navController)
            }
        }

        if (currentRouteName != "login" && currentRouteName != "register") {
            ExpandableFab(navController)
        }
    }
}

@Composable
fun currentRoute(navController: NavController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}
