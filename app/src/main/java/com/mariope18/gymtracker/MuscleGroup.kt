package com.mariope18.gymtracker

enum class MuscleGroup(val displayName: String) {
    CHEST("Petto"),
    BACK("Schiena"),
    LEGS("Gambe"),
    SHOULDERS("Spalle"),
    ARMS("Braccia"),
    CORE("Addome");

    companion object {
        fun fromString(value: String): MuscleGroup? {
            return entries.find { it.displayName == value }
        }
    }
}
