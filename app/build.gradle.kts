import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
}

// LibreTranslate ayarları local.properties'ten okunur; dosya git'e girmediği için
// API anahtarı koda gömülmez. Anahtar yoksa boş string ile (self-host senaryosu) devam eder.
val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) file.inputStream().use { load(it) }
}
val libreTranslateBaseUrl: String =
    localProperties.getProperty("libretranslate.baseUrl") ?: "https://libretranslate.com/"
val libreTranslateApiKey: String =
    localProperties.getProperty("libretranslate.apiKey") ?: ""

android {
    namespace = "com.wordflip.learning"
    compileSdk = 36

    signingConfigs {
        create("release") {
            // İmza bilgileri local.properties'ten okunur; dosya git'e girmediği için
            // şifreler repoya sızmaz. .jks dosyası app klasöründeyse sadece adı yeterli.
            storeFile = file(localProperties.getProperty("signing.storeFile") ?: "wordflip-release.jks")
            storePassword = localProperties.getProperty("signing.storePassword") ?: ""
            keyAlias = localProperties.getProperty("signing.keyAlias") ?: ""
            keyPassword = localProperties.getProperty("signing.keyPassword") ?: ""
        }
    }
    defaultConfig {
        applicationId = "com.wordflip.learning"
        minSdk = 29
        targetSdk = 36
        versionCode = 3
        versionName = "1.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "LIBRETRANSLATE_BASE_URL", "\"$libreTranslateBaseUrl\"")
        buildConfigField("String", "LIBRETRANSLATE_API_KEY", "\"$libreTranslateApiKey\"")
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
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
            signingConfig = signingConfigs.getByName("release")


            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlinx.serialization)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}