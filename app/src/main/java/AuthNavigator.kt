package com.example.projetkotlin.navigation

import android.util.Log
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import com.example.projetkotlin.auth.AuthManager
import com.example.projetkotlin.ui.auth.AuthScreen
import com.example.projetkotlin.ui.main.HomeScreen
import com.google.firebase.auth.FirebaseAuth
import com.example.projetkotlin.AppNavigator

@ExperimentalMaterial3Api
@Composable
fun AuthNavigator() {
    val auth = AuthManager.auth
    var currentUser by remember { mutableStateOf(AuthManager.getCurrentUser()) }

    DisposableEffect(auth) {
        val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            currentUser = firebaseAuth.currentUser
            Log.d("AuthNavigator", "Auth state changed, user: ${currentUser?.uid}")
        }

        auth.addAuthStateListener(authStateListener)

        onDispose {
            auth.removeAuthStateListener(authStateListener)
        }
    }

    if (currentUser == null) {
        AuthScreen(
            auth = auth,
            onAuthComplete = { user ->
                Log.d("AuthNavigator", "Auth complete for: ${user.uid}")
            }
        )
    } else {
        AppNavigator(currentUser!!, onLogout = {
            Log.d("AuthNavigator", "Logout requested")
            auth.signOut()
        })
    }
}
