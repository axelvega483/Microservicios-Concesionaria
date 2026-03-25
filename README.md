<h1 align="center">
  🚗💨 Sistema de Gestión para Concesionaria (Microservicios)
</h1>

<p align="center">
  <b>Sistema backend distribuido para administración integral de una concesionaria de vehículos</b>
  <br>
  <em>Desarrollado con Spring Boot • MySQL/PostgreSQL • OpenAPI 3</em>
</p>

<p align="center">
  <a href="http://localhost:8080/swagger-ui/index.html">
    <img src="https://img.shields.io/badge/Documentación-SwaggerUI-brightgreen?style=for-the-badge&logo=swagger" alt="Swagger UI">
  </a>
  <a href="http://localhost:8080/v3/api-docs">
    <img src="https://img.shields.io/badge/API-OpenAPI3-orange?style=for-the-badge&logo=openapi-initiative" alt="OpenAPI 3">
  </a>
  <img src="https://img.shields.io/badge/Java-17-blue?style=for-the-badge&logo=openjdk" alt="Java 17">
  <img src="https://img.shields.io/badge/Spring_Boot-3.4.5-brightgreen?style=for-the-badge&logo=springboot" alt="Spring Boot">
</p>

---

## 🌟 Características del Sistema

<div align="center">

| Característica | Icono | Descripción |
|----------------|-------|-------------|
| **Gestión Avanzada de Ventas** | 💰 | Generación automática de pagos (cuotas o pago único) |
| **Control de Estados** | 📊 | Seguimiento financiero detallado de ventas y pagos |
| **Confirmación y Anulación** | 🔄 | Actualización automática del saldo pendiente |
| **Relaciones Sólidas** | 🔗 | Entidades interconectadas como `Venta` ↔ `VentaDetalle` |
| **DTOs Personalizados** | 🎯 | Exposición de datos desacoplada entre servicios |
| **Microservicios Independientes** | 🧩 | Separación clara de responsabilidades |
| **Escalabilidad y Modularidad** | 📦 | Arquitectura preparada para crecer sin fricciones |

</div>

---

## 📦 Microservicios del Sistema

<div align="center">

| Servicio | Icono | Descripción | Endpoints |
|----------|-------|-------------|-----------|
| **auth-service** | 🔐 | Gestión de users (ADMIN-EMPLEADO) | `GET/POST/PUT/DELETE /user` |
| **catalog-service** | 🚗 | Inventario de vehículos | `GET/POST/PUT/DELETE /vehiculo` |
| **customer-service** | 👥 | Gestión de clientes e historial | `GET/POST/PUT /cliente` |
| **sales-service** | 💰 | Registro de ventas y generación de pagos | `GET/POST /venta` |
| **payments-service** | 💳 | Confirmación y anulación de pagos | `GET/PUT /pago` |
| **gateway-service** | 🌐 | Ruteo hacia microservicios | `/api/**` |
| **eureka-service** | 📡 | Descubrimiento de servicios | `Eureka Dashboard` |

</div>

---

## 🛠️ Tecnologías Utilizadas

<div align="center">

| Tecnología | Icono | Uso |
|------------|-------|-----|
| **Java 17** | <img src="https://img.shields.io/badge/Java-17-blue?style=flat&logo=openjdk" alt="Java 17"> | Lenguaje principal |
| **Spring Boot** | <img src="https://img.shields.io/badge/Spring_Boot-3.4.5-brightgreen?style=flat&logo=springboot" alt="Spring Boot"> | Framework backend |
| **Spring Data JPA** | <img src="https://img.shields.io/badge/JPA-Hibernate-59666C?style=flat&logo=hibernate" alt="Spring Data JPA"> | Persistencia ORM |
| **Spring Cloud** | <img src="https://img.shields.io/badge/Spring_Cloud-Eureka/Gateway-6DB33F?style=flat&logo=spring"> | Eureka Client y Gateway |
| **MySQL / PostgreSQL** | <img src="https://img.shields.io/badge/SQL-MySQL/PostgreSQL-4479A1?style=flat&logo=mysql" alt="SQL"> | Base de datos relacional |
| **Lombok** | <img src="https://img.shields.io/badge/Lombok-Automático-FF9800?style=flat&logo=lombok" alt="Lombok"> | Reducción de boilerplate |
| **Maven** | <img src="https://img.shields.io/badge/Maven-C71A36?style=flat&logo=apache-maven" alt="Maven"> | Gestión de dependencias |

</div>

---

## 📝 Requerimientos Funcionales

<div align="center">

| Servicio | Funcionalidades | Estado |
|----------|-----------------|--------|
| **🚗 Vehículos** | CRUD completo • Detalles técnicos | ✅ Implementado |
| **👥 Clientes** | Gestión y visualización de historial | ✅ Implementado |
| **🔐 Usuarios** | Roles y autenticación básica | ✅ Implementado |
| **💰 Ventas** | Registro y generación de pagos | ✅ Implementado |
| **💳 Pagos** | Confirmación y actualización de estado | ✅ Implementado |

</div>

---

## 📄 Documentación Técnica

<div align="center">

| Recurso | Enlace | Descripción |
|---------|--------|-------------|
| **📖 Swagger UI** | [Swagger](http://localhost:8080/swagger-ui/index.html) | Documentación interactiva |
</div>

---

## ⚙️ Requerimientos No Funcionales

<div align="center">

| Categoría | Especificación | Estado |
|-----------|----------------|--------|
| **🛡️ Validaciones** | Mensajes claros y personalizados | ✅ Implementado |
| **📐 Modularidad** | Separación por microservicio | ✅ Implementado |
| **🔒 Seguridad** | Preparado para JWT y roles | ✅ Implementado |
| **📊 Performance** | Consultas optimizadas | ✅ Implementado |
| **🧼 Código Limpio** | Principios SOLID y buenas prácticas | ✅ Implementado |

</div>

---

<div align="center">

## 🚀 ¿Listo para Comenzar?

[**📖 Ir a la Documentación Interactiva**](http://localhost:8080/swagger-ui/index.html) • 

**⭐ ¡No olvides darle una estrella al repo si te fue útil!**

---
*Desarrollado con ❤️ usando Spring Boot y Java 17*

</div>
