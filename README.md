# 🚗 Sistema de Gestión para una Concesionaria (Microservicios)

Sistema de backend distribuido para la administración integral de una concesionaria de vehículos.  
Permite gestionar vehículos, clientes, ventas y pagos mediante una arquitectura de **microservicios**, optimizando y automatizando los procesos comerciales y financieros con una API REST robusta, extensible y escalable.

---

## 🌟 Características del Sistema
- Gestión avanzada de ventas con generación automática de pagos (cuotas o pago único).  
- Control de estados de ventas y pagos para seguimiento financiero detallado.  
- Confirmación y anulación de pagos con actualización automática del saldo pendiente.  
- Relaciones internas sólidas entre entidades de un mismo dominio (ej. `Venta` ↔ `VentaDetalle`).  
- Uso de **DTOs** para exposición de información entre microservicios y al frontend.  
- Modularidad y separación de responsabilidades mediante microservicios independientes: `customer-service`, `sales-service`, `payments-service`, `catalog-service`, `gateway-service` y `eureka-service`.  

---

## 🛠️ Tecnologías Utilizadas
- **Back-end (API REST)**  
- Java 17  
- Spring Boot  
  - Spring Web  
  - Spring Data JPA  
  - Spring Security (opcional, para futuras integraciones con auth-service)  
  - Spring Cloud Eureka Client  
  - Spring Cloud Gateway  
- Lombok  
- MySQL / PostgreSQL (configurable)  
- Maven  

---

## 📝 Microservicios y Funcionalidades

### **catalog-service**
- Gestión de vehículos disponibles en la concesionaria.  
- CRUD completo: alta, baja, edición y listado.  
- Información detallada: marca, modelo, año, estado y precio.  

### **customer-service**
- Gestión de clientes.  
- Visualización del historial de compras y pagos realizados.  

### **sales-service**
- Registro de ventas con detalle de vehículo y cliente (almacenando solo `clienteId` y `productoId`).  
- Generación automática de pagos asociados.  
- Actualización de saldo y estado de la venta.  

### **payments-service**
- Confirmación y anulación de pagos.  
- Actualización automática del saldo pendiente y estado del pago.  

### **gateway-service**
- Puerta de entrada al ecosistema de microservicios desde clientes externos (frontend, Postman, etc.).  
- Ruteo hacia los microservicios correspondientes.  

### **eureka-service**
- Registro y descubrimiento de microservicios (Service Discovery).  
- Permite que todos los servicios se localicen dinámicamente y habilita balanceo de carga y resiliencia.  

---

## ⚙️ Requerimientos No Funcionales
- Validaciones en entidades con mensajes claros y personalizados.  
- Modularidad y escalabilidad para futuras integraciones (web, mobile, auth-service).  
- Arquitectura preparada para implementar JWT en el futuro.  
- Código limpio y documentado siguiendo principios SOLID y buenas prácticas.  
- Uso de **DTOs** para desacoplar datos internos de la exposición hacia otros servicios o frontend.  

---

💡 **Nota:**  
Actualmente el sistema **no incluye la gestión de usuarios internos ni autenticación**, para simplificar la demo de microservicios. La seguridad y `auth-service` pueden integrarse en futuras versiones sin afectar la arquitectura principal.
