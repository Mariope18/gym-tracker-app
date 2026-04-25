package com.mariope18.gymtracker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.mariope18.gymtracker.ui.theme.GymTrackerTheme

@Composable()
fun HomeScreen(modifier: Modifier = Modifier, onLogoutSuccess : () -> Unit = {}) {

    val auth = Firebase.auth

    Column( modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Benvenuto")

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