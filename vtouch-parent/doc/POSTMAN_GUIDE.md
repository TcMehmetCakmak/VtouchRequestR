# 📮 Postman Collection Kullanım Kılavuzu

Bu kılavuz, VMerge Services API projesi için oluşturulan Postman collection'ının nasıl kullanılacağını açıklar.

## 📋 İçerik

- [Kurulum](#-kurulum)
- [Environment Ayarları](#-environment-ayarları)
- [Authentication](#-authentication)
- [API Endpoint'leri](#-api-endpointleri)
- [Örnek Kullanım](#-örnek-kullanım)
- [Troubleshooting](#-troubleshooting)

## 🚀 Kurulum

### 1. Dosyaları İndirin
- `VMerge-Services-API.postman_collection.json` - API Collection
- `VMerge-Services-Environment.postman_environment.json` - Environment Variables

### 2. Postman'e Import Edin
1. Postman'i açın
2. **Import** butonuna tıklayın
3. Her iki JSON dosyasını da seçin ve import edin

### 3. Environment'ı Seçin
1. Sağ üst köşedeki environment dropdown'ından **"VMerge Services Environment"** seçin
2. Environment'ın aktif olduğundan emin olun

## ⚙️ Environment Ayarları

### Temel Ayarlar
| Variable | Değer | Açıklama |
|----------|-------|----------|
| `baseUrl` | `http://localhost:7070/api` | API base URL |
| `accessToken` | (boş) | JWT access token |
| `refreshToken` | (boş) | JWT refresh token |
| `userId` | `1` | Test kullanıcı ID'si |
| `username` | `admin` | Test kullanıcı adı |
| `password` | `admin123` | Test şifresi |

### Özel Ayarlar
| Variable | Değer | Açıklama |
|----------|-------|----------|
| `latitude` | `41.0082` | İstanbul enlem |
| `longitude` | `28.9784` | İstanbul boylam |
| `locationId` | `istanbul` | Test lokasyon ID'si |

## 🔐 Authentication

### 1. Login İşlemi
1. **🔐 Authentication** klasörünü açın
2. **Login** request'ini çalıştırın
3. Başarılı response'ta token'lar otomatik olarak environment'a kaydedilir

### 2. Token Kullanımı
- Login sonrası tüm authenticated request'ler otomatik olarak `{{accessToken}}` kullanır
- Token süresi dolduğunda **Refresh Token** request'ini kullanın

### 3. Test Kullanıcıları
```json
// Admin Kullanıcı
{
  "username": "admin",
  "password": "admin123"
}

// Test Kullanıcı
{
  "username": "testuser",
  "password": "password123"
}
```

## 📡 API Endpoint'leri

### 🔐 Authentication
- **Login** - Kullanıcı girişi
- **Register** - Yeni kullanıcı kaydı
- **Refresh Token** - Token yenileme
- **Get Current User** - Mevcut kullanıcı bilgisi
- **Logout** - Çıkış

### 👥 User Management
- **Create User** - Kullanıcı oluşturma
- **Get User by ID** - ID ile kullanıcı getirme
- **Get User by Username** - Kullanıcı adı ile getirme
- **Update User** - Kullanıcı güncelleme
- **Delete User** - Kullanıcı silme (soft delete)
- **Get All Users** - Tüm kullanıcıları listeleme
- **Search Users** - Kullanıcı arama
- **Get User Statistics** - Kullanıcı istatistikleri
- **Get Recent Users** - Son kullanıcılar
- **Change User Status** - Kullanıcı durumu değiştirme
- **Get Users by Status** - Duruma göre kullanıcılar
- **Get Users by Role** - Role göre kullanıcılar

### 🏥 Health Check
- **Health Check** - Sistem sağlık durumu
- **Application Info** - Uygulama bilgileri

### 🌍 Localization Test
- **Test Turkish Messages** - Türkçe mesaj testi
- **Test English Messages** - İngilizce mesaj testi
- **Test Spanish Messages** - İspanyolca mesaj testi
- **Test Message with Parameters** - Parametreli mesaj testi
- **Get Locale Info** - Dil ayarı bilgileri

### 📍 Location Cache (Redis)
- **Cache User Location** - Kullanıcı konumu kaydetme
- **Get User Location** - Kullanıcı konumu getirme
- **Add to Favorites** - Favori lokasyon ekleme
- **Get User Favorites** - Favori lokasyonları getirme
- **Remove from Favorites** - Favori lokasyon silme
- **Get Location Stats** - Lokasyon istatistikleri
- **Clear User Cache** - Kullanıcı önbelleği temizleme
- **Redis Status** - Redis bağlantı durumu

### 📚 Documentation
- **Swagger UI** - Swagger dokümantasyonu
- **OpenAPI JSON** - OpenAPI JSON dokümantasyonu

## 💡 Örnek Kullanım

### 1. İlk Kurulum
```bash
# 1. Uygulamayı başlatın
mvn spring-boot:run

# 2. Docker servislerini başlatın (opsiyonel)
docker-compose up -d
```

### 2. Authentication Flow
1. **Health Check** - Sistemin çalıştığını doğrulayın
2. **Login** - Kullanıcı girişi yapın
3. **Get Current User** - Token'ın çalıştığını test edin

### 3. User Management Test
1. **Create User** - Yeni kullanıcı oluşturun
2. **Get User by ID** - Oluşturulan kullanıcıyı getirin
3. **Update User** - Kullanıcı bilgilerini güncelleyin
4. **Search Users** - Kullanıcıları arayın

### 4. Localization Test
1. **Test Turkish Messages** - Türkçe mesajları test edin
2. **Test English Messages** - İngilizce mesajları test edin
3. **Get Locale Info** - Dil ayarlarını kontrol edin

### 5. Location Cache Test (Redis gerekli)
1. **Redis Status** - Redis bağlantısını kontrol edin
2. **Cache User Location** - Kullanıcı konumu kaydedin
3. **Get User Location** - Kaydedilen konumu getirin
4. **Add to Favorites** - Favori lokasyon ekleyin

## 🔧 Troubleshooting

### Yaygın Hatalar

#### 1. Connection Refused
```
Error: connect ECONNREFUSED 127.0.0.1:7070
```
**Çözüm:**
- Uygulamanın çalıştığından emin olun
- Port 7070'in açık olduğunu kontrol edin
- `baseUrl` environment variable'ını kontrol edin

#### 2. 401 Unauthorized
```
Error: 401 Unauthorized
```
**Çözüm:**
- Login request'ini çalıştırın
- Token'ın environment'a kaydedildiğini kontrol edin
- Token süresinin dolmadığını kontrol edin

#### 3. 404 Not Found
```
Error: 404 Not Found
```
**Çözüm:**
- URL path'ini kontrol edin
- Context path'in `/api` olduğundan emin olun
- Endpoint'in doğru olduğunu kontrol edin

#### 4. 500 Internal Server Error
```
Error: 500 Internal Server Error
```
**Çözüm:**
- Uygulama loglarını kontrol edin
- Veritabanı bağlantısını kontrol edin
- Redis bağlantısını kontrol edin (location cache için)

### Environment Variable Sorunları

#### Token Kaydedilmiyor
```javascript
// Login request'inin test script'ini kontrol edin
if (pm.response.code === 200) {
    const response = pm.response.json();
    if (response.data && response.data.accessToken) {
        pm.environment.set('accessToken', response.data.accessToken);
        pm.environment.set('refreshToken', response.data.refreshToken);
    }
}
```

#### Environment Seçili Değil
- Sağ üst köşede doğru environment'ın seçili olduğunu kontrol edin
- Environment variable'ların görünür olduğunu kontrol edin

## 📊 Response Formatları

### Başarılı Response
```json
{
  "success": true,
  "message": "İşlem başarıyla tamamlandı",
  "data": {
    // Response data
  },
  "timestamp": "2024-01-01T10:00:00",
  "path": "/api/users"
}
```

### Hata Response
```json
{
  "success": false,
  "message": "Hata mesajı",
  "errors": [
    {
      "field": "email",
      "message": "Geçersiz e-posta formatı"
    }
  ],
  "timestamp": "2024-01-01T10:00:00",
  "path": "/api/users"
}
```

## 🎯 İpuçları

### 1. Test Script'leri
- Login request'inde token otomatik kaydedilir
- Diğer request'lerde manuel token kontrolü yapabilirsiniz

### 2. Environment Değişkenleri
- `{{variableName}}` formatında kullanın
- Environment'ı değiştirirken variable'ları güncelleyin

### 3. Request Body'leri
- JSON formatında yazın
- Content-Type header'ını ekleyin
- Validation kurallarına uyun

### 4. Query Parameters
- URL'de `?param=value` formatında kullanın
- Postman'de Query Params sekmesini kullanın

### 5. Headers
- Authorization: `Bearer {{accessToken}}`
- Content-Type: `application/json`
- Accept: `application/json`

## 📞 Destek

Sorun yaşarsanız:
1. Uygulama loglarını kontrol edin
2. Environment ayarlarını doğrulayın
3. Network bağlantısını kontrol edin
4. Docker servislerinin çalıştığını kontrol edin

Bu kılavuz, VMerge Services API projesini Postman ile test etmenize yardımcı olacaktır. Başarılar! 🚀 