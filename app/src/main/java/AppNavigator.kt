package com.example.projetkotlin

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.projetkotlin.ui.main.HomeScreen
import com.google.firebase.auth.FirebaseUser
import androidx.compose.runtime.*
import kotlinx.coroutines.delay
import com.example.projetkotlin.ui.splash.SplashScreen
import com.example.projetkotlin.AppNavigator
import com.example.projetkotlin.navigation.AuthNavigator



@ExperimentalMaterial3Api
@Composable
fun AppNavigator(user: FirebaseUser, onLogout: () -> Unit) {
    // Crée un contrôleur de navigation
    val navController = rememberNavController()
    val showSplash = remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(100) // 0.1 secondes d'attente
        showSplash.value = false
    }

    if (showSplash.value) {
        SplashScreen {
            showSplash.value = false
        }
    } else {
        // Le reste de ton app ici
    }

    // Définit la structure de la navigation
    NavHost(navController = navController, startDestination = "home") {

        // Composant de l'écran d'accueil
        composable("home") {
            HomeScreen(
                user = user,
                onLogout = onLogout,
                onCreateProfileClick = {
                    // Navigue vers la page Profil
                    navController.navigate("profil")
                }
            )
        }

        // Composant de la page Profil
        composable("profil") {
            ProfilScreen(
                onSave = { nom, prenom, sexe, taille, hobbie ->
                    navController.popBackStack() // Retour à Home
                },
                onBack = {
                    navController.popBackStack() // Bouton retour
                }
            )
        }
    }
}
