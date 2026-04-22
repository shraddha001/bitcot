# Bitcot Product Browser

Android app built with modern architecture and Jetpack Compose to display products from a public API, filter by category, and view product details with an image carousel.

## Setup Instructions

1. Open the project in Android Studio (latest stable recommended).
2. Ensure Android SDK 36 and JDK 11 are installed.
3. Sync Gradle.
4. Run:
   - `./gradlew :app:assembleDebug` (macOS/Linux)
   - `gradlew.bat :app:assembleDebug` (Windows)
5. Launch the app on an emulator or physical Android device.

## Architecture

The app uses a clean, layered MVVM approach:

- **UI layer (Compose):**
  - Screens: `HomeScreen`, `ProductDetailScreen`
  - Navigation: `BitcotNavHost`
  - UI State classes managed with `StateFlow`
- **Presentation layer:**
  - `HomeViewModel` and `ProductDetailViewModel`
  - Handles loading, success, error, and retry logic
- **Domain layer:**
  - `Product` model
  - `ProductRepository` contract
- **Data layer:**
  - Retrofit API interface (`ProductApiService`)
  - DTOs + mappers
  - Repository implementation with lightweight in-memory caching
- **Dependency Injection:**
  - Hilt application setup + module-based provisioning

## Features Implemented

- Product list fetched from API
- Dynamic category filter from server response
- Filtering products on selected category
- Product cards with:
  - Thumbnail image
  - Title
  - Price
  - Discount percentage
  - Rating
  - Stock status indicator:
    - `> 50`: Available (Green)
    - `1..50`: Limited (Orange)
    - `0`: Unavailable (Red)
- Product detail screen:
  - Image carousel
  - Dot page indicator
  - Full product metadata (title, description, price, discount, rating, stock, brand, category)
- Error state handling with retry action

## Libraries Used

- **Jetpack Compose** - UI toolkit
- **Navigation Compose** - screen navigation
- **Hilt** - dependency injection
- **Retrofit + Moshi** - networking and JSON parsing
- **Kotlin Coroutines + StateFlow** - async and state management
- **Coil** - image loading in Compose
- **OkHttp Logging Interceptor** - network diagnostics

## API Endpoints

- Product list:
  - `https://api.freeapi.app/api/v1/public/randomproducts?page=1&limit=100`
- Product detail:
  - `https://api.freeapi.app/api/v1/public/randomproducts/{id}`
