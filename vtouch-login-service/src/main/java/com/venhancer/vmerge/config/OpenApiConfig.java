package com.venhancer.vmerge.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${server.servlet.context-path:/}")
    private String contextPath;
    
    @Value("${server.port:7070}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        return new OpenAPI()
            .info(apiInfo())
            .servers(List.of(
                new Server()
                    .url("http://localhost:" + serverPort + contextPath)
                    .description("Development server"),
                new Server()
                    .url("https://api.example.com" + contextPath)
                    .description("Production server")
            ))
            .tags(List.of(
                new Tag()
                    .name("Authentication")
                    .description("Giriş, kayıt ve token yönetimi için JWT kimlik doğrulama endpoint'leri"),
                new Tag()
                    .name("User Management")
                    .description("CRUD işlemleri, arama ve istatistikler dahil kullanıcı yönetimi API'leri"),
                new Tag()
                    .name("Health Check")
                    .description("Sağlık kontrolü ve izleme endpoint'leri")
            ))
            .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
            .components(
                new io.swagger.v3.oas.models.Components()
                    .addSecuritySchemes(securitySchemeName,
                        new SecurityScheme()
                            .name(securitySchemeName)
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("bearer")
                            .bearerFormat("JWT")
                    )
            );
    }

    private Info apiInfo() {
        return new Info()
            .title("Template Java API")
            .description("""
                Java 21 ve JWT Kimlik Doğrulama ile kapsamlı Spring Boot 3.4 REST API şablonu.
                
                ## Özellikler
                - **JWT Kimlik Doğrulama**: Erişim ve yenileme token'ları ile güvenli kimlik doğrulama
                - **Kullanıcı Yönetimi**: Rol tabanlı erişim ile kullanıcılar için tam CRUD işlemleri
                - **Genel Yanıtlar**: Tutarlı API yanıt formatı
                - **Hata Yönetimi**: Yerelleştirilmiş mesajlar ile kapsamlı hata yönetimi
                - **Sayfalama**: Liste endpoint'leri için yerleşik sayfalama desteği
                - **Arama ve Filtreleme**: Gelişmiş arama yetenekleri
                - **Veri Doğrulama**: Detaylı hata mesajları ile istek doğrulama
                - **Uluslararasılaştırma**: Çok dilli destek (Türkçe varsayılan)
                - **JPA Entegrasyonu**: PostgreSQL ile Spring Data JPA
                - **Swagger Dokümantasyonu**: JWT desteği ile etkileşimli API dokümantasyonu
                
                ## Kimlik Doğrulama
                Bu API kimlik doğrulama için JWT (JSON Web Token) kullanır. Korumalı endpoint'lere erişmek için:
                1. `/auth/register` ile yeni kullanıcı kayıt edin
                2. `/auth/login` ile giriş yaparak erişim ve yenileme token'ları alın
                3. Yukarıdaki "Authorize" butonunu kullanarak Bearer token'ınızı ayarlayın
                4. Token'ı Authorization header'ında dahil edin: `Bearer <your-token>`
                
                ## Kullanıcı Rolleri
                - **USER**: Temel kullanıcı erişimi
                - **MODERATOR**: İçerik moderasyonu ve kullanıcı yönetimi
                - **ADMIN**: Tam yönetici erişimi
                
                ## Hata Yönetimi
                API detaylı hata bilgisi ile tutarlı hata yanıt formatı kullanır:
                - **Kimlik Doğrulama Hataları**: 401 Unauthorized
                - **Yetkilendirme Hataları**: 403 Forbidden
                - **İş Kuralı Hataları**: 400 Bad Request
                - **Bulunamadı**: 404 Not Found
                - **Doğrulama Hataları**: Alan bazında hatalar ile 400 Bad Request
                - **Sistem Hataları**: 500 Internal Server Error
                
                ## Sayfalama
                Liste endpoint'leri aşağıdaki parametreler ile sayfalama destekler:
                - `page`: Sayfa numarası (0 tabanlı, varsayılan: 0)
                - `size`: Sayfa boyutu (varsayılan: 10)
                - `sort`: Sıralama alanı (varsayılan: createdAt)
                - `direction`: Sıralama yönü (asc/desc, varsayılan: desc)
                
                ## Yanıt Formatı
                Tüm yanıtlar tutarlı bir format takip eder:
                ```json
                {
                  "success": true,
                  "message": "İşlem başarıyla tamamlandı",
                  "data": {...},
                  "timestamp": "2024-01-01T10:00:00",
                  "path": "/api/users"
                }
                ```
                """)
            .version("1.0.0")
            .contact(new Contact()
                .name("API Support")
                .email("support@example.com")
                .url("https://example.com/support"))
            .license(new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT"));
    }
} 