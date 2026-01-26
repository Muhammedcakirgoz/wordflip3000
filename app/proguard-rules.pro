# WordFlip 3000 - Google Play Store Release ProGuard Rules
# Güvenlik ve performans için optimize edilmiş kurallar

# ========== GÜVENLİK KURALLARI ==========

# Kod karıştırma ve optimizasyon
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose

# Kaynak dosya isimlerini gizle
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable

# Sınıf ve metod isimlerini karıştır
-repackageclasses ''
-allowaccessmodification

# ========== UYGULAMA KORUMA ==========

# Ana aktiviteleri koru (Android tarafından çağrılır)
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

# Veritabanı sınıflarını koru (reflection kullanımı için)
-keep class com.example.learning.database.** { *; }

# ML Kit çeviri için gerekli
-keep class com.google.mlkit.** { *; }
-keep class com.google.android.gms.** { *; }

# Material Design bileşenleri
-keep class com.google.android.material.** { *; }

# ========== PERFORMANS ==========

# Kullanılmayan kaynakları kaldır
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# ========== HATA AYIKLAMA ==========

# Crash raporları için stack trace bilgilerini koru
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes Exceptions

# Enum sınıflarını koru
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}