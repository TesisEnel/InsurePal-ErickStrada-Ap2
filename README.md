# 📱 InsurePal  
**Contrata seguros en línea de forma rápida y fácil — con funcionalidades administrativas para empleados.**  
App Android desarrollada en **Kotlin**, siguiendo **Clean Architecture**, el patrón **MVI**, y un **Single Source of Truth** con sincronización vía API.

## 📥 Descarga la APK

Haz clic en el siguiente enlace para descargar la última versión de InsurePal:

👉 **[📦 Descargar InsurePal v1.0.0](https://github.com/ErickEstradaR/InsurePal/releases/download/v1.0.0/InsurePalAP2.apk)**


---

## 🚀 Descripción General

**InsurePal** es una aplicación móvil diseñada para simplificar la contratación de seguros desde el teléfono.  
El usuario puede:

- Explorar diferentes tipos de seguros.  
- Cotizar y contratar seguros en línea.  
- Consultar pólizas adquiridas.  
- Realizar pagos y ver historial de transacciones.

Además, incluye un módulo administrativo para empleados:

- Gestión de clientes.  
- Aprobación y monitoreo de pagos.  
- Validación de pólizas.  
- Dashboard básico.

---

## 🛠️ Tecnologías y Arquitectura

### ⚡ **Clean Architecture**
Separación en capas para lograr un código mantenible, escalable y testeable:

- **Domain:** Casos de uso, modelos de negocio, repositorios.
- **Data:** Implementaciones de repositorios, control de fuentes (API), DTOs.
- **Presentation:** ViewModels, Intents, States (MVI), pantallas UI.

---

### 🔄 **MVI (Model–View–Intent)**

Patrón utilizado para lograr:

- Flujo de datos unidireccional.
- Estados inmutables.
- Manejo claro de errores y efectos secundarios.
- UI totalmente reactiva.

## 🤝 Contribuciones

¡Contribuciones y sugerencias son bienvenidas!  
Haz un **fork**, crea un PR y estaré encantado de revisarlo.

###  Autor
**Erick Estrada Rosario**


