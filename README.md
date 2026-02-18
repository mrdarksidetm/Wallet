# 🏦 Wallet

> **A premium, offline-first Android money tracking application.**
> *Futuristic "Liquid Glass" design meets advanced local intelligence.* ✨

---

## 🚀 Features

### 🎨 **Stunning Design**
- **Expressive Theme Engine**: Material 3 & Glassmorphism aesthetics powered by `flutter_animate`.
- **Fluid Layouts**: Butter-smooth 120Hz animations with `Hero` transitions.
- **Micro-Interactions**: Delightful responsive touches throughout the UI.

### 🧠 **Offline Intelligence (New!)**
- **Smart Insights**: Rule-based engine analyzes your spending habits locally.
- **Privacy First**: 🔒 **No Cloud Sync**. **No API Keys**. Your data never leaves your device.
- **Contextual Greetings**: The app welcomes you based on time and holidays (e.g., "Happy Diwali" 🪔).

### 📊 **Robust Finance Core**
- **Dashboard**: Interactive charts & financial summaries at a glance.
- **Advanced Tools**: 
  - 🎯 **Goals**: Track savings targets.
  - 💸 **Budgets**: Set category limits.
  - 🤝 **Loans**: Manage debts and lendings.
  - 🔄 **Recurring**: Automate your fixed expenses.
- **Core Power**: Built on **Isar Database** for blazing fast local performance.

---

## 🛠️ Getting Started

Ready to build? Follow these steps to get the engine running.

### Prerequisites
- Flutter SDK (Latest Stable)
- Android Studio / VS Code

### ⚡ Installation

1.  **Clone the Magic**
    ```bash
    git clone https://github.com/mrdarksidetm/Wallet.git
    cd Wallet
    ```

2.  **Install Dependencies**
    ```bash
    flutter pub get
    ```

3.  **Generate Code (Critical Step!)** 🏗️
    *We use Isar for our database, which requires code generation.*
    ```bash
    flutter pub run build_runner build
    ```

4.  **Run the App** 🚀
    ```bash
    flutter run
    ```

---

## 🏗️ Architecture

Clean, scalable, and testable.

- 📂 **`lib/core`**: The foundation (Theme, Constants, Utils).
- 📦 **`lib/features`**: Feature-first modular structure.
- 🧱 **`lib/shared`**: Reusable widgets and providers.
- ⚡ **State Management**: Powered by **Riverpod**.

---

## 🤝 Contributing

Contributions are welcome! See [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.

## 📄 License

MIT © [Abhi]
