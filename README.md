# Nicopoly

Aplicación Android para la consulta y gestión de información de productos y stock de Nicopoly.

## Descripción

**Nicopoly** es una aplicación Android desarrollada para facilitar la consulta rápida de información de productos, precios y disponibilidad de stock.

La aplicación permite buscar productos mediante su código/SKU y consultar de manera centralizada la información correspondiente a las distintas bodegas y puntos de venta.

La versión **2.2** moderniza la arquitectura de red migrando de un intermediario (Google Apps Script) a una conexión **directa** y segura con la **Google Sheets API v4** utilizando una Cuenta de Servicio (Service Account), mejorando el rendimiento de sincronización de datos y enriqueciendo la interfaz de usuario con encabezados fijos (Sticky Headers).

## Funcionalidades principales

- 🔎 Búsqueda de productos mediante SKU o código de producto.
- 📦 Consulta de stock por ubicación.
- 🏬 Visualización diferenciada del stock de las distintas bodegas y tiendas.
- 💰 Consulta de precios de venta y precios mayoristas.
- 📊 Visualización de información detallada del producto.
- 🌐 Sincronización directa y segura con hojas de cálculo privadas de Google Sheets.
- 💾 Almacenamiento local de la información mediante Room.
- 🌙 Compatibilidad con modo claro y modo oscuro de Android.
- ⚡ Consulta rápida de información almacenada localmente.
- 📌 Interfaz optimizada con "Sticky Headers" para no perder de vista los títulos al scrollear tablas largas de stock.

## Actualización de información

La aplicación utiliza una arquitectura de actualización que permite obtener la información actualizada directamente desde los servidores de Google utilizando credenciales de servicio, garantizando la privacidad de los datos internos.

El flujo general de información es:

``text
Hoja de cálculo privada (Google Sheets)
          ↓
Google Sheets API v4 (Service Account)
          ↓
Aplicación Android (batchGet de pestañas Reposicion y Ubicaciones)
          ↓
Procesamiento de datos y cruce de inventario/ubicaciones
          ↓
Base de datos Room
          ↓
Consulta de productos y stock
          ↓
Interfaz de usuario (Jetpack Compose)
``

Al seleccionar **"Actualizar información"**, la aplicación solicita los datos actualizados mediante Internet, procesa la información recibida (fusionando el stock y las descripciones de ubicaciones físicas) y actualiza la base de datos local.

Una vez completada la actualización, las consultas realizadas por el usuario utilizan la información almacenada localmente.

> **Nota de seguridad:** Las credenciales de la Service Account (credentials.json) y cualquier información de configuración sensible no se documentan públicamente en este repositorio y deben ser inyectadas localmente antes de compilar.

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

- Jetpack Compose (Incluyendo stickyHeader y Foundation API)
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
- Servicios de Google API

### Persistencia

- Room Database

Room permite almacenar localmente la información obtenida desde Internet para que las consultas de productos puedan realizarse de forma rápida y sin depender constantemente de una conexión de red.

### Comunicación con Internet

- Google API Client Library for Java
- Google Sheets API v4
- Google Auth Library

La aplicación utiliza estos componentes oficiales de Google para comunicarse directamente con la hoja de cálculo de la empresa de forma segura utilizando una Cuenta de Servicio, y extraer los datos masivamente mediante operaciones atchGet.

### Inyección de dependencias

- Hilt

Hilt se utiliza para administrar las dependencias de los diferentes componentes de la aplicación.

## Arquitectura general

La arquitectura puede representarse de forma simplificada de la siguiente manera:

``text
                    ┌─────────────────────┐
                    │  Google Sheets      │
                    │  (Hoja Privada)     │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │ Google Sheets API   │
                    │  (Service Account)  │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │ GoogleSheetsService │
                    │     (batchGet)      │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │    ExcelImporter    │
                    │ (Procesamiento)     │
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
``

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

``text
Usuario
  │
  │ "Actualizar información"
  ▼
Aplicación
  │
  ▼
GoogleSheetsService
  │
  ▼
Google Sheets API v4
  │
  ▼
Datos de Pestañas (batchGet)
  │
  ▼
Conversión de datos
  │
  ▼
Room
  │
  ▼
Información actualizada
``

## Requisitos de desarrollo

Para trabajar con el proyecto se requiere un entorno de desarrollo Android compatible con el proyecto.

Se recomienda utilizar:

- Android Studio
- JDK 17 (Integrado en Android Studio modernos)
- Android SDK correspondiente a la configuración del proyecto
- Gradle Wrapper incluido en el repositorio

El proyecto incluye gradlew y gradlew.bat, por lo que se recomienda utilizar el Gradle Wrapper incluido en lugar de depender de una instalación global de Gradle.

## Instalación

### 1. Clonar el repositorio

``bash
git clone https://github.com/elopeznanda/NicopolyApp.git
``

### 2. Abrir el proyecto

Abrir la carpeta del proyecto desde Android Studio.

### 3. Configurar credenciales y entorno

- Para que el proyecto pueda conectarse con Google Sheets, debes agregar tu archivo credentials.json en la ruta:
  pp/src/main/res/raw/credentials.json
- El archivo local.properties es específico de cada equipo y **no forma parte del repositorio**.

### 4. Sincronizar el proyecto

Permitir que Android Studio sincronice Gradle y descargue las dependencias necesarias.

### 5. Ejecutar la aplicación

Seleccionar un dispositivo físico o emulador Android y ejecutar la aplicación desde Android Studio.

## Estructura general del proyecto

``text
NicopolyApp/
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
│               └── raw/
│                   └── credentials.json (ignorado)
│
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── gradlew
├── gradlew.bat
├── .gitignore
└── README.md
``

## Control de versiones

El proyecto utiliza Git para mantener un historial de cambios y permitir la recuperación de versiones anteriores.

La versión actual documentada en este repositorio es:

**Nicopoly 2.2**

Se recomienda mantener cada versión estable identificada mediante tags de Git.

## Seguridad y configuración

Los archivos de configuración específicos de cada equipo no deben almacenarse en el repositorio.

Entre los archivos excluidos mediante .gitignore se encuentran configuraciones locales y archivos generados por Android Studio y Gradle.

No se deben almacenar en Git:

- **pp/src/main/res/raw/credentials.json** (Llaves de la cuenta de servicio de Google Sheets).
- Contraseñas.
- Tokens privados.
- Claves privadas.
- Keystores (ej. 
icopoly_key.jks).
- Credenciales.
- Configuraciones locales.
- Información confidencial de la empresa.

La configuración del servicio de datos debe mantenerse de acuerdo con las políticas de seguridad de la empresa.

## Estado del proyecto

**Versión:** 2.2  
**Estado:** Estable / Operativa

La versión 2.2 corresponde a una versión funcional de la aplicación con integración directa vía Google Sheets API, almacenamiento local mediante Room y mejoras en la Interfaz de Usuario (Sticky Headers).

## Mantenimiento

Para realizar modificaciones en el proyecto se recomienda:

1. Crear una nueva rama para cambios importantes.
2. Realizar cambios pequeños y controlados.
3. Probar la aplicación en Android Studio.
4. Confirmar que la aplicación continúa funcionando correctamente.
5. Crear un commit descriptivo.
6. Fusionar los cambios a main cuando estén validados.
7. Crear un nuevo tag para cada versión estable.

## Historial de versiones

| Versión | Estado | Descripción |
|---|---|---|
| 2.2 | Estable | Integración directa con Google Sheets API (Service Account) y UI Sticky Header |
| 2.1 | Estable | Actualización de información mediante Internet (Google Apps Script) |

## Licencia

Este proyecto es propiedad de Nicopoly.

El código fuente, la aplicación y los componentes desarrollados específicamente para este proyecto no deben ser redistribuidos ni utilizados fuera de los términos autorizados por la empresa.

---

**Nicopoly — Aplicación Android de consulta de productos y stock**

Versión 2.2
