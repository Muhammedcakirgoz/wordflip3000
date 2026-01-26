# 📸 Ekran Görüntüleri Alma Rehberi - WordFlip 3000

## 🎯 HEDEF
Google Play Store için minimum 2, maksimum 8 ekran görüntüsü hazırlamak.

---

## 📏 TEKNİK GEREKSİNİMLER

### Boyutlar
```
Telefon: 1080x1920 px veya 1080x2340 px (önerilen)
Format: PNG veya JPEG
Max: 8 MB per dosya
Min: 2 adet
Max: 8 adet
```

### Önerilen Ekranlar (Öncelik Sırasıyla)
1. ✅ Ana menü (logo + 3 buton) - **ZORUNLU**
2. ✅ Kelime öğrenme kartı - **ZORUNLU**
3. 📊 İlerleme ekranı
4. 🎯 Seviye seçimi
5. 🌙 Ayarlar (tema değiştirme)
6. 🔤 Çeviri ekranı
7. 📝 Günlük görevler
8. 🏆 Başarılar

---

## 🚀 YÖNTEM 1: Android Emulator (ÖNERİLEN)

### Adım 1: Emulator'u Hazırlayın

1. **Android Studio'da emulator başlatın**:
   ```
   AVD Manager → Play butonu (yeşil üçgen)
   ```

2. **Önerilen Cihaz**:
   ```
   Pixel 5 veya Pixel 6
   Resolution: 1080 x 2340
   API Level: 30+ (Android 11+)
   ```

### Adım 2: Uygulamayı Çalıştırın

```
Run → Run 'app'
Emulator'da uygulama açılacak
```

### Adım 3: Ekran Görüntülerini Alın

**Emulator'da Screenshot Alma**:

1. Emulator penceresinde sağ taraftaki toolbar
2. **Camera icon** 📷 tıklayın (veya Ctrl+S / Cmd+S)
3. Otomatik olarak kaydedilir

**Veya:**
```
Tools → AVD Manager → Emulator → Screenshot
```

**Kayıt Yeri**:
```
Windows: C:\Users\[kullanıcı]\Desktop\
Mac: ~/Desktop/
```

### Adım 4: Hangi Ekranları Almalı?

#### 1️⃣ ANA MENÜ ✅ **ZORUNLU**
```
- Temiz, modern görünüm
- Logo net görünmeli
- 3 ana buton (Kelime Öğren, Günlük Görev, Ayarlar)
- Light tema tercih edilir
```

**Nasıl:**
- Uygulamayı aç
- Ana ekranda bekle
- Screenshot al

#### 2️⃣ KELİME KARTLARI ✅ **ZORUNLU**
```
- Bir kelime kartı göster
- "Flip" (çevir) butonu görünmeli
- Temiz ve anlaşılır
```

**Nasıl:**
- Ana menü → "Kelime Öğren"
- Seviye seç (A2 veya B1 tercih edilir)
- İlk kart açıldığında screenshot al

#### 3️⃣ İLERLEME EKRANI
```
- Grafik/istatistikler görünmeli
- Öğrenilen kelime sayısı
- Seviye bilgisi
```

**Nasıl:**
- Bottom navigation → İlerleme
- Screenshot al

#### 4️⃣ SEVİYE SEÇİMİ
```
- Tüm seviyeler görünmeli (A1-C1)
- Hangi seviye açık/kilitli belli olmalı
```

**Nasıl:**
- Ana menü → Kelime Öğren
- Seviye seçim ekranında screenshot

#### 5️⃣ AYARLAR (Opsiyonel)
```
- Tema değiştirme
- Dil seçimi
- Temiz menü
```

#### 6️⃣ ÇEVİRİ EKRANI (Opsiyonel)
```
- Çeviri özelliğini göster
- Input/output alanları
```

**Nasıl:**
- Bottom navigation → Çeviri
- Örnek bir kelime yaz ve çevir
- Screenshot al

---

## 🚀 YÖNTEM 2: Gerçek Cihaz

### Adım 1: USB Debugging Aktif Et

**Cihazda**:
```
Settings → About Phone → Build Number (7 kez tıkla)
Settings → Developer Options → USB Debugging ✅
```

### Adım 2: Cihazı Bağla

```
USB kablo ile bilgisayara bağla
Android Studio → Run → Cihazınızı seçin
```

### Adım 3: Screenshot Al

**Yöntem A: Cihazda**
```
Power + Volume Down (aynı anda bas)
Kayıt yeri: Gallery → Screenshots
```

**Yöntem B: Android Studio'dan**
```
View → Tool Windows → Logcat
Camera icon 📷 (Screenshot)
```

### Adım 4: Bilgisayara Aktar

```
USB kablo ile bağlı iken:
File Explorer (Windows) / Android File Transfer (Mac)
DCIM/Screenshots/ → Kopyala
```

---

## 🎨 YÖNTEM 3: Screenshot Düzenleme (Opsiyonel - Profesyonel)

### Araçlar

**1. Canva (Kolay)**
- https://www.canva.com/
- "Phone Mockup" şablonları
- Screenshot'ları mockup'a yerleştir
- Başlık ve açıklama ekle

**2. Figma (Profesyonel)**
- https://www.figma.com/
- Device frame ekle
- Screenshot yerleştir
- Export 1080x1920

**3. Shotbot (Hızlı)**
- https://app.shotbot.io/
- Upload screenshot
- Otomatik device frame
- Download

### Mockup Kullanımı

```
1. Screenshot'ı al (çerçevesiz)
2. Canva/Figma'da telefon mockup aç
3. Screenshot'ı telefon ekranına yerleştir
4. (Opsiyonel) Başlık ekle: "Kelime Öğrenme", vb.
5. Export: 1080x1920 PNG
```

---

## ✅ KALİTE KONTROL

Screenshot'lar hazır olduğunda kontrol edin:

### Teknik Kontrol
- [ ] Boyut doğru: 1080x1920 veya 1080x2340
- [ ] Format: PNG veya JPEG
- [ ] Dosya boyutu: Max 8 MB
- [ ] Net ve bulanık değil
- [ ] Status bar temiz (bildirim yok)

### İçerik Kontrol
- [ ] Ekran tam ve kesilmemiş
- [ ] Metin okunabilir
- [ ] Kişisel bilgi yok (e-posta, telefon, vb.)
- [ ] Uygulama adı görünüyor
- [ ] Ana özellikler net

### Estetik Kontrol
- [ ] Tutarlı tema (hepsi light veya dark)
- [ ] Profesyonel görünüm
- [ ] İyi örnekler kullanılmış
- [ ] Boş ekran yok

---

## 📝 DOSYA İSİMLENDİRME

```
screenshot-1-main-menu.png
screenshot-2-word-cards.png
screenshot-3-progress.png
screenshot-4-level-selection.png
screenshot-5-settings.png
screenshot-6-translate.png
```

**Veya basitçe**:
```
1.png
2.png
3.png
...
```

---

## 🚀 HIZLI BAŞLANGIÇ (10 DAKİKA)

### Minimum 2 Screenshot İçin:

```
1. Emulator aç (Android Studio)
2. Uygulamayı çalıştır (Run)
3. Ana menüde screenshot al (📷)
4. "Kelime Öğren" → Bir kart aç → Screenshot al (📷)
5. İki PNG dosyası Desktop'ta olacak
6. Dosyaları bir klasöre kaydet: "wordflip-screenshots/"
```

**Bitti!** ✅ Google Play'e yüklemek için hazır.

---

## 🎯 ÖNERİLER

### Google Play İçin En İyi Pratikler:

1. **İlk 2 screenshot çok önemli** - En çok görüntülenen
2. **Ana özelliği göster** - Kelime öğrenme odaklı
3. **Temiz ve profesyonel** - Karmaşık ekranlar kullanma
4. **Light tema tercih et** - Daha parlak ve çekici
5. **Gerçek içerik göster** - Boş ekranlar değil

### Kaçınılması Gerekenler:

- ❌ Debug menüleri
- ❌ Geliştirici araçları
- ❌ Hata mesajları
- ❌ Lorem ipsum placeholder metinler
- ❌ Kişisel bilgiler

---

## 🆘 SORUN GİDERME

### Screenshot bulanık çıkıyor
```
Çözüm: Emulator'da daha yüksek çözünürlük kullanın
AVD Manager → Edit → Show Advanced Settings
Display → Resolution: 1080 x 2340
```

### Emulator çok yavaş
```
Çözüm 1: Hardware acceleration aktif olmalı
Çözüm 2: Gerçek cihaz kullanın (daha hızlı)
```

### Screenshot kayıt yeri bulunamıyor
```
Windows: %USERPROFILE%\Desktop
Mac: ~/Desktop
veya Emulator → More (...) → Screenshots klasörü
```

---

## 📊 ÖNERİLEN SIRA

1. screenshot-1-main-menu.png → Ana menü
2. screenshot-2-word-card.png → Kelime kartı (flip edilmemiş)
3. screenshot-3-progress.png → İlerleme istatistikleri
4. screenshot-4-levels.png → Seviye seçimi

**4 screenshot yeterli! Google Play için ideal sayı.**

---

**🎉 Hazır olduğunda bir sonraki adıma geçin: Feature Graphic oluşturma!**
