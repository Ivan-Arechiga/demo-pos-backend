package com.democlass.pos.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Demo POS API para QA")
                .description("Backend de ejemplo para un sistema de punto de venta (POS). " +
                    "Diseñado específicamente para pruebas automatizadas de UI, API y performance.\n\n" +
                    "Este backend incluye:\n" +
                    "- Gestión completa de clientes (CRUD)\n" +
                    "- Gestión completa de productos con inventario\n" +
                    "- Registro y consulta de ventas con detalles de líneas\n" +
                    "- Movimientos de caja con soporte para ventas, reembolsos y ajustes manuales\n" +
                    "- Endpoints especiales para pruebas (timeouts, errores aleatorios, validaciones)\n" +
                    "- Documentación completa con Swagger/OpenAPI\n\n" +
                    "IMPORTANTE: Los endpoints bajo /api/test/* son SOLO para pruebas y no representan " +
                    "funcionalidad de un sistema real.")
                .version("1.0.0")
                .contact(new Contact()
                    .name("Demo POS API Support")
                    .email("support@democlass.com")
                    .url("https://github.com/Ivan-Arechiga/demo-pos-backend"))
                .license(new License()
                    .name("Apache 2.0")
                    .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}
