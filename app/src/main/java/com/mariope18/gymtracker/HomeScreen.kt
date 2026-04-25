package com.mariope18.gymtracker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.mariope18.gymtracker.ui.theme.GymTrackerTheme

@Composable()
fun HomeScreen(modifier: Modifier = Modifier, onLogoutSuccess: () -> Unit = {}) {

    val auth = remember { Firebase.auth }
    val db = remember { Firebase.firestore }

    var userData by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val uid = currentUser.uid

            db.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        userData = document.getString("email")
                    }
                    isLoading = false
                }
                .addOnFailureListener { exception ->
                    isLoading = false
                }
        } else {
            isLoading = false
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isLoading) {
            CircularProgressIndicator();
            Spacer(modifier = Modifier.height(16.dp))
            Text("Recupero dati in corso...")
        } else {
            Text(
               text =  "Benvenuto, ${userData ?: "Utente Sconosciuto"}!",
                style = MaterialTheme.typography.headlineMedium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            auth.signOut()
            onLogoutSuccess()
        }) {
            Text("Logout")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    GymTrackerTheme {
        HomeScreen(onLogoutSuccess = {})
    }
}