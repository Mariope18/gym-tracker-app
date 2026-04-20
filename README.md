# 🏋️ Gym Tracker App - Product Requirements Document (PRD)

## 📌 Visione del Prodotto
Gym Tracker è un'applicazione mobile Android nativa progettata per atleti e frequentatori di palestre. Funge da diario di allenamento digitale completo, permettendo agli utenti di consultare un catalogo di esercizi, strutturare schede di allenamento multi-giorno e tracciare le performance dal vivo (serie, ripetizioni, carico) durante le sessioni. L'app è progettata con un approccio "Offline-First", garantendo l'utilizzo in palestra anche in assenza di rete, con sincronizzazione cloud trasparente.

## 🛠 Stack Tecnologico (Modern Android Standard)
* **Linguaggio:** Kotlin
* **UI Framework:** Jetpack Compose (UI Dichiarativa)
* **Architettura:** MVVM (Model-View-ViewModel) integrata con Clean Architecture.
* **Local Data (Offline):** Room Database (SQLite abstraction).
* **Cloud & Backend (BaaS):** Firebase Firestore (NoSQL Database), Firebase Authentication.
* **Asincronia:** Kotlin Coroutines & Flows.

## 🎯 Core Features (MVP)

### 1. Autenticazione e Profilo Utente
* Registrazione e login sicuri tramite Email e Password (gestiti tramite Firebase Auth).
* Gestione della sessione utente persistente.

### 2. Catalogo Esercizi (Exercise Dictionary)
* Libreria globale consultabile contenente esercizi standard (es. Panca Piana, Squat, Stacco).
* Ogni esercizio presenta una scheda di dettaglio contenente:
    * Nome e Gruppo Muscolare target.
    * Descrizione testuale dell'esecuzione corretta.
    * Link o riferimento multimediale (video tutorial) per l'esecuzione.

### 3. Gestione Schede di Allenamento (Routines)
* Creazione di schede personalizzate (es. "Scheda Ipertrofia Invernale").
* Strutturazione **Multi-Giorno**: ogni scheda può essere suddivisa in più giornate (es. "Giorno 1: Spinta", "Giorno 2: Trazione").
* Assegnazione degli esercizi a specifiche giornate, impostando target precisi per l'utente (es. 3 Serie da 10 Ripetizioni).

### 4. Live Workout Tracking (Tracciamento Sessione)
* Avvio di una sessione di allenamento basata su un "Giorno" specifico di una "Scheda".
* Interfaccia rapida per l'inserimento dei dati reali durante il recupero: registrazione di Serie effettive, Ripetizioni completate e Peso sollevato.
* Chiusura e salvataggio della sessione nello storico personale.

### 5. Sincronizzazione Offline-First
* L'utente deve poter iniziare, tracciare e salvare un allenamento anche senza connessione internet (es. palestre interrate).
* I dati vengono salvati primariamente sul database locale (Room).
* Sincronizzazione in background verso Firebase Firestore non appena la connettività viene ripristinata.

## 🗄️ Architettura Dati (Modello NoSQL Documentale)
Per garantire massime performance in lettura su client mobile, il database cloud (Firestore) adotta una struttura denormalizzata e innestata (nested data).
I dati non sono divisi in tabelle relazionali, ma ottimizzati in documenti JSON per l'interfaccia utente:
* I target degli esercizi sono innestati direttamente all'interno delle Routine dell'utente.
* I log di allenamento (set/reps/peso) sono salvati direttamente all'interno del documento della Sessione completata.
* Il Catalogo Esercizi funge da singola fonte di verità (Single Source of Truth) in sola lettura per popolare l'app.