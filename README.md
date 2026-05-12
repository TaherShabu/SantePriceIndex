# Sante-Price Index

**Vendor's Intelligence · Fair Price Tool**

Sante-Price Index is a comprehensive Android application designed to empower vendors and traders with real-time price monitoring, profit calculation, and market trend analysis.

## 🚀 Features

-   **Price Watch**: Monitor real-time price fluctuations and stay updated with the latest market rates.
-   **Profit Calc**: Easily calculate profit margins and determine optimal selling prices based on costs and desired returns.
-   **Price Board (Digital Slate)**: A digital display for price listings, perfect for quick updates and clear communication with customers.
-   **Trends**: Visualize market data over time to identify patterns and make informed business decisions.

## 🛠 Tech Stack

-   **Language**: [Kotlin](https://kotlinlang.org/)
-   **UI Framework**: [Android XML Layouts](https://developer.android.com/guide/topics/ui/declaring-layout) with [View Binding](https://developer.android.com/topic/libraries/view-binding)
-   **Architecture**: MVVM (Model-View-ViewModel)
-   **Database**: [Room Persistence Library](https://developer.android.com/training/data-storage/room)
-   **Backend**: [Firebase Firestore](https://firebase.google.com/docs/firestore) for real-time data synchronization.
-   **Components**:
    -   Material Design Components
    -   Jetpack Lifecycle (ViewModel, LiveData)
    -   Navigation Component
    -   Coroutines for asynchronous tasks

## 📂 Project Structure

```text
app/src/main/java/com/sante/priceindex/
├── MainActivity.kt          # Host activity with bottom navigation
├── ui/                      # UI Components (Fragments)
│   ├── PriceWatchFragment.kt
│   ├── ProfitCalcFragment.kt
│   ├── DigitalSlateFragment.kt
│   └── TrendsFragment.kt
└── ...                      # Models, ViewModels, and Data sources
```

## 🏗 Setup & Installation

1.  **Clone the repository**:
    ```bash
    git clone https://github.com/your-username/SantePriceIndex.git
    ```
2.  **Open in Android Studio**:
    -   Go to `File -> Open` and select the `SantePriceIndex` directory.
3.  **Firebase Configuration**:
    -   Create a new project in the [Firebase Console](https://console.firebase.google.com/).
    -   Add an Android App with the package name `com.sante.priceindex`.
    -   Download `google-services.json` and place it in the `app/` directory.
4.  **Build & Run**:
    -   Click the **Run** button in Android Studio to deploy the app to an emulator or physical device.

## 📄 License

This project is licensed under the Apache License, Version 2.0. See the [LICENSE](LICENSE) file for details.
