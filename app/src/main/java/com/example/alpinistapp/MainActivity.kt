package com.example.alpinistapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.alpinistapp.ui.theme.AlpinistAppTheme
import com.mapbox.common.TileStore
import com.mapbox.bindgen.Value

import com.example.alpinistapp.components.ExpandableFab
import com.example.alpinistapp.screen.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val tileStore = TileStore.create()
        val capacidadBytes = 200L * 1024L * 1024L
        tileStore.setOption("disk-capacity", Value.valueOf(capacidadBytes))

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
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val isLoggedIn by sessionManager.isLoggedIn.collectAsState(initial = null)

    if (isLoggedIn == null) return

    Scaffold{ innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (isLoggedIn == true) "home" else "login",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("login") { LoginScreen(navController) }
            composable("register") { RegisterScreen(navController) }
            composable("home") { HomeScreen(navController) }

            composable(
                route = "search?query={query}&type={type}",
                arguments = listOf(
                    navArgument("query") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    },
                    navArgument("type") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = "Trails"
                    }
                )
            ) { backStackEntry ->
                val query = backStackEntry.arguments?.getString("query")
                val typeStr = backStackEntry.arguments?.getString("type") ?: "Trails"
                val type = try { SearchType.valueOf(typeStr) } catch(e: Exception) { SearchType.Trails }

                SearchScreen(
                    navController = navController,
                    initialQuery = query,
                    initialType = type
                )
            }

            composable("profile") { ProfileScreen(navController) }
            composable("settings") { SettingsScreen(navController) }
            composable("expedition") { ActiveExpedition(navController) }

            composable(
                route = "trail_detail/{trailId}",
                arguments = listOf(
                    navArgument("trailId") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val trailId = backStackEntry.arguments?.getInt("trailId") ?: 0
                TrailScreen(
                    trailId = trailId,
                    navController = navController
                )
            }

            composable(
                route = "expedition_detail/{expeditionId}/{trailId}/{date}",
                arguments = listOf(
                    navArgument("expeditionId") { type = NavType.IntType },
                    navArgument("trailId") { type = NavType.IntType },
                    navArgument("date") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val expeditionId = backStackEntry.arguments?.getInt("expeditionId") ?: 0
                val trailId = backStackEntry.arguments?.getInt("trailId") ?: 0
                val date = backStackEntry.arguments?.getString("date") ?: ""

                ExpeditionScreen(
                    expeditionId = expeditionId,
                    trailId = trailId,
                    date = date,
                    navController = navController
                )
            }
        }
    }
    if (currentRouteName?.startsWith("login") == false &&
        currentRouteName?.startsWith("register") == false &&
        currentRouteName != null) {
        ExpandableFab(navController)
    }

}

@Composable
fun currentRoute(navController: NavController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}
