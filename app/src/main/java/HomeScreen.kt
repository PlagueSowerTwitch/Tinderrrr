package com.example.projetkotlin.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.projetkotlin.R
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import android.util.Log
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.ui.draw.clip
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf



private const val URL_RTDB =
    "https://projetkotlin-backend-default-rtdb.europe-west1.firebasedatabase.app"


fun ajouterLike(userId: String, likedUserId: String) {
    val database = FirebaseDatabase.getInstance(URL_RTDB)
    val userLikesRef = database.getReference("userDataPrivee").child(userId).child("likes")

    userLikesRef.get().addOnSuccessListener { snapshot ->
        val currentLikes = snapshot.getValue(object : GenericTypeIndicator<MutableList<String>>() {}) ?: mutableListOf()
        if (!currentLikes.contains(likedUserId)) {
            currentLikes.add(likedUserId)
            userLikesRef.setValue(currentLikes)
        }
    }.addOnFailureListener {
        Log.e("HomeScreen", "Erreur lors de la récupération des likes", it)
    }
}


data class UserInfo(
    val prenom: String? = null,
    val age: String? = null,
    val taille: String? = null,
    val sexe: String? = null,
    val hobbie: String? = null
)

@Composable
fun HomeScreen(
    user: FirebaseUser,
    onLogout: () -> Unit,
    onCreateProfileClick: () -> Unit
) {
    // Initialisation de l'état pour les informations de l'utilisateur
    val userInfo = remember { mutableStateOf(UserInfo()) }

    // Variable d'état pour les profils alternatifs
    val alternateUserInfo = remember { mutableStateOf(UserInfo()) }

    val database = FirebaseDatabase.getInstance(URL_RTDB)
    val userInfoRef = database.getReference("userDataPublic").child(user.uid).child("info")

    // Utilisation de LaunchedEffect pour charger les données de l'utilisateur
    LaunchedEffect(user.uid) {
        userInfoRef.get().addOnSuccessListener { snapshot ->
            val info = snapshot.getValue(UserInfo::class.java)
            if (info != null) {
                userInfo.value = info // Met à jour la valeur de userInfo avec les données de l'utilisateur connecté
            }
        }.addOnFailureListener {
            Log.e("HomeScreen", "Erreur lors de la récupération des infos utilisateur", it)
        }
    }

    // Fonction pour récupérer un profil aléatoire à afficher
    fun fetchNextProfile() {
        val userIdsRef = database.getReference("userDataPublic") // Référence à la liste des utilisateurs
        userIdsRef.get().addOnSuccessListener { snapshot ->
            // Récupère tous les IDs, filtre les nulls et assure que l'ID de l'utilisateur actuel est exclu
            val userIds = snapshot.children.map { it.key }.filterNotNull().filter { it != user.uid }
            if (userIds.isNotEmpty()) {
                val randomUserId = userIds.random() // Sélectionne un autre utilisateur au hasard

                // Récupère les informations de ce profil
                val nextUserInfoRef = userIdsRef.child(randomUserId).child("info")
                nextUserInfoRef.get().addOnSuccessListener { nextSnapshot ->
                    val nextInfo = nextSnapshot.getValue(UserInfo::class.java)
                    if (nextInfo != null) {
                        alternateUserInfo.value = nextInfo // Met à jour avec le profil suivant
                    }
                }.addOnFailureListener {
                    Log.e("HomeScreen", "Erreur lors de la récupération du profil suivant", it)
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 👤 Image et bouton Créer Profil
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            // 🔝 Créer Profil (en haut à gauche au-dessus de l'image)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Button(onClick = onCreateProfileClick) {
                    Text("Créer Profil")
                }
            }

            // 🖼️ Image de profil qui prend une grande place en haut
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(2f) // Augmente la taille de l'image
            ) {
                Image(
                    painter = painterResource(id = R.drawable.pod_international),
                    contentDescription = "Photo de profil",
                    modifier = Modifier
                        .fillMaxSize() // L'image prend toute la place
                        .clip(RoundedCornerShape(bottomEnd = 32.dp, bottomStart = 32.dp))
                )
            }
        }

        // 👤 Informations de l'utilisateur en dessous de l'image
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.BottomStart)
        ) {
            // Prénom de l'utilisateur actuel
            Text(
                text = "Prénom: ${userInfo.value.prenom ?: "Non disponible"}",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Âge (ou une approximation, si disponible dans les données)
            Text(
                text = "Âge: ${userInfo.value.age ?: "Non disponible"}",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Taille
            Text(
                text = "Taille: ${userInfo.value.taille ?: "Non disponible"}",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Sexe
            Text(
                text = "Sexe: ${userInfo.value.sexe ?: "Non disponible"}",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Hobbie
            Text(
                text = "Hobbies: ${userInfo.value.hobbie ?: "Non disponible"}",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        // 💖 Like / Dislike (en bas au-dessus du bouton de déconnexion)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 80.dp)
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Bouton Dislike
            IconButton(onClick = {
                // Appel de la fonction pour récupérer un autre profil
                fetchNextProfile()
                Log.d("HomeScreen", "Dislike cliqué, changement de profil")
            }) {
                Image(
                    painter = painterResource(id = R.drawable.logo_dislike),
                    contentDescription = "Dislike",
                    modifier = Modifier.size(60.dp)
                )
            }

            // Bouton Like
            IconButton(onClick = {
                ajouterLike(user.uid, user.uid) // Remplace fakeProfileId par l'ID réel si nécessaire
                Log.d("HomeScreen", "Like envoyé pour ${user.uid}")
            }) {
                Image(
                    painter = painterResource(id = R.drawable.logo_like),
                    contentDescription = "Like",
                    modifier = Modifier.size(60.dp)
                )
            }
        }

        // 🔚 Bouton Se déconnecter (toujours en bas-centre)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(onClick = onLogout) {
                Text("Se déconnecter")
            }
        }
    }
}