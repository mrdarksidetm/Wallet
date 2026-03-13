# 🏦 Wallet (Android Native)

> **A premium, offline-first Android money tracking application built with Jetpack Compose.**
> *Experience the future of finance with a tactile "Liquid Glass" design and local data sovereignty.* ✨

---

## 🚀 Features

### 🎨 **Material 3 Expressive Design**
- **Sleek Aesthetics**: A "Liquid Glass" UI featuring vibrant gradients, glassmorphism, and premium typography.
- **Dynamic Theming**: Fully utilizes Material 3 dynamic color, with refined hardcoded fallbacks.
- **Micro-Animations**: Smooth, realistic interactions powered by Jetpack Compose spring physics.
- **Tactile Feedback**: Large corner radii (16dp–24dp) for a friendly, modern feel.

### 🧠 **Secure & Offline-First**
- **Zero Network Calls**: Your financial data never leaves your device. No cloud sync, no API keys, total privacy.
- **Local Intelligence**: Rule-based insights analyze your spending habits without external processing.
- **Blazing Fast**: Built on **Room Database** for high-performance local data management.

### 📊 **Comprehensive Financial Core**
- **Dashboard**: Total balance overview with "This Month" breakdown (Income/Expense).
- **Interactive Reports**: Custom-built Donut Charts (Compose Canvas) and category-wise spending breakdowns.
- **Account Management**: Support for Cash, Bank, and Digital Wallets with swipable transaction logs.
- **Feature Pipeline**: Budgets, Bill Splitter, Loans, and Goal tracking (Coming soon).

---

## 🛠️ Tech Stack & Architecture

- **Language**: 100% Kotlin
- **UI Framework**: Jetpack Compose (Material 3)
- **Architecture**: MVVM + Unidirectional Data Flow (UDF)
- **Database**: Room Persistence Library
- **Dependency Injection**: Manual Provider Pattern
- **Navigation**: Compose Navigation
- **Typography**: Google Sans Flex (Bundled locally)

---

## 🏗️ Getting Started

### Prerequisites
- Android Studio Ladybug (or newer)
- Android SDK 34 (Target) / API 26 (Min)
- JDK 17

### ⚡ Installation

1.  **Clone the Repository**
    ```bash
    git clone https://github.com/mrdarksidetm/Wallet.git
    cd Wallet
    ```

2.  **Open in Android Studio**
    Import the project and wait for Gradle to sync.

3.  **Run the App** 🚀
    Select your device/emulator and click the "Run" button.

> [!IMPORTANT]
> This project is optimized for 4GB RAM development environments. Avoid heavy builds; use selective compilation where possible.

---

## 📂 Project Structure

- **`app/src/main/java/com/mrdarksidetm/wallet/ui`**: All Compose screens and components.
- **`app/src/main/java/com/mrdarksidetm/wallet/data`**: Room entities, DAOs, and Database configuration.
- **`app/src/main/java/com/mrdarksidetm/wallet/viewmodel`**: Domain logic and UI state management.
- **`app/src/main/res/font`**: Bundled premium fonts (`Google Sans Flex`).

---

## 🤝 Contributing

We love contributions! Please read our [CONTRIBUTING.md](CONTRIBUTING.md) to learn how to get involved.

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

MIT © [Abhi](https://github.com/mrdarksidetm)
