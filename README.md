<h1 align="center">
  🚗💨 Sistema de Gestión para Concesionaria (Microservicios)
</h1>

<p align="center">
  <b>Sistema backend distribuido para administración integral de una concesionaria de vehículos</b>
  <br>
  <em>Desarrollado con Spring Boot • MySQL/PostgreSQL</em>
</p>

<p align="center">
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

## 📋 Lo que se agregó / mejoró

### 🏗️ **Arquitectura**
- ✅ **Migración de monolito a microservicios** – Separación en 7 servicios independientes
- ✅ **Service Discovery** con Netflix Eureka
- ✅ **API Gateway** con Spring Cloud Gateway (único punto de entrada)
- ✅ **Configuración centralizada** con Spring Cloud Config Server + Git
- ✅ **Balanceo de carga** integrado en Feign Clients

### 🔧 **Tecnologías agregadas**
- ✅ **Spring Cloud Gateway** – Enrutamiento y filtros (TokenRelay)
- ✅ **Netflix Eureka** – Registro y descubrimiento de servicios
- ✅ **Spring Cloud Config Server** – Configuración externa versionada
- ✅ **Resilience4J** – Circuit Breaker y Retry para tolerancia a fallos
- ✅ **Feign Clients** – Comunicación síncrona declarativa entre servicios

### 🔐 **Seguridad**
- ✅ **Propagación de JWT** con `TokenRelay` desde Gateway
- ✅ **Validación de token en cada servicio** – Cada microservicio valida por su cuenta
- ✅ **Filtro JWT personalizado** en Gateway (`JwtTokenValidator`)

### 📊 **Gestión de ventas y pagos**
- ✅ **Generación automática de pagos** al crear una venta (cuotas o pago único)
- ✅ **Anulación de ventas** con restauración de stock y anulación de pagos
- ✅ **Soft delete** – Trazabilidad completa (ventas anuladas siguen visibles, pagos se marcan inactivos)
- ✅ **Actualización automática de saldo** al confirmar pagos

### 📦 **DTOs**
- ✅ **DTOs específicos por servicio** – Separación clara entre `VehiculoDTO` (comunicación) y `VehiculoVentaDetalleDTO` (respuestas externas)


### 🧩 **Servicios**
- ✅ **7 microservicios independientes** con sus propias bases de datos
- ✅ **Comunicación sincrónica** vía Feign con balanceo de carga
- ✅ **Tolerancia a fallos** – Circuit Breaker en llamadas críticas
- ✅ **README actualizado** con badges, tablas y estructura profesional

---

## 📌 **Comparativa rápida**

| Aspecto | Monolito | Microservicios |
|---------|----------|----------------|
| **Despliegue** | Único | Independiente por servicio |
| **Escalabilidad** | Vertical | Horizontal por servicio |
| **Configuración** | Archivos locales | Config Server + Git |
| **Descubrimiento** | No aplica | Eureka |
| **Enrutamiento** | No aplica | Gateway |
| **Tolerancia a fallos** | Manual | Resilience4J |
| **Comunicación** | Interna (mismo proceso) | Feign + HTTP |
| **Seguridad** | JWT en un solo lugar | TokenRelay + validación por servicio |

---

<div align="center">

## 🚀 ¿Listo para Comenzar?

**⭐ ¡No olvides darle una estrella al repo si te fue útil!**

---
*Desarrollado con ❤️ usando Spring Boot y Java 17*

</div>
