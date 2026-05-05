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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
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

            composable("detail/{title}/{date}/{imageRes}") { backStackEntry ->
                val title = backStackEntry.arguments?.getString("title") ?: ""
                val date = backStackEntry.arguments?.getString("date") ?: ""
                val imageRes = backStackEntry.arguments?.getString("imageRes")?.toIntOrNull() ?: 0

                ExpeditionScreen(title, date, imageRes)
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
