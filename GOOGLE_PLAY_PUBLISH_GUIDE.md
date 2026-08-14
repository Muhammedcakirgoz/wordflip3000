# 🚀 Google Play Store Yayınlama Rehberi - WordFlip 3000

## ✅ MEVCUT DURUM

- [x] Google Play Console hesabı açıldı ve doğrulandı
- [x] Uygulama kodu hazır
- [x] Güvenlik ayarları yapıldı
- [x] Screenshots hazır (5 adet)
- [x] Store metinleri hazır
- [ ] ⚠️ **Package name değiştirilmeli** (KRİTİK!)
- [ ] Gizlilik politikası URL'i gerekli
- [ ] Signing key oluşturulmalı
- [ ] Release AAB oluşturulmalı

---

## 🔴 **ADIM 1: PACKAGE NAME DEĞİŞTİR (KRİTİK!)**

### Sorun
Mevcut package name: `com.example.learning`  
**Google Play "example" içeren package name'leri KABUL ETMEZ!**

### Çözüm
Yeni package name: `com.wordflip.learning`

### Nasıl Değiştirilir?

#### Yöntem 1: Android Studio Refactor (ÖNERİLEN)

1. **Android Studio'da projeyi açın**

2. **Project görünümünde**:
   ```
   app/src/main/java/com/example/learning
   ```
   sağ tıklayın

3. **Refactor → Rename** seçin

4. **Yeni isim**: `wordflip`

5. **Search in comments and strings** ✅ seçin

6. **Refactor** tıklayın

7. **build.gradle.kts'i açın** ve şunu değiştirin:
   ```kotlin
   // ÖNCE
   namespace = "com.wordflip.learning"
   applicationId = "com.wordflip.learning"
   
   // SONRA
   namespace = "com.wordflip.learning"
   applicationId = "com.wordflip.learning"
   ```

8. **Sync Project** (File → Sync Project with Gradle Files)

9. **Clean & Rebuild**:
   ```
   Build → Clean Project
   Build → Rebuild Project
   ```

#### Yöntem 2: Manuel (Alternatif)

1. `app/build.gradle.kts` dosyasında:
   ```kotlin
   android {
       namespace = "com.wordflip.learning"
       
       defaultConfig {
           applicationId = "com.wordflip.learning"
           // ...
       }
   }
   ```

2. Tüm `.kt` dosyalarında package satırını değiştir:
   ```kotlin
   // Önce
   package com.wordflip.learning
   
   // Sonra
   package com.wordflip.learning
   ```

3. Klasör yapısını değiştir:
   ```
   app/src/main/java/com/example/learning
   →
   app/src/main/java/com/wordflip/learning
   ```

---

## 🔑 **ADIM 2: SIGNING KEY OLUŞTUR**

Google Play Store için uygulamanızı imzalamanız gerekiyor.

### Android Studio'da:

1. **Build → Generate Signed Bundle / APK**

2. **Android App Bundle** seçin → **Next**

3. **Create new...** tıklayın (ilk kez ise)

4. **Key Store Ayarları**:
   ```
   Key store path: C:\Users\mami9\wordflip-release.jks
   Password: [Güçlü bir şifre - KAYDET!]
   Alias: wordflip
   Alias Password: [Yine güçlü bir şifre - KAYDET!]
   
   Certificate:
   First and Last Name: Muhammed Çakırgöz
   Organizational Unit: Independent Developer
   Organization: WordFlip
   City or Locality: [Şehriniz]
   State or Province: [İliniz]
   Country Code: TR
   
   Validity (years): 25 (en az 25 yıl olmalı!)
   ```

5. **OK** tıklayın

6. **⚠️ ÖNEMLİ: Şifreleri ve JKS dosyasını GÜVENLİ BİR YERE KAYDET!**
   - Bu bilgileri kaybederseniz uygulamayı güncelleyemezsiniz!

---

## 📦 **ADIM 3: RELEASE AAB OLUŞTUR**

### Android Studio'da:

1. **Build → Generate Signed Bundle / APK**

2. **Android App Bundle** seçin → **Next**

3. **Key store bilgilerini** girin (Adım 2'de oluşturdunuz)

4. **Next** tıklayın

5. **Build Variants**:
   ```
   ✅ release (seçili olmalı)
   ❌ debug (seçmeyin)
   ```

6. **Signature Versions**:
   ```
   ✅ V1 (Jar Signature)
   ✅ V2 (Full APK Signature)
   ```

7. **Finish** tıklayın

8. **AAB dosyası oluşacak**:
   ```
   app/build/outputs/bundle/release/app-release.aab
   ```

9. **Bu dosyayı kaydedin** (Google Play'e yükleyeceksiniz)

---

## 🌐 **ADIM 4: GİZLİLİK POLİTİKASI YAYINLA**

Google Play için gizlilik politikası URL'i gerekli.

### Hızlı Yol: GitHub Gist (2 Dakika)

1. https://gist.github.com/ → Giriş yapın

2. **New gist** tıklayın

3. **Filename**: `privacy-policy.md`

4. **İçerik**: `PRIVACY_POLICY.md` dosyanızın içeriğini kopyalayın

5. **Create public gist** tıklayın

6. **URL'yi kopyalayın**:
   ```
   https://gist.github.com/Muhammedcakirgoz/[id]
   ```

7. Bu URL'yi Google Play Console'da kullanacaksınız

---

## 🎮 **ADIM 5: GOOGLE PLAY CONSOLE'DA UYGULAMA OLUŞTUR**

### 5.1 Yeni Uygulama Oluştur

1. https://play.google.com/console/ → Giriş yapın

2. **Create app** tıklayın

3. **App details**:
   ```
   App name: WordFlip 3000
   Default language: Turkish (Türkçe)
   App or game: App
   Free or paid: Free
   ```

4. **Declarations**:
   ```
   ✅ Developer Program Policies
   ✅ US export laws
   ```

5. **Create app** tıklayın

### 5.2 Set up your app (Sol menü)

#### **App access** (Uygulama Erişimi)
```
All functionality is available without restrictions
→ Save
```

#### **Ads** (Reklamlar)
```
No, my app does not contain ads
→ Save
```

#### **Content ratings** (İçerik Derecelendirmesi)

1. **Start questionnaire**

2. **Email address**: muhammedcakirgoz00@gmail.com

3. **Category**: Education

4. **Sorular** (Hepsi "No"):
   ```
   Violence: No
   Sexual content: No
   Language: No
   Controlled substances: No
   User interaction: No
   Shares location: No
   ```

5. **Calculate rating** → **Apply rating**

#### **Target audience** (Hedef Kitle)

```
Target age: 13+ (Teens and Adults)
→ Next → Save
```

#### **News apps** (Haber Uygulamaları)
```
No, this is not a news app
→ Save
```

#### **COVID-19 contact tracing and status apps**
```
No
→ Save
```

#### **Data safety** (Veri Güvenliği)

1. **Start**

2. **Does your app collect or share user data?**:
   ```
   No, our app doesn't collect or share user data
   ```

3. **Next** → **Submit**

#### **Government apps**
```
No, this is not a government app
→ Save
```

#### **Financial features**
```
No financial features
→ Save
```

#### **Privacy policy** (Gizlilik Politikası)

```
Privacy policy URL: [Adım 4'te aldığınız GitHub Gist URL'i]
→ Save
```

#### **App category** (Uygulama Kategorisi)

```
Category: Education
Tags: Language learning, Vocabulary, Education, English
→ Save
```

#### **Store settings** (Mağaza Ayarları)

```
Email: muhammedcakirgoz00@gmail.com
Phone: [Opsiyonel]
Website: https://github.com/Muhammedcakirgoz/wordflip3000
→ Save
```

---

## 🎨 **ADIM 6: STORE LISTING (Mağaza Listesi)**

Sol menüden **Store listing** tıklayın.

### **App details**

**App name**:
```
WordFlip 3000 - İngilizce Öğren
```

**Short description** (80 karakter max):
```
Eğlenceli kartlarla İngilizce kelime öğren! 🎯 5 seviye, 1000+ kelime, offline
```

**Full description** (4000 karakter max):

`STORE_TEXTS_READY.md` dosyasından **Uzun Açıklama** bölümünü kopyalayın.

### **Graphics**

#### **App icon** (512x512 px)
- Launcher icon'unuzu 512x512 px olarak export edin
- Canva'da yaptıysanız 512x512 olarak indirin

#### **Feature graphic** (1024x500 px) **ZORUNLU**
1. `feature-graphic.svg` dosyasını açın
2. CloudConvert ile PNG'ye çevirin (1024x500)
3. Yükleyin

#### **Phone screenshots** (Min 2, Max 8)
`screenshots/` klasöründeki dosyaları yükleyin:
- screenshot-1-main-menu.png
- screenshot-2-word-card.png
- screenshot-3-progress.png (opsiyonel)
- screenshot-4-level-selection.png (opsiyonel)
- screenshot-5-translate.png (opsiyonel)

**⚠️ ÖNEMLI**: Screenshot boyutları 1080x1920 veya 1080x2340 olmalı!

### **Contact details**

```
Email: muhammedcakirgoz00@gmail.com
Phone: [Opsiyonel]
Website: https://github.com/Muhammedcakirgoz/wordflip3000
```

**Save** tıklayın.

---

## 📱 **ADIM 7: RELEASE (Yayın)**

### 7.1 Production (Üretim)

1. Sol menüden **Production** tıklayın

2. **Create new release** tıklayın

3. **App bundles**:
   - **Upload** tıklayın
   - `app-release.aab` dosyasını seçin (Adım 3'te oluşturdunuz)

4. **Release name**:
   ```
   1.0.0 (1)
   ```

5. **Release notes** (Turkish):
   ```
   🎉 İlk sürüm!
   
   ✨ Özellikler:
   • 1000+ İngilizce kelime
   • A1'den C1'e 5 seviye
   • Spaced Repetition algoritması
   • Offline çalışma
   • Koyu/Açık tema
   • Ücretsiz ve reklamsız
   
   📚 WordFlip 3000 ile İngilizce öğrenmek artık çok kolay!
   ```

6. **Release notes** (English - Opsiyonel):
   ```
   🎉 Initial release!
   
   ✨ Features:
   • 1000+ English words
   • 5 levels (A1-C1)
   • Spaced Repetition algorithm
   • Offline mode
   • Dark/Light theme
   • Free and ad-free
   
   📚 Learn English easily with WordFlip 3000!
   ```

7. **Save** → **Review release**

8. **Tüm bilgileri kontrol edin**

9. **Start rollout to Production** tıklayın

10. **Rollout** tıklayın

---

## ⏱️ **ADIM 8: İNCELEME SÜRECİ**

### Ne Olacak?

1. **Google incelemesi**: 1-7 gün
2. **İlk inceleme**: Genellikle 1-3 gün
3. **Status**: "In review" yazacak

### İnceleme Sırasında:

- ✅ Uygulama çalışıyor mu?
- ✅ Gizlilik politikası doğru mu?
- ✅ Yanıltıcı içerik yok mu?
- ✅ Telif hakkı ihlali yok mu?
- ✅ Store listing uygun mu?

### Sonuç:

**Onaylandı** ✅:
```
🎉 Tebrikler! Uygulamanız Google Play Store'da yayında!
URL: https://play.google.com/store/apps/details?id=com.wordflip.learning
```

**Reddedildi** ❌:
```
Red nedeni e-posta ile gelir
Düzeltmeleri yapın
Tekrar yükleyin
```

---

## 📊 **SONRAKI GÜNCELLEMELER**

### Yeni Sürüm Yüklemek İçin:

1. **build.gradle.kts**'de versiyonları artırın:
   ```kotlin
   versionCode = 2  // Her güncelleme +1
   versionName = "1.0.1"  // Semantic versioning
   ```

2. **Yeni AAB oluşturun** (Adım 3)

3. **Production** → **Create new release**

4. **Yeni AAB'yi yükleyin**

5. **Release notes** yazın (Ne değişti?)

6. **Start rollout to Production**

---

## ✅ **KONTROL LİSTESİ**

Yayınlamadan önce kontrol edin:

### Teknik
- [ ] Package name değiştirildi (`com.wordflip.learning`)
- [ ] versionCode = 1, versionName = "1.0.0"
- [ ] Release AAB oluşturuldu
- [ ] Signing key güvenli yerde saklandı
- [ ] Tüm ekranlarda test yapıldı

### Google Play Console
- [ ] Uygulama oluşturuldu
- [ ] App access ayarlandı
- [ ] Content ratings tamamlandı
- [ ] Data safety tamamlandı
- [ ] Privacy policy URL eklendi
- [ ] Store listing dolduruldu
- [ ] Feature graphic yüklendi (1024x500)
- [ ] Screenshots yüklendi (min 2)
- [ ] Release notes yazıldı

### Store Listing
- [ ] App name (50 karakter max)
- [ ] Short description (80 karakter max)
- [ ] Full description (4000 karakter max)
- [ ] Feature graphic (1024x500 px)
- [ ] Screenshots (min 2, max 8)
- [ ] App icon (512x512 px)
- [ ] Category: Education
- [ ] Contact email

---

## 🆘 **SORUN GİDERME**

### "Package name already exists"
```
Başka biri bu package name'i kullanıyor.
Çözüm: Farklı bir package name seçin
Örnek: com.wordflip.learning → com.wordflip.vocabularyapp
```

### "Upload failed - Invalid signature"
```
Signing key hatası.
Çözüm: 
1. Signing key'i yeniden oluşturun
2. AAB'yi tekrar generate edin
```

### "Feature graphic size error"
```
Boyut 1024x500 px değil.
Çözüm: Görüntüyü tam olarak 1024x500 px yapın
```

### "Screenshots size error"
```
Screenshot boyutları uygun değil.
Çözüm: 1080x1920 veya 1080x2340 px yapın
```

### "Privacy policy URL invalid"
```
URL erişilemiyor veya geçersiz.
Çözüm: 
1. GitHub Gist'te public olduğundan emin olun
2. URL'yi doğru kopyaladığınızdan emin olun
```

---

## 📞 **DESTEK KAYNAKLARI**

- **Google Play Console Help**: https://support.google.com/googleplay/android-developer
- **Play Console**: https://play.google.com/console
- **Developer Policy**: https://play.google.com/about/developer-content-policy/

---

## 🎉 **BAŞARI!**

Tüm adımları tamamladıktan sonra:

1. ✅ Uygulamanız Google Play Store'da
2. ✅ Milyonlarca kullanıcıya ulaşabilir
3. ✅ İstatistikleri takip edebilirsiniz
4. ✅ Güncellemeler yayınlayabilirsiniz

**🎊 Tebrikler! İlk Android uygulamanız yayında!**

---

## 📈 **YAYINDAN SONRA**

### İstatistikler
```
Google Play Console → Statistics
- Kurulum sayısı
- Aktif kullanıcılar
- Crash raporları
- Ratings & reviews
```

### Marketing
```
- Sosyal medyada paylaşın
- GitHub README'de link ekleyin
- Arkadaşlarınıza gönderin
- App store optimizasyonu (ASO)
```

### Güncellemeler
```
- Kullanıcı geri bildirimlerini okuyun
- Bug fix'ler yapın
- Yeni özellikler ekleyin
- Düzenli güncellemeler yayınlayın
```

---

**🚀 Başarılar! WordFlip 3000 Google Play Store'da görüşmek üzere!**
