package com.example.projetkotlin

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.draw.clip


private const val URL_RTDB = "https://projetkotlin-backend-default-rtdb.europe-west1.firebasedatabase.app"

@ExperimentalMaterial3Api
@Composable
fun ProfilScreen(
    onSave: (String, String, String, String, String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    var nom by remember { mutableStateOf("") }
    var prenom by remember { mutableStateOf("") }
    var sexe by remember { mutableStateOf("") }
    var taille by remember { mutableStateOf("") }
    var hobbie by remember { mutableStateOf("") }

    val user = FirebaseAuth.getInstance().currentUser
    val userId = user?.uid

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFC0CB)) // gris/rose très clair en fond
            .padding(16.dp)
    ) {
        // 🔙 Bouton retour en haut à droite
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .align(Alignment.TopEnd),
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5E5E)) // Rouge Tinder
            ) {
                Text("Retour", color = Color.White)
            }
        }

        // 📝 Carte formulaire au centre avec fond blanc
        Card(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.95f),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Créer ton profil",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color(0xFFFF5E5E) // Rouge Tinder
                )

                // Affichage de l'image (placeholder pod_international.jpg)
                Image(
                    painter = painterResource(id = R.drawable.pod_international), // Remplace par ton image
                    contentDescription = "Image de profil",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(2.dp, Color.Gray, RoundedCornerShape(8.dp))
                        .padding(8.dp)
                )

                TextField(
                    value = nom,
                    onValueChange = { nom = it },
                    label = { Text("Nom") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = prenom,
                    onValueChange = { prenom = it },
                    label = { Text("Prénom") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = sexe,
                    onValueChange = { sexe = it },
                    label = { Text("Sexe (Homme/Femme/Autres)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = taille,
                    onValueChange = { taille = it },
                    label = { Text("Taille (en cm)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = hobbie,
                    onValueChange = { hobbie = it },
                    label = { Text("Hobbie") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (nom.isBlank() || prenom.isBlank() || sexe.isBlank() || taille.isBlank() || hobbie.isBlank()) {
                            Toast.makeText(context, "Tous les champs doivent être remplis.", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        if (userId != null) {
                            val userDataRef = FirebaseDatabase.getInstance(URL_RTDB)
                                .getReference("userDataPublic")
                                .child(userId)
                                .child("info")

                            val userInfo = mapOf(
                                "nom" to nom,
                                "prenom" to prenom,
                                "sexe" to sexe,
                                "taille" to taille,
                                "hobbie" to hobbie,
                                "image" to "pod_international.jpg" // Ajout de l'image placeholder
                            )

                            Log.d("ProfilScreen", "Ceci est un log de vérif des userInfos $userInfo")

                            userDataRef.setValue(userInfo)
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Profil enregistré avec succès.", Toast.LENGTH_SHORT).show()
                                    onSave(nom, prenom, sexe, taille, hobbie)
                                }
                                .addOnFailureListener {
                                    Toast.makeText(context, "Erreur lors de l'enregistrement.", Toast.LENGTH_SHORT).show()
                                }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5E5E)) // Rouge
                ) {
                    Text("Enregistrer", color = Color.White)
                }
            }
        }
    }
}
