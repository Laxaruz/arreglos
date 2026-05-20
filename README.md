

Proyecto base en Spring Boot MVC para gestion de recursos humanos y certificaciones.

## Requisitos
- Java 17+
- Maven 3.9+
- MySQL local

## Configuracion
Edita `src/main/resources/application.properties` si tu MySQL requiere contraseña.

## Ejecucion local
```powershell
mvn spring-boot:run
```

## Notas
- El esquema se crea/actualiza con `spring.jpa.hibernate.ddl-auto=update`.
- Los roles se manejan como enum en `Usuario`.

