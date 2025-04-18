package com.example.projetkotlin.ui.main

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.tasks.await

data class Profile(val uid: String, val name: String, val photoUrl: String)

@Composable
fun TinderScreenFirebase() {
    val database = FirebaseDatabase.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser

    var profiles by remember { mutableStateOf(listOf<Profile>()) }
    var currentIndex by remember { mutableStateOf(0) }

    // Charger les profils une fois
    LaunchedEffect(Unit) {
        val ref = database.getReference("profiles")
        ref.get().addOnSuccessListener { snapshot ->
            val list = mutableListOf<Profile>()
            snapshot.children.forEach { child ->
                val uid = child.key ?: return@forEach
                val name = child.child("name").getValue(String::class.java) ?: ""
                val photo = child.child("photoUrl").getValue(String::class.java) ?: ""
                if (uid != currentUser?.uid) {
                    list.add(Profile(uid, name, photo))
                }
            }
            profiles = list
        }
    }

    if (currentIndex >= profiles.size) {
        Text("Plus de profils !")
        return
    }

    val profile = profiles[currentIndex]

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text(profile.name, style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Image(
            painter = rememberAsyncImagePainter(profile.photoUrl),
            contentDescription = "Photo",
            modifier = Modifier.size(300.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    enregistrerSwipe(currentUser!!.uid, profile.uid, "dislike")
                    currentIndex++
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Refuser")
            }

            Button(
                onClick = {
                    enregistrerSwipe(currentUser!!.uid, profile.uid, "like")
                    currentIndex++
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Valider")
            }
        }
    }
}

fun enregistrerSwipe(userId: String, targetId: String, action: String) {
    val ref = FirebaseDatabase.getInstance()
        .getReference("swipes")
        .child(userId)
        .child(targetId)
    ref.setValue(action)
}
