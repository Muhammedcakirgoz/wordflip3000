plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.learning"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.learning"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    
    // Split APKs'i devre dışı bırak - Tek universal APK oluştur
    splits {
        abi {
            isEnable = false
        }
    }

    buildTypes {
        release {
            // Güvenlik: Code obfuscation ve minification aktif
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            
            // Güvenlik: Debug bilgilerini kaldır
            isDebuggable = false
            isJniDebuggable = false
            isRenderscriptDebuggable = false
            isPseudoLocalesEnabled = false
            
            // Güvenlik: Sadece gerçek cihazlar için ARM mimarileri (APK boyutunu küçültür)
            ndk {
                abiFilters += listOf("arm64-v8a", "armeabi-v7a")
            }
        }
        debug {
            // Debug modunda güvenlik ayarları
            isMinifyEnabled = false
            isDebuggable = true
            
            // Debug: Tüm mimarileri destekle (emulator ve gerçek cihaz için)
            // x86_64: Android Emulator
            // arm64-v8a, armeabi-v7a: Gerçek cihazlar
            ndk {
                abiFilters += listOf("arm64-v8a", "armeabi-v7a", "x86", "x86_64")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.mlkit.translate)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}