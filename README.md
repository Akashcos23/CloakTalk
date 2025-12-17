# CloakTalk

A secure Android messaging application built with Kotlin and Jetpack Compose, featuring end-to-end encryption for private communication.

## Table of Contents

- [Features](#features)
- [Project Structure](#project-structure)
- [Requirements](#requirements)
- [Installation](#installation)
- [Build & Run](#build--run)
- [Architecture](#architecture)
- [Technology Stack](#technology-stack)
- [Testing](#testing)
- [Configuration](#configuration)
- [Contributing](#contributing)
- [License](#license)

## Features

- 🔐 **End-to-End Encryption**: Secure message encryption using cryptographic algorithms
- 🔑 **Key Management**: Secure key storage and management for encryption/decryption
- 🔓 **Firebase Authentication**: User authentication with Firebase Auth and Google Sign-In
- 💾 **Local Database**: Room database for offline message storage
- 🎨 **Modern UI**: Built with Jetpack Compose for a smooth and responsive interface
- 📱 **Responsive Design**: Optimized for Android devices (API 24+)
- 🧪 **Comprehensive Tests**: Unit tests for encryption and repository logic

## Project Structure

```
CloakTalk/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/cloaktalk/
│   │   │   │   ├── auth/                 # Authentication logic
│   │   │   │   │   └── FirebaseAuthManager.kt
│   │   │   │   ├── data/                 # Data layer
│   │   │   │   │   ├── local/            # Room database
│   │   │   │   │   └── repository/       # Data repositories
│   │   │   │   ├── ui/                   # Compose UI layer
│   │   │   │   │   └── navigation/       # Navigation setup
│   │   │   │   └── MainActivity.kt
│   │   │   └── res/                      # Resources (layouts, strings, etc.)
│   │   ├── test/                         # Unit tests
│   │   └── androidTest/                  # Android instrumented tests
│   ├── build.gradle.kts                  # App module build configuration
│   └── proguard-rules.pro                # ProGuard configuration
├── gradle/
│   ├── libs.versions.toml                # Dependency versions
│   └── wrapper/
├── Testing/                              # Test files
│   ├── EncryptionTest.kt
│   └── KeyRepositoryTest.kt
├── build.gradle.kts                      # Root build configuration
├── settings.gradle.kts                   # Gradle settings
└── gradle.properties                     # Gradle properties
```

## Requirements

- **Android Studio**: Latest version (Hedgehog or newer recommended)
- **Minimum SDK**: API 24 (Android 7.0)
- **Target SDK**: API 36 (Android 15)
- **Java Version**: 11+
- **Kotlin**: 1.9+ (managed by Gradle)
- **Gradle**: 8.0+ (via wrapper)

## Installation

### Prerequisites

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/CloakTalk.git
   cd CloakTalk
   ```

2. Ensure you have Android Studio installed with the latest Android SDK.

3. Set up your local Firebase project:
   - Go to [Firebase Console](https://console.firebase.google.com)
   - Create a new project or use an existing one
   - Download the `google-services.json` file
   - Place it in the `app/` directory

4. Update `local.properties` (if needed):
   ```properties
   sdk.dir=/path/to/your/Android/SDK
   ```

## Build & Run

### Using Android Studio

1. Open the project in Android Studio
2. Wait for Gradle sync to complete
3. Connect an Android device or start an emulator
4. Click **Run** (Shift + F10) or select **Run > Run 'app'**

### Using Gradle

Build the app:
```bash
./gradlew build
```

Run the app on a connected device:
```bash
./gradlew installDebug
```

Create a release build:
```bash
./gradlew buildRelease
```

## Architecture

CloakTalk follows the **MVVM (Model-View-ViewModel)** architecture pattern with clean separation of concerns:

### Layers

- **UI Layer** (`ui/`): Jetpack Compose screens and navigation
- **Data Layer** (`data/`): Repositories and local database (Room)
- **Auth Layer** (`auth/`): Firebase authentication management
- **Encryption**: Cryptographic operations for message security

### Key Components

- **MainActivity**: Entry point of the application
- **EncryptionApp**: Main Compose navigation container
- **FirebaseAuthManager**: Handles user authentication
- **Repository Pattern**: Abstracts data sources for repositories

## Technology Stack

### Core Framework
- **Kotlin**: Modern programming language for Android development
- **Jetpack Compose**: Modern declarative UI framework
- **Jetpack Navigation**: Navigation between screens

### Database & Storage
- **Room**: Local SQLite database abstraction layer
- **DataStore/SharedPreferences**: Key-value storage (if used)

### Authentication & Security
- **Firebase Authentication**: User account management
- **Google Sign-In**: OAuth authentication
- **Android Credentials API**: Credential management

### Networking & Services
- **Firebase Analytics**: App analytics
- **Google Play Services**: Core services

### UI & UX
- **Material3**: Material Design 3 components
- **Coil**: Image loading library
- **Lifecycle**: Component lifecycle management

### Testing
- **JUnit 4**: Unit testing framework
- **Espresso**: UI testing framework
- **Compose UI Test**: Compose-specific testing utilities

## Testing

### Running Tests

Run all unit tests:
```bash
./gradlew test
```

Run instrumented tests on device:
```bash
./gradlew connectedAndroidTest
```

### Test Files

- `Testing/EncryptionTest.kt`: Tests for encryption functionality
- `Testing/KeyRepositoryTest.kt`: Tests for key repository operations

### Test Coverage

Test your encryption logic to ensure:
- ✅ Message encryption/decryption works correctly
- ✅ Keys are stored and retrieved securely
- ✅ Authentication flows work as expected
- ✅ UI components render properly

## Configuration

### Build Configuration

Edit `app/build.gradle.kts` to modify:
- `compileSdk` & `targetSdk`: Android API levels
- `minSdk`: Minimum supported API level
- `versionCode` & `versionName`: App version information
- Dependencies and their versions

### Dependency Versions

Manage all dependency versions in `gradle/libs.versions.toml`:
```toml
[versions]
compileSdk = "36"
minSdk = "24"
# ... other versions
```

### Firebase Configuration

Ensure your `app/google-services.json` is properly placed and contains:
- Project ID
- API keys
- Firebase URLs

## Troubleshooting

### Gradle Sync Issues
- Run: `./gradlew clean`
- Invalidate caches in Android Studio (File > Invalidate Caches)
- Sync Gradle files again

### Firebase Connection Issues
- Verify `google-services.json` is in the `app/` directory
- Check Firebase console for API keys
- Ensure Internet permission in AndroidManifest.xml

### Build Failures
- Clean project: `./gradlew clean`
- Rebuild: `./gradlew build`
- Check Kotlin plugin version compatibility

## Contributing

Contributions are welcome! Please follow these guidelines:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Code Style

- Follow [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use meaningful variable and function names
- Add comments for complex logic
- Ensure all tests pass before submitting a PR

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## Additional Resources

- [Android Developers](https://developer.android.com/)
- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)
- [Firebase Documentation](https://firebase.google.com/docs)
- [Kotlin Documentation](https://kotlinlang.org/docs/home.html)
- [Room Database Guide](https://developer.android.com/training/data-storage/room)

## Support

For issues, questions, or suggestions, please open an issue on the repository.

---

**Last Updated**: December 2025
