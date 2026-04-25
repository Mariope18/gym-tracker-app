package com.mariope18.gymtracker

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

@Composable
fun LoginScreen(modifier: Modifier = Modifier) {

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoginMode by remember { mutableStateOf(true) }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        Text(
            text = if (isLoginMode) "Login" else "Registrazione",
            style = MaterialTheme.typography.headlineLarge
        )
        OutlinedTextField(
            value = username,
            onValueChange = {
                username = it
            },
            label = { Text("Username") }
        )
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
            },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )

        val context =
            LocalContext.current; // Serve per mostrare il Toast (il messaggino a comparsa)
        val auth = Firebase.auth; // L'istanza di Firebase!
        val db = Firebase.firestore; // L'istanza di Firebase!

        Button(onClick = {
            if (username.isBlank() || password.isBlank()) {
                Toast.makeText(context, "Inserisci email e password", Toast.LENGTH_SHORT).show()
                return@Button
            }

            if (password.length < 6) {
                Toast.makeText(
                    context,
                    "La password deve essere di almeno 6 caratteri",
                    Toast.LENGTH_SHORT
                ).show()
                return@Button
            }

            if (isLoginMode) {
                auth.signInWithEmailAndPassword(username, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                context,
                                "Login avvenuto con successo!",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Log.e("GymTrackerAuth", "Errore Login", task.exception)
                            Toast.makeText(
                                context,
                                "Errore Login: ${task.exception?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

            } else {
                // Fase 1: Creazione account su Firebase Auth
                auth.createUserWithEmailAndPassword(username, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // L'utente è stato creato! Prendiamo il suo ID segreto (UID)
                            val userId = task.result?.user?.uid
                            if (userId != null) {
                                // Fase 2: Prepariamo la scatola di dati per il Database NoSQL (Chiave -> Valore)
                                val profiloUtente = hashMapOf(
                                    "email" to username,
                                    "dataRegistrazione" to System.currentTimeMillis()
                                )
                                db.collection("users").document(userId)
                                    .set(profiloUtente)
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            context,
                                            "Registrazione avvenuta con successo!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e("GymTrackerAuth", "Errore salvataggio DB", e)
                                        Toast.makeText(
                                            context,
                                            "Errore salvataggio DB: ${e.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }
                        } else {
                            Log.e("GymTrackerAuth", "Errore Registrazione", task.exception)
                            Toast.makeText(
                                context,
                                "Errore Registrazione: ${task.exception?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }
        ) {
            Text(text = if (isLoginMode) "Login" else "Registrazione")
        }

        TextButton(onClick = { isLoginMode = !isLoginMode }) {
            Text(text = if (isLoginMode) "Non hai un account? Registrati" else "Hai già un account? Accedi")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen()
}