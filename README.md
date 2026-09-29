# Cross-Match Tracker

**Cross-Match Tracker** is a modern, reliable Android application engineered for blood bank management, inventory monitoring, donor record tracking, expiration warnings, and real-time rare blood type cross-matching compatibility.

---

## Key Features

- **Blood Inventory Management**: Track blood units with RFID/barcode identifiers, component types (Whole Blood, Red Blood Cells, Platelets, Fresh Frozen Plasma, Cryoprecipitate), volume, storage temperatures, and expiration dates.
- **Cross-Match & Compatibility Engine**: Instant compatibility calculation between donor and recipient blood types, Rh factors, and rare phenotypes (Bombay phenotype, Duffy-null, Kell-null, etc.).
- **Expiration & Critical Alerts**: Live alerts for approaching expiration dates, low unit reserves, and storage temperature breaches.
- **Offline & Cloud Sync (Supabase & Room)**: Robust local persistence via Android Jetpack Room with remote sync capability to Supabase backend.
- **Material 3 Interface**: Clean, accessible, medical-grade UI built entirely in Jetpack Compose with edge-to-edge support and dynamic themes.

---

## Tech Stack

- **UI**: 100% Jetpack Compose with Material Design 3
- **Language**: Kotlin 2.0+
- **Architecture**: MVVM with Repository Pattern, Kotlin Coroutines, and StateFlow
- **Local Database**: Room with KSP
- **Remote Integration**: Supabase (REST API with Ktor/Retrofit serialization)
- **Target Android Version**: Android 14+ (API 34) / minSdk 26

---

## Getting Started

### Prerequisites

- Android Studio Koala / Ladybug or newer
- JDK 17 or higher
- Android SDK 34+

### Building from Source

1. Clone this repository:
   ```bash
   git clone <your-repository-url>.git
   cd cross-match-tracker
   ```
2. Open in Android Studio.
3. Sync Gradle and run on an emulator or physical device.

To build the APK via command line:
```bash
./gradlew assembleDebug
```
The APK will be generated in `app/build/outputs/apk/debug/app-debug.apk`.

---

## GitHub Release Instructions

1. Go to your repository on GitHub.
2. Under the **Releases** section on the right, click **Create a new release** (or **Draft a new release**).
3. Set the release tag (e.g., `v1.0.0`) and title (e.g., `v1.0.0 - Initial Release`).
4. Attach the generated `.apk` file (from `app/build/outputs/apk/debug/app-debug.apk` or via the AI Studio Export menu).
5. Click **Publish release**.

---

## License

This project is licensed under the Apache 2.0 License.
