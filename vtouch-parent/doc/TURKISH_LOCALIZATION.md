# Turkish Localization - Türkçe Yerelleştirme

Bu dokümanda Template Java uygulamasının Türkçe yerelleştirme özelliklerini bulabilirsiniz.

## 🇹🇷 Türkçe Varsayılan Dil

Uygulama artık **Türkçe** varsayılan dil olarak yapılandırılmıştır.

### Yapılandırma Detayları:
- **Varsayılan Locale**: `tr_TR`
- **Desteklenen Diller**: Türkçe, İngilizce, İspanyolca
- **Mesaj Dosyaları**: `messages_tr.properties`, `messages.properties`, `messages_es.properties`
- **Karakter Kodlaması**: UTF-8

## 📁 Mesaj Dosyaları

### `messages_tr.properties` (Türkçe - Varsayılan)
- Ana Türkçe mesajları içerir
- 200+ mesaj anahtarı
- Tüm uygulama bileşenleri için çeviri

### `messages.properties` (İngilizce)
- Fallback mesajları
- İngilizce çeviriler

### `messages_es.properties` (İspanyolca)
- İspanyolca çeviriler
- Mevcut destek

## 🔧 Yapılandırma

### Application Configuration
```yaml
spring:
  messages:
    basename: messages
    encoding: UTF-8
    cache-duration: 3600
  web:
    locale: tr_TR
    locale-resolver: fixed
```

### Locale Configuration Class
- `LocaleConfig.java` - Türkçe varsayılan locale ayarları
- Session tabanlı locale resolver
- Dil değiştirme interceptor'u

## 🌐 Dil Değiştirme

### URL Parametresi ile Dil Değiştirme
```bash
# Türkçe (varsayılan)
GET /api/test/messages

# İngilizce
GET /api/test/messages?lang=en

# İspanyolca
GET /api/test/messages?lang=es
```

### Desteklenen Dil Kodları
- `tr` veya `tr_TR` - Türkçe
- `en` veya `en_US` - İngilizce
- `es` veya `es_ES` - İspanyolca

## 📝 Mesaj Kategorileri

### 1. Genel Mesajlar (Common Messages)
```properties
operation.success=İşlem başarıyla tamamlandı
operation.created=Kaynak başarıyla oluşturuldu
operation.updated=Kaynak başarıyla güncellendi
operation.deleted=Kaynak başarıyla silindi
```

### 2. Hata Mesajları (Error Messages)
```properties
SYSTEM_ERROR=Dahili sistem hatası oluştu
VALIDATION_ERROR=Doğrulama başarısız
RESOURCE_NOT_FOUND=Kaynak bulunamadı
INVALID_OPERATION=Geçersiz işlem
```

### 3. Kimlik Doğrulama (Authentication)
```properties
auth.login.success=Giriş başarılı
auth.invalid.credentials=Geçersiz kimlik bilgileri
auth.access.denied=Erişim reddedildi
auth.token.expired=Token süresi doldu
```

### 4. Kullanıcı Yönetimi (User Management)
```properties
user.created=Kullanıcı başarıyla oluşturuldu
user.updated=Kullanıcı başarıyla güncellendi
user.not.found=Kullanıcı bulunamadı
user.already.exists=Kullanıcı zaten mevcut
```

### 5. Doğrulama Mesajları (Validation)
```properties
NotNull=Alan zorunludur
NotEmpty=Alan boş olamaz
Email=Geçersiz e-posta formatı
Size=Alan boyutu {2} ile {1} arasında olmalıdır
```

### 6. Arayüz Mesajları (UI Messages)
```properties
button.save=Kaydet
button.cancel=İptal
button.delete=Sil
button.edit=Düzenle
button.search=Ara
```

## 🧪 Test Endpoint'leri

### Türkçe Mesajları Test Etme
```bash
# Tüm Türkçe mesajları test et
GET /api/test/messages

# Parametreli mesaj test et
GET /api/test/message-with-params?name=Ahmet

# Locale bilgilerini getir
GET /api/test/locale-info
```

### Örnek Yanıt
```json
{
  "success": true,
  "message": "Türkçe mesajlar başarıyla yüklendi",
  "data": {
    "operation.success": "İşlem başarıyla tamamlandı",
    "user.created": "Kullanıcı başarıyla oluşturuldu",
    "auth.login.success": "Giriş başarılı"
  },
  "timestamp": "2024-01-01T10:00:00",
  "path": "/api/test/messages"
}
```

## 📊 Swagger UI - Türkçe

### API Dokümantasyonu
- Swagger UI açıklamaları Türkçe
- Endpoint açıklamaları Türkçe
- Hata mesajları Türkçe
- Örnek yanıtlar Türkçe

### Swagger Erişim
```
http://localhost:7070/api/swagger-ui.html
```

## 🔍 Kullanım Örnekleri

### 1. Controller'da Mesaj Kullanımı
```java
@RestController
public class MyController {
    
    @Autowired
    private MessageService messageService;
    
    @PostMapping("/users")
    public ResponseEntity<ApiResponse<UserDTO>> createUser(@RequestBody CreateUserRequest request) {
        // ... user creation logic
        
        String message = messageService.getMessage("user.created");
        return ResponseEntity.ok(ApiResponse.success(message, userDTO));
    }
}
```

### 2. Exception Handler'da Mesaj Kullanımı
```java
@ExceptionHandler(ResourceNotFoundException.class)
public ResponseEntity<ApiResponse<Object>> handleResourceNotFound(ResourceNotFoundException ex) {
    String message = messageService.getMessage("RESOURCE_NOT_FOUND");
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ApiResponse.error(message, null));
}
```

### 3. Validation Mesajları
```java
@Entity
public class User {
    
    @NotNull(message = "{NotNull}")
    @Size(min = 3, max = 50, message = "{Size}")
    private String username;
    
    @Email(message = "{Email}")
    private String email;
}
```

## 🎯 Demo Kullanıcıları - Türkçe

### Test Hesapları
| Kullanıcı Adı | E-posta | Şifre | Rol | Durum |
|----------------|---------|-------|-----|--------|
| admin | admin@example.com | admin123 | YÖNETİCİ | AKTİF |
| user | user@example.com | user123 | KULLANICI | AKTİF |
| moderator | moderator@example.com | mod123 | MODERATÖR | AKTİF |

### Giriş Testi
```bash
# Türkçe yanıt ile giriş
curl -X POST http://localhost:7070/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

**Beklenen Yanıt:**
```json
{
  "success": true,
  "message": "Giriş başarılı",
  "data": {
    "accessToken": "...",
    "refreshToken": "...",
    "user": {
      "username": "admin",
      "role": "ADMIN"
    }
  }
}
```

## 🚀 Uygulama Başlatma

### PostgreSQL ile Birlikte
```bash
# PostgreSQL'i başlat
docker-compose up -d

# Uygulamayı Türkçe ile başlat
mvn spring-boot:run -Dspring.profiles.active=local
```

### Erişim Noktaları
- **API**: http://localhost:7070/api
- **Swagger UI**: http://localhost:7070/api/swagger-ui.html (Türkçe)
- **Sağlık Kontrolü**: http://localhost:7070/api/actuator/health
- **Test Endpoint'leri**: http://localhost:7070/api/test/messages

## 📋 Özellik Listesi

### ✅ Tamamlanan Özellikler
- [x] Türkçe mesaj dosyası (`messages_tr.properties`)
- [x] Türkçe varsayılan locale ayarı
- [x] Locale yapılandırma sınıfı
- [x] Dil değiştirme desteği
- [x] Swagger UI Türkçe açıklamaları
- [x] Test endpoint'leri
- [x] Türkçe hata mesajları
- [x] Türkçe doğrulama mesajları
- [x] Türkçe kimlik doğrulama mesajları

### 🔄 Desteklenen Diller
- 🇹🇷 **Türkçe** (Varsayılan)
- 🇺🇸 İngilizce
- 🇪🇸 İspanyolca

## 🛠️ Geliştirici Notları

### Yeni Mesaj Ekleme
1. `messages_tr.properties` dosyasına Türkçe mesaj ekleyin
2. `messages.properties` dosyasına İngilizce karşılığını ekleyin
3. İsteğe bağlı olarak `messages_es.properties` dosyasına İspanyolca karşılığını ekleyin

### Mesaj Kullanımı
```java
// Basit mesaj
String message = messageService.getMessage("operation.success");

// Parametreli mesaj
String message = messageService.getMessage("welcome.user", username);

// Varsayılan mesaj ile
String message = messageService.getMessageWithDefault("custom.message", "Varsayılan mesaj");
```

### Yeni Dil Ekleme
1. `messages_[dil_kodu].properties` dosyası oluşturun
2. `LocaleConfig` sınıfında desteklenen dilleri güncelleyin
3. Test endpoint'lerini güncelleyin

## 📞 Destek

Türkçe yerelleştirme ile ilgili sorularınız için:
- Test endpoint'lerini kullanarak mesajları kontrol edin
- Swagger UI'da Türkçe açıklamaları inceleyin
- Hata mesajlarının Türkçe görüntülendiğini doğrulayın

**Türkçe yerelleştirme başarıyla tamamlandı! 🎉** 