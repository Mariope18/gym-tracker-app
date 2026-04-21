package com.mariope18.gymtracker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun LoginScreen(modifier: Modifier = Modifier) {
    
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column (
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            text = "Login",
            style = MaterialTheme.typography.headlineLarge
        )
        OutlinedTextField(
            value = username,
            onValueChange = { username = it
            },
            label = { Text("Username") }
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it
            },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )
        Button(onClick = { /* Gestisci il login */ }) {
            Text("Login")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen()
}