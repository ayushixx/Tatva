# Tatva: Offline Emergency Response AI 🛡️🛰️

Tatva is a high-performance, **completely offline** emergency response application designed for situations where cellular networks and internet connectivity are unavailable. Powered by **Google's Gemma LLM**, it provides real-time medical guidance, victim detection via Bluetooth radar, and vital signs monitoring.

## 🚀 Key Features

*   **Gemma AI Chatbot:** A fully offline, on-device AI assistant that provides medical guidance and general first aid instructions using streaming responses.
*   **Rescue Radar:** Scans for "Victim Signals" using Bluetooth Low Energy (BLE) to locate nearby individuals in distress without needing GPS or internet.
*   **Vitals Monitor:** Real-time patient monitoring including a live ECG graph, SpO2 tracking, and body temperature simulation with AI assessment.
*   **Emergency SOS:** High-visibility pulsing SOS signal that broadcasts your location to nearby rescuers and provides quick-dial emergency contacts.
*   **Medical Guides:** Curated, interactive instructions for common emergencies like burns, bleeding, fractures, and CPR.

## 🛠️ Tech Stack

*   **Language:** Kotlin
*   **UI:** Jetpack Compose (Modern, Lively UI/UX)
*   **AI Engine:** Google LiteRT-LM (Gemma 2b Model)
*   **Connectivity:** Bluetooth LE (Scanner & Advertiser)
*   **Architecture:** Clean Architecture with Coroutines & Flow

---

## 📦 Installation & Setup

### 1. Prerequisites
*   Android Studio Ladybug or newer.
*   Physical Android Device (Bluetooth features require a real device).
*   Minimum SDK: API 29 (Android 10).

### 2. The Gemma Model (Crucial Step) 🤖
To keep the repository lightweight, the **2GB Gemma model file** is not included in the Git history. You must add it manually to run the AI:

1.  Download the `gemma.litertlm` model file (Gemma 2b-it-cpu-int4).
2.  Place the file in: `app/src/main/assets/gemma.litertlm`
3.  Ensure the filename is exactly `gemma.litertlm`.

### 3. Build & Run
1.  Clone the repository.
    
