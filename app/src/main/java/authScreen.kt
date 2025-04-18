package com.example.projetkotlin.ui.auth

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(auth: FirebaseAuth, onAuthComplete: (FirebaseUser) -> Unit) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Bienvenue !", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Mot de passe") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Connexion
                Button(onClick = {
                    if (email.isBlank() || password.isBlank()) {
                        errorMessage = "Email et mot de passe requis."
                        return@Button
                    }
                    if (!isEmailValid(email)) {
                        errorMessage = "Format d'email invalide."
                        return@Button
                    }
                    isLoading = true
                    errorMessage = null
                    coroutineScope.launch {
                        try {
                            val result = auth.signInWithEmailAndPassword(email, password).await()
                            Log.d("AuthScreen", "Login Success: ${result.user?.email}")
                            onAuthComplete(result.user!!)
                        } catch (e: Exception) {
                            Log.w("AuthScreen", "Login Failed", e)
                            errorMessage = getAuthErrorMessage(e)
                        } finally {
                            isLoading = false
                        }
                    }
                }) { Text("Connexion") }

                // Inscription
                Button(onClick = {
                    if (email.isBlank() || password.isBlank()) {
                        errorMessage = "Email et mot de passe requis."
                        return@Button
                    }
                    if (!isEmailValid(email)) {
                        errorMessage = "Format d'email invalide."
                        return@Button
                    }
                    isLoading = true
                    errorMessage = null
                    coroutineScope.launch {
                        try {
                            val result = auth.createUserWithEmailAndPassword(email, password).await()
                            Log.d("AuthScreen", "Sign Up Success: ${result.user?.email}")
                            onAuthComplete(result.user!!)
                        } catch (e: Exception) {
                            Log.w("AuthScreen", "Sign Up Failed", e)
                            errorMessage = getAuthErrorMessage(e)
                        } finally {
                            isLoading = false
                        }
                    }
                }) { Text("Inscription") }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// --- Fonctions utilitaires ---

fun isEmailValid(email: String): Boolean {
    val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")
    return emailRegex.matches(email)
}

fun getAuthErrorMessage(e: Exception): String {
    return when (e) {
        is FirebaseAuthInvalidCredentialsException -> "Email ou mot de passe incorrect."
        is FirebaseAuthUserCollisionException -> "Cet email est déjà utilisé."
        is FirebaseAuthWeakPasswordException -> "Mot de passe trop faible (6 caractères min)."
        is FirebaseAuthInvalidUserException -> "Utilisateur introuvable. Vérifiez votre email."
        else -> e.localizedMessage ?: "Erreur inconnue lors de l'authentification."
    }
}
