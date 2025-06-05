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

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- JDK 8 or higher
- Android SDK API 24+
- Google Maps API Key

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/tastybuds-android.git
   cd tastybuds-android
   ```

2. **Setup API Keys**
   Create `local.properties` file in root directory:
   ```properties
   MAPS_API_KEY=your_google_maps_api_key_here
   ```

3. **Build and Run**
   ```bash
   ./gradlew assembleDebug
   ./gradlew installDebug
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

### Supabase Integration
```kotlin
// Example API call structure
@GET("restaurants")
suspend fun getRecommendedRestaurants(
    @Query("select") select: String = "*",
    @Query("is_open") isOpen: String = "eq.true",
    @Query("order") order: String = "rating.desc",
    @Query("limit") limit: String = "20"
): List<RestaurantResponse>
```

### Authentication Headers
```kotlin
.addHeader("apikey", "YOUR_SUPABASE_ANON_KEY")
.addHeader("Authorization", "Bearer YOUR_SUPABASE_ANON_KEY")
```

## 🧪 Testing

### Running Tests
```bash
# Unit tests
./gradlew test

# Instrumentation tests
./gradlew connectedAndroidTest
```

### Test Coverage
- **Unit Tests**: ViewModels, Use Cases, Repositories
- **Integration Tests**: API services, Database operations
- **UI Tests**: Navigation flows, User interactions

## 📱 Supported Devices

- **Minimum SDK**: API 24 (Android 7.0)
- **Target SDK**: API 35 (Android 15)
- **Screen Sizes**: Phone and Tablet
- **Orientation**: Portrait (primary), Landscape (supported)

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Code Style
- Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use meaningful commit messages
- Add unit tests for new features
- Update documentation as needed

## 📋 Roadmap

### Upcoming Features
- [ ] Push notifications for order updates
- [ ] Social login (Google, Facebook)
- [ ] Offline mode support
- [ ] Multiple language support
- [ ] Advanced filtering options
- [ ] Loyalty program integration
- [ ] Voice ordering
- [ ] AR menu visualization

### Performance Improvements
- [ ] Image caching optimization
- [ ] Database query optimization
- [ ] Background task management
- [ ] Memory usage optimization

## 🐛 Known Issues

- Search suggestions may have slight delay on slower devices
- Map rendering might be slower on devices with limited RAM
- Some custom fonts may not load properly on older Android versions

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👥 Authors

- **Your Name** - *Initial work* - [YourGitHub](https://github.com/yourusername)

## 🙏 Acknowledgments

- [Jetpack Compose](https://developer.android.com/jetpack/compose) for modern UI toolkit
- [Material Design](https://material.io/) for design guidelines
- [Supabase](https://supabase.io/) for backend services
- [Google Maps](https://developers.google.com/maps) for mapping services

## 📞 Support

For support, email support@tastybuds.com or create an issue in this repository.

---

<div align="center">
Made with ❤️ for food lovers everywhere
</div>
