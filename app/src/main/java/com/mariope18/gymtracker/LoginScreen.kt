package com.mariope18.gymtracker

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

@Composable
fun LoginScreen(modifier: Modifier = Modifier) {

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        Text(
            text = "Login",
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

        Button(onClick = {
            // Diciamo a Firebase di creare un utente con l'email e password inserite!
            auth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        //Successo!
                        Toast.makeText(context, "Login effettuato", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Errore: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
        ) {
            Text("Registrati / Login")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen()
}