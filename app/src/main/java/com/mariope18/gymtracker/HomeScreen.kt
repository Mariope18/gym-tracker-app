package com.mariope18.gymtracker

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

@Composable()
fun HomeScreen(
    modifier: Modifier = Modifier,
    onLogoutSuccess: () -> Unit = {},
    onWorkoutClick: (String, String) -> Unit = {_, _ ->}
    ) {

    val auth = remember { Firebase.auth }
    val db = remember { Firebase.firestore }

    var userData by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    var showDialog by remember { mutableStateOf(false) }
    var newWorkoutName by remember { mutableStateOf("") }

    var workoutsList by remember { mutableStateOf(emptyList<Workout>()) }

    LaunchedEffect(Unit) {
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val uid = currentUser.uid

            db.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        userData = document.getString("email")

                        db.collection("users").document(uid)
                            .collection("workouts")
                            .addSnapshotListener { snapshot, error ->
                                if ( error == null && snapshot != null) {
                                    workoutsList = snapshot.documents.map { doc ->
                                        Workout(doc.id, doc.getString("name") ?: "")
                                    }
                                }
                            }
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

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true }
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Aggiungi")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isLoading) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text("Recupero dati in corso...")
            } else {
                Text(
                    text = "Benvenuto, ${userData ?: "Utente Sconosciuto"}!",
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            LazyColumn( modifier = Modifier.weight(1f)) {
                items(workoutsList) { workout ->
                    Card(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                            .clickable { onWorkoutClick(workout.id, workout.name) }
                    ) {
                        Text(
                            text = workout.name,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                auth.signOut()
                onLogoutSuccess()
            }) {
                Text("Logout")
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = {
                    showDialog = false
                },
                title = {
                    Text("Nuovo Allenamento")
                },
                text = {
                    Column {
                        Text("Inserisci il nome della nuova scheda:")
                        Spacer(modifier = Modifier.height(8.dp))
                        TextField(
                            value = newWorkoutName,
                            onValueChange = { newWorkoutName = it },
                            label = { Text("Nome") }
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        if (newWorkoutName.isNotBlank()) {
                            val currentUser = auth.currentUser
                            if (currentUser != null) {
                                val uid = currentUser.uid
                                val workoutData = hashMapOf(
                                    "name" to newWorkoutName,
                                    "createdAt" to System.currentTimeMillis()
                                )
                                db.collection("users").document(uid)
                                    .collection("workouts")
                                    .add(workoutData)
                                    .addOnSuccessListener {
                                        showDialog = false
                                        newWorkoutName = ""
                                    }
                                    .addOnFailureListener { exception ->
                                        showDialog = false
                                        newWorkoutName = ""
                                    }
                            }
                        }
                    }) {
                        Text("Conferma")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Annulla")
                    }
                }
            )
        }
    }
}

data class Workout(val id: String, val name: String)
