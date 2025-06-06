# TastyBuds 🍔 - Food Delivery Android App

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-blue.svg)](https://kotlinlang.org)
[![Android](https://img.shields.io/badge/Android-API%2024+-green.svg)](https://android.com)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-1.5.8-orange.svg)](https://developer.android.com/jetpack/compose)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

A modern, feature-rich food delivery Android application built with Jetpack Compose and following Clean Architecture principles. TastyBuds offers a seamless food ordering experience with restaurant discovery, real-time tracking, and intuitive user interface.

## 📱 Features

### Core Features
- **Restaurant Discovery**: Browse restaurants with categories, filters, and search functionality
- **Menu Browsing**: Detailed food items with customization options (size, toppings, spice level)
- **Order Management**: Add to cart, order review, and checkout process
- **Real-time Tracking**: Live order tracking with map integration
- **User Profile**: Profile management and preferences
- **Location Services**: GPS-based location selection and address management
- **Payment Integration**: Multiple payment methods support
- **Rating System**: Rate drivers and restaurants

### UI/UX Features
- **Modern Design**: Material 3 design system with custom orange theme
- **Dark Mode Support**: Toggle between light and dark themes
- **Responsive Layout**: Optimized for different screen sizes
- **Smooth Animations**: Engaging micro-interactions and transitions
- **Search Functionality**: Real-time search with suggestions
- **Interactive Maps**: Google Maps integration for location and tracking

## 🏗️ Architecture

TastyBuds follows **Clean Architecture** with MVVM pattern, ensuring separation of concerns and maintainability.

```
┌─────────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                        │
│  ┌─────────────────┐  ┌─────────────────┐  ┌──────────────┐ │
│  │   UI (Compose)  │  │   ViewModels    │  │  UI States   │ │
│  │                 │  │                 │  │              │ │
│  │ • HomeScreen    │  │ • HomeViewModel │  │ • HomeUiState│ │
│  │ • OrderScreen   │  │ • ProfileVM     │  │ • Loading    │ │
│  │ • ProfileScreen │  │                 │  │ • Success    │ │
│  └─────────────────┘  └─────────────────┘  │ • Error      │ │
│                                            └──────────────┘ │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                     DOMAIN LAYER                            │
│  ┌─────────────────┐  ┌─────────────────┐  ┌──────────────┐ │
│  │   Use Cases     │  │   Domain Models │  │ Repositories │ │
│  │                 │  │                 │  │ (Interfaces) │ │
│  │ • HomeUseCase   │  │ • Restaurant    │  │ • HomeRepo   │ │
│  │ • ProfileUseCase│  │ • Category      │  │ • ProfileRepo│ │
│  │                 │  │ • Banner        │  │              │ │
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
│  │• ProfileRepoImpl│  │ • Local Data    │  │ • Supabase   │ │
│  │                 │  │                 │  │ • OkHttp     │ │
│  └─────────────────┘  └─────────────────┘  └──────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

## 🛠️ Tech Stack

### Core Technologies
- **Language**: Kotlin 100%
- **UI Framework**: Jetpack Compose 1.5.8
- **Architecture**: MVVM + Clean Architecture
- **Dependency Injection**: Hilt (Dagger)
- **Build System**: Gradle with Kotlin DSL

### Jetpack Components
- **Navigation**: Navigation Compose
- **Lifecycle**: ViewModel, LiveData
- **Reactive Programming**: Kotlin Coroutines + Flow
- **State Management**: Compose State

### Networking & Data
- **HTTP Client**: Retrofit + OkHttp
- **JSON Parsing**: Moshi with Kotlin adapters
- **Backend**: Supabase (PostgreSQL)
- **Image Loading**: Glide with Compose integration

### Maps & Location
- **Maps**: Google Maps Compose
- **Location**: Google Play Services Location
- **Permissions**: Accompanist Permissions

### UI & Design
- **Design System**: Material 3
- **Icons**: Custom vector drawables
- **Fonts**: Poppins, Inter
- **Theme**: Custom orange primary color (#FF7700)
- **System UI**: Accompanist System UI Controller

### Testing
- **Unit Testing**: JUnit 5, MockK
- **Coroutines Testing**: Kotlinx Coroutines Test
- **Flow Testing**: Turbine
- **UI Testing**: Compose Testing, Espresso

## 📊 Application Flow

### User Journey Flow
```mermaid
graph TD
    A[App Launch] --> B[Home Screen]
    B --> C{User Action}
    
    C -->|Search| D[Search Results]
    C -->|Browse Categories| E[Category Listing]
    C -->|Select Restaurant| F[Restaurant Details]
    C -->|Profile| G[Profile Screen]
    C -->|Location| H[Location Picker]
    
    D --> F
    E --> F
    F --> I[Food Details]
    I --> J[Add to Cart]
    J --> K[Order Review]
    K --> L[Payment]
    L --> M[Order Tracking]
    M --> N[Rating]
    
    G --> O[Edit Profile]
    H --> P[Confirm Location]
    P --> B
```

### Screen Navigation Structure
```
MainActivity
├── HomeScreen (Bottom Nav)
│   ├── SearchResultsScreen
│   ├── CategoryDetailsScreen (Food Listing)
│   └── RestaurantDetailsScreen
│       └── FoodDetailsScreen
│           └── OrderReviewScreen
│               └── OrderTrackingScreen
│                   └── RatingScreen
├── OrdersScreen (Bottom Nav)
├── FavoritesScreen (Bottom Nav)
├── InboxScreen (Bottom Nav)
├── ProfileScreen
└── LocationTrackerScreen
```

## 🔧 Project Structure

```
app/src/main/java/com/app/tastybuds/
├── 📁 common/                 # Shared utilities and API interfaces
│   └── TastyBudsApiService.kt
├── 📁 data/                   # Data layer implementation
│   ├── model/                 # Data models and DTOs
│   ├── repo/                  # Repository implementations
│   └── ProfileApiService.kt
├── 📁 di/                     # Dependency injection modules
│   └── NetworkModule.kt
├── 📁 domain/                 # Business logic layer
│   ├── model/                 # Domain models
│   └── *UseCase.kt           # Use case implementations
├── 📁 ui/                     # Presentation layer
│   ├── checkout/             # Checkout related screens
│   ├── favorites/            # Favorites feature
│   ├── home/                 # Home and search screens
│   ├── inbox/                # Chat functionality
│   ├── location/             # Location services
│   ├── orders/               # Order management
│   ├── profile/              # User profile
│   └── theme/                # UI theming
├── 📁 util/                   # Utility classes and navigation
│   ├── AppNavGraph.kt
│   ├── AppTopBar.kt
│   └── HomeSearchBar.kt
└── MainActivity.kt
```

### Configuration

#### Backend Setup
The app uses Supabase as backend. Update the base URL and API keys in `NetworkModule.kt`:
```kotlin
private val baseUrl = "https://your-supabase-url.supabase.co/rest/v1/"
```

#### Database Schema
The app expects the following Supabase tables:
- `banners` - Promotional banners
- `categories` - Food categories
- `restaurants` - Restaurant information
- `collections` - Curated collections
- `sale_items` - Deals and offers
- `vouchers` - User vouchers

## 🔗 API Integration

### Authentication Headers
```kotlin
.addHeader("apikey", "YOUR_SUPABASE_ANON_KEY")
.addHeader("Authorization", "Bearer YOUR_SUPABASE_ANON_KEY")
```

## 🙏 Acknowledgments

- [Jetpack Compose](https://developer.android.com/jetpack/compose) for modern UI toolkit
- [Material Design](https://material.io/) for design guidelines
- [Supabase](https://supabase.io/) for backend services
- [Google Maps](https://developers.google.com/maps) for mapping services

---

<div align="center">
Made with ❤️ for food lovers everywhere
</div>
