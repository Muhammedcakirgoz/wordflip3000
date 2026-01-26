# 🚀 GitHub'a Proje Yükleme Rehberi - WordFlip 3000

## 🎯 HEDEF
WordFlip 3000 projesini GitHub'a güvenli bir şekilde yüklemek.

---

## ✅ HAZIRLIK KONTROL LİSTESİ

Yüklemeden önce kontrol edin:

- [x] `.gitignore` dosyası güncellendi (hassas dosyalar korunuyor)
- [x] `README.md` oluşturuldu (proje açıklaması)
- [x] `LICENSE` dosyası eklendi (MIT License)
- [ ] Signing keys (*.jks) projede YOK
- [ ] API keys projede YOK veya .gitignore'da
- [ ] Kişisel bilgiler temizlendi

---

## 🚀 YÖNTEM 1: Android Studio ile (ÖNERİLEN - Kolay)

### Adım 1: Git Başlat

1. **Android Studio'da projenizi açın**

2. **VCS menüsüne gidin**:
   ```
   VCS → Enable Version Control Integration
   ```

3. **Git seçin** ve **OK** tıklayın

### Adım 2: İlk Commit

1. **Terminal açın** (Alt+F12):
   ```bash
   git add .
   git commit -m "Initial commit: WordFlip 3000 v1.0"
   ```

2. **Veya Android Studio UI ile**:
   ```
   VCS → Commit (Ctrl+K)
   Tüm dosyaları seç
   Commit message: "Initial commit: WordFlip 3000 v1.0"
   Commit butonu
   ```

### Adım 3: GitHub Repository Oluştur

1. **GitHub'a gidin**: https://github.com
2. **Giriş yapın** (hesabınız yoksa oluşturun)
3. Sağ üstte **"+"** → **"New repository"**

**Repository Ayarları**:
```
Repository name: wordflip3000
Description: İngilizce Kelime Öğrenme Uygulaması - English Vocabulary Learning App
Public ✅ (herkes görebilir)
Add README: ❌ (zaten var)
Add .gitignore: ❌ (zaten var)
Choose a license: ❌ (zaten var - MIT)
```

4. **Create repository** tıklayın

### Adım 4: GitHub'a Push

1. **Repository URL'ini kopyalayın**:
   ```
   https://github.com/[kullanici-adiniz]/wordflip3000.git
   ```

2. **Terminal'de** (Android Studio):
   ```bash
   git remote add origin https://github.com/[kullanici-adiniz]/wordflip3000.git
   git branch -M main
   git push -u origin main
   ```

3. **GitHub username/password** isteği gelirse:
   - Username: GitHub kullanıcı adınız
   - Password: **Personal Access Token** (şifre değil!)

### Adım 5: Personal Access Token Oluşturma (Gerekirse)

Eğer şifre istenirse:

1. GitHub → Settings (sağ üst profil)
2. Developer settings (en altta)
3. Personal access tokens → Tokens (classic)
4. Generate new token
5. İsim: "WordFlip3000"
6. Scope: `repo` (seçin)
7. Generate token
8. **Token'ı kopyalayın** (bir daha göremezsiniz!)
9. Terminal'de şifre yerine bu token'ı girin

---

## 🚀 YÖNTEM 2: Git Bash/Terminal ile (Manuel)

### Önkoşul: Git Kurulu Olmalı

**Git var mı kontrol edin**:
```bash
git --version
```

**Yoksa indirin**: https://git-scm.com/downloads

### Adım 1: Git Yapılandırma

```bash
git config --global user.name "Adınız"
git config --global user.email "email@example.com"
```

### Adım 2: Repository Başlat

```bash
cd C:\Users\mami9\AndroidStudioProjects\UygulamaProje
git init
```

### Adım 3: Dosyaları Ekle ve Commit

```bash
git add .
git commit -m "Initial commit: WordFlip 3000 v1.0

- 1000+ kelime veritabanı
- Spaced Repetition (SM-2) algoritması
- Offline çalışma
- 5 seviye (A1-C1)
- Material Design UI"
```

### Adım 4: GitHub'da Repository Oluştur

1. https://github.com/ → New repository
2. İsim: `wordflip3000`
3. Public seçin
4. Create repository

### Adım 5: Push Et

```bash
git remote add origin https://github.com/[kullanici-adiniz]/wordflip3000.git
git branch -M main
git push -u origin main
```

---

## 🔒 GÜVENLİK KONTROL

Yüklemeden ÖNCE kontrol edin:

### ❌ ASLA GitHub'a Yüklenmemeli

```
✗ Signing keys (*.jks, *.keystore)
✗ keystore.properties
✗ google-services.json (eğer Firebase kullanılıyorsa)
✗ API keys
✗ Kişisel bilgiler
✗ Telefon numaraları, e-postalar
✗ Şifreler
```

### ✅ .gitignore Kontrolü

`.gitignore` dosyanız şunları içermeli:

```
# Signing Keys
*.jks
*.keystore
keystore.properties

# API Keys
secrets.properties
google-services.json

# Build files
/build
*.apk
*.aab
```

### 🔍 Hassas Dosya Kontrolü

Yüklemeden önce kontrol edin:

```bash
# Proje klasöründe arayın
dir /s *.jks
dir /s *.keystore

# Veya PowerShell'de
Get-ChildItem -Recurse -Include *.jks,*.keystore
```

**Bulduysanız**: `.gitignore`'a ekleyin ve commit'e dahil etmeyin!

---

## 📝 İYİ COMMIT MESAJLARI

### Format

```
<type>: <kısa açıklama>

<detaylı açıklama (opsiyonel)>
```

### Örnekler

```bash
# İlk commit
git commit -m "Initial commit: WordFlip 3000 v1.0"

# Özellik ekleme
git commit -m "feat: Spaced Repetition algoritması eklendi"

# Bug fix
git commit -m "fix: İlerleme ekranı crash sorunu düzeltildi"

# UI güncellemesi
git commit -m "ui: Ana menü simgeleri modern tasarıma güncellendi"

# Dokümantasyon
git commit -m "docs: README ve kurulum rehberi eklendi"
```

### Commit Tipleri

```
feat:     Yeni özellik
fix:      Bug fix
ui:       UI/UX değişikliği
refactor: Kod refactoring
docs:     Dokümantasyon
test:     Test ekleme/değiştirme
chore:    Genel bakım (dependencies, vb.)
```

---

## 📸 Ekran Görüntüleri Klasörü

GitHub'da güzel görünsün diye:

### Klasör Oluştur

```bash
mkdir screenshots
```

### Screenshot'ları Kopyala

```
wordflip-store-assets/screenshot-1.png → screenshots/screenshot-1.png
wordflip-store-assets/screenshot-2.png → screenshots/screenshot-2.png
...
```

### Commit ve Push

```bash
git add screenshots/
git commit -m "docs: Ekran görüntüleri eklendi"
git push
```

---

## 🎨 Repository'yi Güzelleştir

### GitHub Profile README

1. **Repository Settings**
2. **About** (sağ üst) → Edit
3. **Description**: "İngilizce Kelime Öğrenme Uygulaması"
4. **Website**: Google Play URL'niz (yayınlandığında)
5. **Topics** ekleyin:
   ```
   android, kotlin, education, language-learning, 
   vocabulary, spaced-repetition, material-design
   ```

### Badges Ekle

README.md'ye badges ekleyin (zaten ekli):

```markdown
[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)]()
[![Language](https://img.shields.io/badge/Language-Kotlin-purple.svg)]()
```

---

## 🔄 Gelecekte Güncelleme

### Değişiklikleri Push Etme

```bash
# Değişiklikleri görüntüle
git status

# Tüm değişiklikleri ekle
git add .

# Commit yap
git commit -m "feat: Yeni özellik eklendi"

# Push et
git push
```

### Belirli Dosyaları Push Etme

```bash
git add app/src/main/java/com/example/learning/MainActivity.kt
git commit -m "fix: Ana menü düzeltildi"
git push
```

---

## 🆘 SORUN GİDERME

### "fatal: remote origin already exists"

```bash
git remote remove origin
git remote add origin https://github.com/[kullanici]/wordflip3000.git
```

### "Permission denied"

Personal Access Token kullanın (Adım 5'te açıklandı)

### "Large files detected"

APK/AAB dosyalarını yüklemeyin:

```bash
# Eğer yanlışlıkla eklediyseniz
git rm --cached app/build/outputs/apk/*.apk
git commit -m "Remove APK files"
```

### "rejected - non-fast-forward"

```bash
# Dikkatli! Remote'taki değişiklikleri çeker
git pull origin main --rebase
git push
```

---

## ✅ BAŞARI KONTROLÜ

GitHub'a başarıyla yüklendiğini kontrol edin:

1. **Repository sayfanıza gidin**: `https://github.com/[kullanici]/wordflip3000`

2. **Kontrol listesi**:
   - [x] README.md görünüyor ve düzgün formatlanmış
   - [x] Kod dosyaları var
   - [x] .gitignore dosyası var
   - [x] LICENSE dosyası var
   - [x] Hassas dosyalar YOK (*.jks, vb.)
   - [x] Commit mesajı anlamlı

3. **README önizleme**:
   - Logo görünüyor mu?
   - Linkler çalışıyor mu?
   - Formatlar düzgün mü?

---

## 🎉 TAMAMLANDI!

Projeniz artık GitHub'da! 🚀

**Repository URL'niz**:
```
https://github.com/[kullanici-adiniz]/wordflip3000
```

### Sonraki Adımlar:

1. ✅ **Repository'yi Public yapın** (herkes görebilsin)
2. ✅ **About bölümünü doldurun** (açıklama, topics)
3. ✅ **Star verin** kendi projenize (motivasyon 😄)
4. ✅ **Google Play URL'ini ekleyin** (yayınlandığında)

---

## 📚 Ek Kaynaklar

- **Git Basics**: https://git-scm.com/book/en/v2
- **GitHub Guides**: https://guides.github.com/
- **Markdown Guide**: https://www.markdownguide.org/

---

**🎊 Tebrikler! Projeniz artık açık kaynak!**
