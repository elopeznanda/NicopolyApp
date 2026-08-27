# Nicopoly

Aplicación Android para la consulta y gestión de información de productos y stock de Nicopoly.

## Descripción

**Nicopoly** es una aplicación Android desarrollada para facilitar la consulta rápida de información de productos, precios y disponibilidad de stock.

La aplicación permite buscar productos mediante su código/SKU y consultar de manera centralizada la información correspondiente a las distintas bodegas y puntos de venta.

La versión **2.1** incorpora la actualización de información mediante Internet, permitiendo obtener los datos directamente desde la fuente de información de la empresa sin depender de una actualización manual mediante archivos Excel.

## Funcionalidades principales

- 🔎 Búsqueda de productos mediante SKU o código de producto.
- 📦 Consulta de stock por ubicación.
- 🏬 Visualización diferenciada del stock de las distintas bodegas y tiendas.
- 💰 Consulta de precios de venta y precios mayoristas.
- 📊 Visualización de información detallada del producto.
- 🌐 Actualización de información mediante Internet.
- 💾 Almacenamiento local de la información mediante Room.
- 🌙 Compatibilidad con modo claro y modo oscuro de Android.
- ⚡ Consulta rápida de información almacenada localmente.
- 🔄 Actualización de la base de datos local a partir de la información obtenida desde Internet.

## Actualización de información

La aplicación utiliza una arquitectura de actualización que permite obtener la información actualizada desde Internet.

El flujo general de información es:

```text
Fuente de datos de la empresa
          ↓
Google Apps Script / API
          ↓
Aplicación Android
          ↓
Procesamiento de datos
          ↓
Base de datos Room
          ↓
Consulta de productos y stock
          ↓
Interfaz de usuario
```

Al seleccionar **"Actualizar información"**, la aplicación solicita los datos actualizados mediante Internet, procesa la información recibida y actualiza la base de datos local.

Una vez completada la actualización, las consultas realizadas por el usuario utilizan la información almacenada localmente.

> La URL del servicio de datos y cualquier información de configuración sensible no se documentan públicamente en este archivo.

## Información mostrada

La aplicación trabaja con información asociada a los productos, incluyendo, entre otros:

- Código de producto.
- Descripción.
- Categoría.
- Temporada.
- Precios.
- Stock por ubicación.
- Información de bodega.
- Información de tiendas.
- Código de variante cuando corresponde.
- Ubicación del producto cuando está disponible.

### Ubicaciones de stock

La aplicación distingue las diferentes ubicaciones de inventario.

Entre las ubicaciones utilizadas por la aplicación se encuentran:

| Ubicación | Descripción |
|---|---|
| Bodega | Stock disponible en la bodega principal |
| P1 | Stock correspondiente a T003 |
| F | Stock correspondiente a T009 |
| P2 | Stock correspondiente a T012 |
| Online | Stock correspondiente a T060 |

La aplicación mantiene estas ubicaciones diferenciadas para evitar mezclar los valores de stock entre bodegas y tiendas.

## Tecnologías

Nicopoly está desarrollada utilizando tecnologías modernas del ecosistema Android.

### Lenguaje

- Kotlin

### Interfaz

- Jetpack Compose
- Material Design 3

### Arquitectura

La aplicación utiliza una arquitectura organizada por capas, separando responsabilidades entre:

- Presentation
- Domain
- Data

Entre los componentes utilizados se encuentran:

- ViewModels
- Repositories
- DTOs
- Entidades de base de datos
- Servicios de API

### Persistencia

- Room Database

Room permite almacenar localmente la información obtenida desde Internet para que las consultas de productos puedan realizarse de forma rápida y sin depender constantemente de una conexión de red.

### Comunicación con Internet

- Retrofit
- OkHttp
- Gson

La aplicación utiliza estos componentes para comunicarse con el servicio de datos y convertir la información recibida en objetos utilizados internamente por la aplicación.

### Inyección de dependencias

- Hilt

Hilt se utiliza para administrar las dependencias de los diferentes componentes de la aplicación.

## Arquitectura general

La arquitectura puede representarse de forma simplificada de la siguiente manera:

```text
                    ┌─────────────────────┐
                    │ Fuente de datos     │
                    │ de la empresa       │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │ Google Apps Script  │
                    │       / API         │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │     Retrofit        │
                    │       + OkHttp      │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │   ExcelImporter /   │
                    │ procesamiento API   │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │    Room Database    │
                    │    almacenamiento   │
                    │       local         │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │    Repository       │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │     ViewModel       │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │   Jetpack Compose   │
                    │   Interfaz de UI    │
                    └─────────────────────┘
```

## Base de datos local

La información obtenida mediante la actualización por Internet se almacena en una base de datos local utilizando Room.

Esto permite separar:

**Obtención de información**

de

**Consulta de información**

Una vez actualizada la base de datos, el usuario puede realizar búsquedas y consultas utilizando los datos almacenados localmente.

La actualización de información reemplaza la información anterior por los datos obtenidos de la fuente actualizada.

## Flujo de actualización

El proceso de actualización funciona de la siguiente manera:

```text
Usuario
  │
  │ "Actualizar información"
  ▼
Aplicación
  │
  ▼
Solicitud HTTP
  │
  ▼
API
  │
  ▼
Datos JSON
  │
  ▼
Conversión de datos
  │
  ▼
Room
  │
  ▼
Información actualizada
```

## Requisitos de desarrollo

Para trabajar con el proyecto se requiere un entorno de desarrollo Android compatible con el proyecto.

Se recomienda utilizar:

- Android Studio
- JDK compatible con la configuración Gradle del proyecto
- Android SDK correspondiente a la configuración del proyecto
- Gradle Wrapper incluido en el repositorio

El proyecto incluye `gradlew` y `gradlew.bat`, por lo que se recomienda utilizar el Gradle Wrapper incluido en lugar de depender de una instalación global de Gradle.

## Instalación

### 1. Clonar el repositorio

```bash
git clone https://github.com/leonardonarocl/Nicopoly.git
```

### 2. Abrir el proyecto

Abrir la carpeta `Nicopoly` desde Android Studio.

### 3. Configurar el entorno Android

Android Studio debe tener instalado el SDK requerido por el proyecto.

El archivo `local.properties` es específico de cada equipo y **no forma parte del repositorio**.

### 4. Sincronizar el proyecto

Permitir que Android Studio sincronice Gradle y descargue las dependencias necesarias.

### 5. Ejecutar la aplicación

Seleccionar un dispositivo físico o emulador Android y ejecutar la aplicación desde Android Studio.

## Estructura general del proyecto

```text
Nicopoly/
│
├── app/
│   └── src/
│       └── main/
│           ├── java/
│           │   └── com/nicopoly/app/
│           │       ├── data/
│           │       ├── domain/
│           │       └── presentation/
│           │
│           └── res/
│
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── gradlew
├── gradlew.bat
├── .gitignore
└── README.md
```

## Control de versiones

El proyecto utiliza Git para mantener un historial de cambios y permitir la recuperación de versiones anteriores.

La versión actual documentada en este repositorio es:

**Nicopoly 2.1**

Se recomienda mantener cada versión estable identificada mediante tags de Git.

Ejemplo:

```text
v2.1
v2.2
v2.3
v3.0
```

Esto permite identificar claramente las versiones utilizadas y recuperar una versión anterior cuando sea necesario.

## Seguridad y configuración

Los archivos de configuración específicos de cada equipo no deben almacenarse en el repositorio.

Entre los archivos excluidos mediante `.gitignore` se encuentran configuraciones locales y archivos generados por Android Studio y Gradle.

No se deben almacenar en Git:

- Contraseñas.
- Tokens privados.
- Claves privadas.
- Keystores.
- Credenciales.
- Configuraciones locales.
- Información confidencial de la empresa.

La configuración del servicio de datos debe mantenerse de acuerdo con las políticas de seguridad de la empresa.

## Estado del proyecto

**Versión:** 2.1  
**Estado:** Estable / Operativa

La versión 2.1 corresponde a una versión funcional de la aplicación con actualización de información mediante Internet y almacenamiento local mediante Room.

## Mantenimiento

Para realizar modificaciones en el proyecto se recomienda:

1. Crear una nueva rama para cambios importantes.
2. Realizar cambios pequeños y controlados.
3. Probar la aplicación en Android Studio.
4. Confirmar que la aplicación continúa funcionando correctamente.
5. Crear un commit descriptivo.
6. Fusionar los cambios a `main` cuando estén validados.
7. Crear un nuevo tag para cada versión estable.

## Historial de versiones

| Versión | Estado | Descripción |
|---|---|---|
| 2.1 | Estable | Versión actual del proyecto |

## Licencia

Este proyecto es propiedad de Nicopoly.

El código fuente, la aplicación y los componentes desarrollados específicamente para este proyecto no deben ser redistribuidos ni utilizados fuera de los términos autorizados por la empresa.

---

**Nicopoly — Aplicación Android de consulta de productos y stock**

Versión 2.1