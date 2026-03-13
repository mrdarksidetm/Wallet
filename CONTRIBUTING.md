# Contributing to Wallet

Thank you for your interest in contributing to the native Android version of Wallet! We welcome contributions to help improve the app's features, design, and performance.

## How to Contribute

1.  **Fork the repository** on GitHub.
2.  **Clone** your fork locally.
3.  **Create a branch** for your feature or bugfix (`git checkout -b feature/amazing-feature`).
4.  **Make your changes** following the [Coding Standards](#coding-standards).
5.  **Run tests** to ensure no regressions.
6.  **Commit** your changes with descriptive messages (`git commit -m 'Add support for custom categories'`).
7.  **Push** to your branch (`git push origin feature/amazing-feature`).
8.  **Open a Pull Request**.

## Development Setup

1.  **Environment**: Install the latest stable version of [Android Studio](https://developer.android.com/studio).
2.  **JDK**: Ensure you have JDK 17 configured.
3.  **SDK**: Install Android SDK 34 via the SDK Manager.
4.  **Gradle Sync**: Open the project in Android Studio and allow Gradle to sync all dependencies.
5.  **Running**: Use a physical device (recommended) or an emulator with API 26+.

## Coding Standards

-   **Language**: Use Kotlin with modern idioms.
-   **UI**: All new UI must be built with **Jetpack Compose** and adhere to **Material 3 Expressive** guidelines.
-   **Architecture**: Follow the **MVVM** pattern with Unidirectional Data Flow.
-   **Readability**: Adhere to the *3 Laws of Readable Code*:
    1.  Meaningful Names.
    2.  Single Responsibility.
    3.  Clean Flow.
-   **Formatting**: Use the default Android Studio Kotlin formatter.

## Reporting Issues

If you find a bug or have a feature request, please open an issue on GitHub. Please include:
- A clear description of the issue.
- Steps to reproduce (for bugs).
- Screenshots or screen recordings if applicable.
- Your device and Android version info.

---

Thank you for helping us make Wallet better!
