@echo off
echo ========================================
echo WordFlip 3000 - GitHub Upload Script
echo ========================================
echo.

:: Git kurulu mu kontrol et
git --version >nul 2>&1
if %errorlevel% neq 0 (
    echo [HATA] Git yuklu degil!
    echo Git indirin: https://git-scm.com/downloads
    pause
    exit /b 1
)

echo [OK] Git yuklu.
echo.

:: Git config kontrol
echo Git yapilandirmasi kontrol ediliyor...
git config user.name >nul 2>&1
if %errorlevel% neq 0 (
    echo.
    echo Git yapilandirmasi yapilmamis.
    echo.
    set /p USERNAME="Adinizi girin: "
    set /p EMAIL="E-postanizi girin: "
    git config --global user.name "%USERNAME%"
    git config --global user.email "%EMAIL%"
    echo [OK] Git yapilandirildi.
)

echo.
echo ========================================
echo ADIM 1: Git Repository Baslatiliyor
echo ========================================
git init
echo [OK] Git repository baslatildi.

echo.
echo ========================================
echo ADIM 2: Dosyalar Ekleniyor
echo ========================================
git add .
echo [OK] Tum dosyalar eklendi.

echo.
echo ========================================
echo ADIM 3: Ilk Commit Yapiliyor
echo ========================================
git commit -m "Initial commit: WordFlip 3000 v1.0 - English Vocabulary Learning App with Spaced Repetition"
if %errorlevel% neq 0 (
    echo [HATA] Commit basarisiz!
    pause
    exit /b 1
)
echo [OK] Commit yapildi.

echo.
echo ========================================
echo ADIM 4: GitHub Remote Ekleme
echo ========================================
echo.
echo GitHub'da yeni repository olusturun:
echo 1. https://github.com/ adresine gidin
echo 2. "+" -^> "New repository" tiklayin
echo 3. Repository name: wordflip3000
echo 4. Public secin
echo 5. "Create repository" tiklayin
echo.
set /p GITHUB_URL="GitHub repository URL'inizi girin (ornek: https://github.com/kullanici/wordflip3000.git): "

git remote add origin %GITHUB_URL%
if %errorlevel% neq 0 (
    echo [UYARI] Remote zaten var, guncelleniyor...
    git remote set-url origin %GITHUB_URL%
)
echo [OK] Remote eklendi.

echo.
echo ========================================
echo ADIM 5: GitHub'a Push Ediliyor
echo ========================================
git branch -M main
git push -u origin main

if %errorlevel% neq 0 (
    echo.
    echo [UYARI] Push basarisiz!
    echo.
    echo Olasi nedenler:
    echo 1. GitHub kimlik dogrulamasi gerekiyor
    echo 2. Personal Access Token gerekiyor
    echo.
    echo Personal Access Token olusturmak icin:
    echo 1. GitHub -^> Settings -^> Developer settings
    echo 2. Personal access tokens -^> Generate new token
    echo 3. Scope: "repo" secin
    echo 4. Token'i kopyalayin
    echo 5. Sifre yerine token'i kullanin
    echo.
    pause
    exit /b 1
)

echo.
echo ========================================
echo BASARILI!
echo ========================================
echo.
echo Projeniz GitHub'a yuklendi!
echo Repository: %GITHUB_URL%
echo.
echo Sonraki adimlar:
echo 1. GitHub repository sayfaniza gidin
echo 2. README.md'nin duzgun goruntulendigini kontrol edin
echo 3. About bolumunu doldurun (Description, Topics)
echo 4. Projenizi paylasin!
echo.
pause
