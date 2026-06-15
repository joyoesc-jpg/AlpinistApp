package com.example.alpinistapp

import android.os.Bundle
import android.util.Log
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
import com.mapbox.common.TileStore
import com.mapbox.bindgen.Value

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //MapboxOptions.accessToken = ""

        val tileStore = TileStore.create()

        val capacidadBytes = 200L * 1024L * 1024L

        tileStore.setOption(
            "disk-capacity",
            Value(capacidadBytes)
        )

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

    val currentContext = LocalContext.current
    val scope = rememberCoroutineScope()

    val userPreferences = remember { UserPreferences(currentContext) }
    val loggedInState = userPreferences.isLoggedIn.collectAsState(initial = false)

    Scaffold{ innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (loggedInState.value) "home" else "login",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("login") { LoginScreen(navController) }
            composable("register") { RegisterScreen(navController) }
            composable("home") { HomeScreen(navController) }
            composable("search") { SearchTrailsScreen(navController) }
            composable("profile") { ProfileScreen(navController) }
            composable("settings") { SettingsScreen(navController) }
            composable("expedition") { ActiveExpedition(navController) }

            composable(
                route = "detail/{trailId}/{routeTitle}/{location}/{imageUrl}/{difficulty}/{rating}",
                arguments = listOf(
                    navArgument("trailId") { type = NavType.IntType },
                    navArgument("routeTitle") { type = NavType.StringType },
                    navArgument("location") { type = NavType.StringType },
                    navArgument("imageUrl") { type = NavType.StringType },
                    navArgument("difficulty") { type = NavType.StringType },
                    navArgument("rating") { type = NavType.FloatType }
                )
            ) { backStackEntry ->
                val trailId = backStackEntry.arguments?.getInt("trailId")
                Log.d("DEBUG_RUTAS", "ID extraído del NavHost: $trailId")
                    ?: throw IllegalStateException("¡Error: El trailId es obligatorio y no fue proporcionado en la navegación!")
                val routeTitle = backStackEntry.arguments?.getString("routeTitle") ?: ""
                val location = backStackEntry.arguments?.getString("location") ?: ""
                val imageUrl = backStackEntry.arguments?.getString("imageUrl") ?: ""
                val rating = backStackEntry.arguments?.getFloat("rating")?.toDouble() ?: 0.0

                TrailScreen(
                    trailId = trailId,
                    routeTitle = routeTitle,
                    location = location,
                    imageUrl = imageUrl,
                    navController = navController
                )
            }

            composable(

                route = "expedition_detail/{title}/{date}/{imageUrl}?routeTitle={routeTitle}&location={location}&trailImage={trailImage}&rating={rating}&trailId={trailId}",
                arguments = listOf(
                    navArgument("title") { type = NavType.StringType },
                    navArgument("date") { type = NavType.StringType },
                    navArgument("imageUrl") { type = NavType.StringType },
                    navArgument("routeTitle") { type = NavType.StringType; defaultValue = "" },
                    navArgument("location") { type = NavType.StringType; defaultValue = "" },
                    navArgument("trailImage") { type = NavType.StringType; defaultValue = "" },
                    navArgument("rating") { type = NavType.FloatType; defaultValue = 4.0f },
                    navArgument("trailId") { type = NavType.IntType;}
                )
            ) { backStackEntry ->
                val title = backStackEntry.arguments?.getString("title") ?: ""
                val date = backStackEntry.arguments?.getString("date") ?: ""
                val imageUrl = backStackEntry.arguments?.getString("imageUrl") ?: ""

                val routeTitle = backStackEntry.arguments?.getString("routeTitle") ?: title
                val location = backStackEntry.arguments?.getString("location") ?: "Ubicación"
                val trailImage = backStackEntry.arguments?.getString("trailImage") ?: imageUrl
                val rating = backStackEntry.arguments?.getFloat("rating")?.toDouble() ?: 4.0
                val trailId = backStackEntry.arguments?.getInt("trailId")

                ExpeditionScreen(
                    title = title,
                    date = date,
                    imageUrl = imageUrl,
                    routeTitle = routeTitle,
                    location = location,
                    trailImage = trailImage,
                    rating = rating,
                    trailId = trailId,
                    navController = navController
                )
            }
        }
    }
    if (currentRouteName != "login" && currentRouteName != "register") {
        ExpandableFab(navController)
    }

}

@Composable
fun currentRoute(navController: NavController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}
