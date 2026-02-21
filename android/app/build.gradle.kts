import java.util.Properties
import java.io.FileInputStream
import java.util.Base64

plugins {
    id("com.android.application")
    id("kotlin-android")
    // The Flutter Gradle Plugin must be applied after the Android and Kotlin Gradle plugins.
    id("dev.flutter.flutter-gradle-plugin")
}

val keystorePropertiesFile = rootProject.file("key.properties")
val keystoreProperties = Properties()
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
}

android {
    namespace = "com.mrdarksidetm.wallet"
    compileSdk = 36
    ndkVersion = flutter.ndkVersion

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        jvmToolchain(17)
    }

    defaultConfig {
        applicationId = "com.mrdarksidetm.wallet"
        minSdk = 33
        targetSdk = 36
        ndk {
           abiFilters.add("armeabi-v7a")
           abiFilters.add("arm64-v8a")
        }
        versionCode = flutter.versionCode
        versionName = flutter.versionName
    }
    
    splits {
        abi {
            isEnable = true
            reset()
            include("armeabi-v7a", "arm64-v8a")
            isUniversalApk = true            
        }
    }

    signingConfigs {
        create("release") {
            val envAlias = System.getenv("CM_KEY_ALIAS")?.trim()?.replace("\"", "")
            val envStorePass = System.getenv("CM_KEYSTORE_PASSWORD")?.trim()?.replace("\"", "")
            val envKeyPass = System.getenv("CM_KEY_PASSWORD")?.trim()?.replace("\"", "")?.takeIf { it.isNotEmpty() } ?: envStorePass

            keyAlias = keystoreProperties.getProperty("keyAlias") ?: envAlias
            storePassword = keystoreProperties.getProperty("storePassword") ?: envStorePass
            keyPassword = keystoreProperties.getProperty("keyPassword") ?: envKeyPass

            val base64Key = System.getenv("CM_KEYSTORE_BASE64")
            if (base64Key != null && base64Key.isNotEmpty()) {
                var cleanBase64 = base64Key.replace("\\s".toRegex(), "")
                val padding = (4 - cleanBase64.length % 4) % 4
                if (padding > 0) {
                    cleanBase64 += "=".repeat(padding)
                }
                val decodedBytes = Base64.getDecoder().decode(cleanBase64)
                val tempKeyFile = file("upload-keystore.jks")
                tempKeyFile.writeBytes(decodedBytes)
                storeFile = tempKeyFile
            } else {
                val storeFileStr = keystoreProperties.getProperty("storeFile") ?: System.getenv("CM_KEYSTORE_PATH")
                storeFile = storeFileStr?.let { file(it) }
            }
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
        }
    }
}

flutter {
    source = "../.."
}
