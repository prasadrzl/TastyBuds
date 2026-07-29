# TastyBuds 🍔 - Food Delivery Android App

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.22-blue.svg)](https://kotlinlang.org)
[![Android](https://img.shields.io/badge/Android-API%2024+-green.svg)](https://android.com)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-2024.02.00-orange.svg)](https://developer.android.com/jetpack/compose)
[![AGP](https://img.shields.io/badge/AGP-8.5.2-green.svg)](https://developer.android.com/studio/releases/gradle-plugin)

A modern, feature-rich food delivery Android application built with Jetpack Compose and following Clean Architecture principles. TastyBuds offers a seamless food ordering experience with restaurant discovery, real-time tracking, secure data storage, and intuitive user interface.

## 📱 Features

### Core Features
- **Restaurant Discovery**: Browse restaurants with categories, filters, and search functionality
- **Menu Browsing**: Detailed food items with customization options (size, toppings, spice level)
- **Cart Management**: Edit cart items, save customizations, and manage quantities
- **Order Management**: Complete checkout process, order review, and order history
- **User Profile**: Profile management and preferences with secure data storage
- **Favorites**: Save favorite restaurants and menu items
- **Vouchers & Deals**: Access to promotional offers and discounts

### UI/UX Features
- **Modern Design**: Material 3 design system with custom orange theme
- **Dark Mode Support**: Toggle between light and dark themes
- **Responsive Layout**: Optimized for different screen sizes
- **Smooth Animations**: Engaging micro-interactions and transitions
- **Search Functionality**: Real-time search with suggestions and filters
- **Interactive Maps**: Google Maps integration for location and tracking
- **Extended Color System**: Custom color palette for enhanced UI


## 📸 Screenshots

### ☀️ Light Mode Screenshots 
| Home Screen | Category Details | Resturant Details | Food Details |
|-------------|-------------------|--------------|--------------|
| <img src="https://github.com/user-attachments/assets/a62ec62e-44e6-4c4f-956b-0ae44774714e" width="300"/> | <img src="https://github.com/user-attachments/assets/32f5781c-2afe-41ef-86f6-de68223b78c1" width="300"/> | <img src="https://github.com/user-attachments/assets/94bf8692-bd0e-4c19-affa-7c11100ff09f" width="300"/> |<img src="https://github.com/user-attachments/assets/72184e36-01b3-4e03-a889-2415bf2facde" width="300"/> |



### 🌙 Dark Mode Screenshots
| Home Screen | My Orders | Search |
|---------------|---------------|---------------|
| <img src="https://github.com/user-attachments/assets/813753cf-97c2-4a60-93b5-cf9d616fb803" width="300"/> | <img src="https://github.com/user-attachments/assets/00925d2a-b1a8-404c-8e57-6f003b4aa261" width="300"/> |<img src="https://github.com/user-attachments/assets/0ab38c57-03ff-4a9e-833b-66b0ff6e0016" width="300"/> |


## 🏗️ Architecture

TastyBuds is a **multi-module** app following **Clean Architecture** + MVVM. `:app` is a thin shell (~11 files: `Application`, `MainActivity`, navigation, splash); every feature is its own Gradle module, and shared code lives in three `:core` modules.

> Visual diagrams: [`docs/module-architecture.html`](docs/module-architecture.html) (module dependency graph) and [`docs/clean-architecture-flow.html`](docs/clean-architecture-flow.html) (request/response flow for a feature).

### Module structure

| Module | Type | Responsibility |
|---|---|---|
| `:app` | app | Application, `MainActivity`, navigation graph, splash — orchestration only |
| `:core:common` | **pure Kotlin/JVM** | Domain layer — models, use cases, repository **interfaces**, `Result`. No Android, no Moshi. |
| `:core:data` | android lib | Data layer — Retrofit API service, repository **implementations**, DTOs + mappers, DI modules, `DataStore`/secure storage |
| `:core:ui` | android lib | Shared UI — theme, fonts, common components (`LoadingScreen`, `ErrorScreen`, `AppTopBar`), `ThemeManager`, shared `CartViewModel` |
| `:feature:home` `:feature:restaurants` `:feature:orders` `:feature:favorites` `:feature:account` `:feature:onboarding` | android feature libs | Self-contained feature slices (screens, ViewModels, feature-only resources) |
| `:baselineprofile` | test | Generates the startup Baseline Profile + macrobenchmark |

### Dependency rules (enforced)

- **Downward only:** `:app` → `:feature:*` → `:core:*`. Dependencies never point sideways or up.
- **Zero feature→feature dependencies** — features share only through `:core`.
- **Domain is pure:** `:core:common` depends on nothing (no Android, no Moshi). DTOs live in `:core:data`; the domain exposes only domain models and repository interfaces.
- **Dependency inversion:** each `RepositoryImpl` (data) implements a repository interface (domain); Hilt binds them, so ViewModels never see Retrofit/Moshi.

### Layered flow (every feature follows this)

```
UI (Compose) → ViewModel → UseCase → Repository«interface» → RepositoryImpl → ApiService → backend
        ▲ StateFlow / UI model      (domain, :core:common)   (data, :core:data)
```
Data crosses each boundary in the right shape: **DTO** (`FavoriteResponse`) → `.toDomain()` → **domain model** (`FavoriteRestaurant`, wrapped in `Result`) → `.toUi()` → **UI model** (`FavoriteRestaurantUi`).

```
┌─────────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                        │
│  ┌─────────────────┐  ┌─────────────────┐  ┌──────────────┐ │
│  │   UI (Compose)  │  │   ViewModels    │  │  UI States   │ │
│  │                 │  │                 │  │              │ │
│  │ • HomeScreen    │  │ • HomeViewModel │  │ • HomeUiState│ │
│  │ • OrderScreen   │  │ • CartViewModel │  │ • Loading    │ │
│  │ • ProfileScreen │  │ • ProfileVM     │  │ • Success    │ │
│  │ • AuthScreens   │  │ • AuthViewModel │  │ • Error      │ │
│  └─────────────────┘  └─────────────────┘  └──────────────┘ │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                     DOMAIN LAYER                            │
│  ┌─────────────────┐  ┌─────────────────┐  ┌──────────────┐ │
│  │   Use Cases     │  │   Domain Models │  │ Repositories │ │
│  │                 │  │                 │  │ (Interfaces) │ │
│  │ • HomeUseCase   │  │ • Restaurant    │  │ • HomeRepo   │ │
│  │ • CartUseCase   │  │ • Category      │  │ • CartRepo   │ │
│  │ • ProfileUseCase│  │ • Banner        │  │ • ProfileRepo│ │
│  │ • AuthUseCase   │  │ • MenuItems     │  │ • AuthRepo   │ │
│  └─────────────────┘  └─────────────────┘  └──────────────┘ │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                      DATA LAYER                             │
│  ┌─────────────────┐  ┌─────────────────┐  ┌──────────────┐ │
│  │ Repository Impl │  │   Data Sources  │  │ API Services │ │
│  │                 │  │                 │  │              │ │
│  │• HomeRepoImpl   │  │ • Remote Data   │  │ • Retrofit   │ │
│  │• CartRepoImpl   │  │ • Local Storage │  │ • Spring Boot│ │
│  │• ProfileRepoImpl│  │ • Secure Prefs  │  │ • OkHttp     │ │
│  │• AuthRepoImpl   │  │ • DataStore     │  │ • Moshi      │ │
│  └─────────────────┘  └─────────────────┘  └──────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

## 🛠️ Tech Stack

### Core Technologies
- **Language**: Kotlin 100%
- **UI Framework**: Jetpack Compose BOM 2024.02.00
- **Architecture**: Multi-module + Clean Architecture + MVVM
- **Dependency Injection**: Hilt (Dagger) 2.48
- **Build System**: Gradle with Kotlin DSL (AGP 8.5.2), version catalog + `build-logic` convention plugins
- **Performance**: Baseline Profiles + Macrobenchmark (`:baselineprofile`, Gradle Managed Device)

### Jetpack Components
- **Navigation**: Navigation Compose 2.7.7
- **Lifecycle**: ViewModel, LiveData 2.8.4
- **Reactive Programming**: Kotlin Coroutines + Flow 1.8.0
- **State Management**: Compose State + Runtime Compose 2.8.6
- **Data Storage**: DataStore Preferences 1.0.0
- **Security**: Security Crypto 1.1.0-alpha06

### Networking & Data
- **HTTP Client**: Retrofit 2.11.0 + OkHttp 4.12.0
- **JSON Parsing**: Moshi 1.15.0 with Kotlin adapters (in `:core:data`)
- **Backend**: Self-hosted **Spring Boot** REST API over **PostgreSQL** (runs locally via Docker; migrated off Supabase)
- **Base URL**: configured in `config.properties` → `BuildConfig.API_BASE_URL` (default `http://10.0.2.2:8080/api/v1/` for the emulator) — no hardcoded URLs in source
- **Image Loading**: Glide 4.16.0 with Compose integration

### Maps & Location
- **Maps**: Google Maps Compose 4.3.3
- **Location**: Google Play Services Location 21.3.0
- **Permissions**: Accompanist Permissions 0.32.0
- **Coroutines Integration**: Play Services 1.8.0

### UI & Design
- **Design System**: Material 3
- **Icons**: Custom vector drawables + Material Icons
- **Fonts**: Poppins, Inter
- **Theme**: Custom orange primary color (#FF7700)
- **System UI**: Accompanist System UI Controller 0.32.0
- **Refresh**: Accompanist Swipe Refresh 0.32.0

### Testing & Development (WIP)
- **Unit Testing**: JUnit 5.10.0, MockK 1.13.10
- **Coroutines Testing**: Kotlinx Coroutines Test 1.8.0
- **Flow Testing**: Turbine 1.1.0
- **UI Testing**: Compose Testing, Espresso 3.6.1
- **Android Testing**: JUnit 1.1.5

## 📊 Application Flow

### User Journey Flow
```mermaid
graph TD
    A[App Launch] --> B{User Authenticated?}
    B -->|No| C[Onboarding/Login]
    B -->|Yes| D[Home Screen]
    
    C --> E[Login/Register]
    E --> D
    
    D --> F{User Action}
    
    F -->|Search| G[Search Results]
    F -->|Browse Categories| H[Category Listing]
    F -->|Select Restaurant| I[Restaurant Details]
    F -->|Profile| J[Profile Screen]
    F -->|Location| K[Location Picker]
    F -->|View Cart| L[Cart Screen]
    
    G --> I
    H --> I
    I --> M[Food Details]
    M --> N[Customize & Add to Cart]
    N --> L
    L --> O[Order Review]
    O --> P[Payment]
    P --> Q[Order Tracking]
    Q --> R[Rating & Review]
    
    J --> S[Edit Profile/Settings]
    K --> T[Confirm Location]
    T --> D
```
#### **Build Configuration**
```kotlin
android {
    compileSdk = 35
    targetSdk = 35
    minSdk = 24
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
```
> Requires **JDK 17** to build (Gradle 8.7 / Kotlin 1.9.22 toolchain).

### Network Configuration

App config is externalized in `config.properties` (repo root), read in `:core:data/build.gradle.kts` and surfaced via `BuildConfig` — no hardcoded URLs or timeouts in source:

```properties
# config.properties
API_BASE_URL=http://10.0.2.2:8080/api/v1/   # 10.0.2.2 = emulator's alias for host localhost
NETWORK_TIMEOUT_SECONDS=30
```
```kotlin
// core/data/build.gradle.kts
buildConfigField("String", "API_BASE_URL", "\"${config("API_BASE_URL", ...)}\"")
buildConfigField("long", "NETWORK_TIMEOUT_SECONDS", "${config("NETWORK_TIMEOUT_SECONDS", "30")}L")

// NetworkModule.kt (:core:data)
Retrofit.Builder().baseUrl(BuildConfig.API_BASE_URL) …
```
Point it at a physical device or a deployed backend by editing `config.properties` (e.g. `API_BASE_URL=http://192.168.1.20:8080/api/v1/`) — no code change.

### Installation

**1. Start the local backend** (Spring Boot API + PostgreSQL):
```bash
cd db && docker compose up -d          # PostgreSQL, seeded from the schema dump
# then run the Spring Boot API (tastybuds-api) on :8080
```

**2. Configure the app**:
```bash
echo "MAPS_API_KEY=your_maps_api_key" >> local.properties   # Google Maps key
# API base URL is in config.properties (defaults to the emulator alias)
```

**3. Build / run**:
```bash
./gradlew :app:assembleDebug     # APK
./gradlew :app:bundleRelease     # AAB (Play)
```

## 📱 Key Features Implementation

### Authentication Flow
- **Onboarding**: First-time user experience
- **Login/Register**: Email-based authentication

### Cart Management
- **Add to Cart**: Customizable food items with size, toppings, spice level
- **Edit Items**: Modify existing cart items
- **Quantity Control**: Increase/decrease item quantities

### Search & Discovery
- **Real-time Search**: Search restaurants and menu items
- **Category Filtering**: Browse by food categories
- **Popular Items**: Featured and trending foods
- **Location-based**: Nearby restaurants

### Order Tracking
- **Map Integration**: Visual tracking on Google Maps
- **Delivery Estimation**: Accurate delivery time predictions

## 🙏 Acknowledgments

- [Jetpack Compose](https://developer.android.com/jetpack/compose) - Modern toolkit for native UI
- [Material Design 3](https://m3.material.io/) - Design system and guidelines
- [Spring Boot](https://spring.io/projects/spring-boot) - Self-hosted REST backend over PostgreSQL
- [Google Maps](https://developers.google.com/maps) - Maps and location services
- [Hilt](https://dagger.dev/hilt/) - Dependency injection for Android
- [Retrofit](https://square.github.io/retrofit/) - Type-safe HTTP client
- [Glide](https://bumptech.github.io/glide/) - Image loading and caching
