package com.mariope18.gymtracker

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

@Composable
fun WorkoutDetailScreen(
    workoutId: String, workoutName: String,
    selectedExerciseName: String = "",
    onNavigateBack: () -> Unit,
    onOpenCatalog: () -> Unit,
    onExerciseConsumed: () -> Unit) {

    val auth = remember { Firebase.auth }
    val db = remember { Firebase.firestore }

    var showDialog by remember { mutableStateOf(false) }
    var exerciseName by remember { mutableStateOf("") }
    var sets by remember { mutableStateOf("") } // Serie
    var reps by remember { mutableStateOf("") } // Ripetizioni
    var dayNumber by remember { mutableStateOf("1") } // Giorno

    LaunchedEffect(selectedExerciseName) {
        if (selectedExerciseName.isNotEmpty()) {
            exerciseName = selectedExerciseName
            showDialog = true
            onExerciseConsumed()
        }
    }

    val exerciseList = remember { mutableStateListOf<Exercise>() }

    LaunchedEffect(workoutId) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("users").document(userId)
                .collection("workouts").document(workoutId)
                .collection("exercises")
                .addSnapshotListener { snapshot, _ ->
                    exerciseList.clear()
                    snapshot?.documents?.forEach { doc ->
                        exerciseList.add(
                            Exercise(
                                doc.id,
                                doc.getString("name") ?: "",
                                (doc.getLong("sets") ?: 0).toInt(),
                                (doc.getLong("reps") ?: 0).toInt(),
                                (doc.getLong("dayNumber") ?: 1L).toInt()
                            )
                        )
                    }
                }
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true }
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Aggiungi esercizio")
            }
        }
    ) { innerPadding ->
        if (showDialog) {
            AlertDialog(
                onDismissRequest = {
                    exerciseName = ""
                    sets = ""
                    reps = ""
                    showDialog = false
                },
                title = { Text(text = "Aggiungi esercizio") },
                text = {
                    Column() {
                        OutlinedTextField(
                            value = exerciseName,
                            onValueChange = { exerciseName = it },
                            label = { Text("Nome esercizio") },
                            trailingIcon = {
                                Button(onClick = {
                                    showDialog = false
                                    onOpenCatalog()
                                }) { Text("Cerca") }
                            })
                        OutlinedTextField(
                            value = sets,
                            onValueChange = { sets = it },
                            label = { Text("Serie") })
                        OutlinedTextField(
                            value = reps,
                            onValueChange = { reps = it },
                            label = { Text("Ripetizioni") })
                        OutlinedTextField(
                            value = dayNumber,
                            onValueChange = { dayNumber = it },
                            label = { Text("Giorno") })
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        val exercise = hashMapOf(
                            "name" to exerciseName,
                            "sets" to (sets.toIntOrNull() ?: 0),
                            "reps" to (reps.toIntOrNull() ?: 0),
                            "dayNumber" to (dayNumber.toIntOrNull() ?: 1)
                        )
                        val userId = auth.currentUser?.uid
                        if (userId != null) {
                            db.collection("users").document(userId)
                                .collection("workouts").document(workoutId)
                                .collection("exercises")
                                .add(exercise)
                                .addOnSuccessListener {
                                    exerciseName = ""
                                    sets = ""
                                    reps = ""
                                    dayNumber = "1"
                                    showDialog = false
                                }
                                .addOnFailureListener {
                                    showDialog = false
                                }
                        }
                    }) {
                        Text("Salva")
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        exerciseName = ""
                        sets = ""
                        reps = ""
                        showDialog = false
                    }) {
                        Text("Annulla")
                    }
                }
            )
        }
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Text(text = workoutName)

            val groupedExercises = exerciseList.groupBy { it.dayNumber }
            val availableDays = groupedExercises.keys.sorted()

            var selectedDay by remember { mutableIntStateOf(1) }

            val selectedTabIndex = availableDays.indexOf(selectedDay)
            if (availableDays.isNotEmpty() && selectedTabIndex != -1) {
                ScrollableTabRow(selectedTabIndex) {
                    availableDays.forEach { day ->
                        Tab(
                            selected = (selectedDay == day),
                            onClick = { selectedDay = day }
                        ) {
                            Text(
                                text = "Giorno $day",
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                val exercisesToShow = groupedExercises[selectedDay] ?: emptyList()
                items(exercisesToShow) { exercise ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = exercise.name,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "${exercise.sets} serie x ${exercise.reps} rip",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            Button(onClick = onNavigateBack) {
                Text(text = "Indietro")
            }
        }
    }
}

data class Exercise(
    val id: String,
    val name: String,
    val sets: Int,
    val reps: Int,
    val dayNumber: Int
)
