# 🎬 Velvet

> Offline-first movie discovery app for Android, powered by the TMDB API.

Built as a portfolio project showcasing modular clean architecture, MVI with a custom `BaseViewModel`, an offline-first data layer and a modern Jetpack Compose UI.

---

## Screenshots

> _Coming soon_

---

## Features

- 🏠 **Home** — Trending carousel, popular, top-rated and upcoming sections
- 🔍 **Search** — Real-time search with debounce and genre filters
- 🎥 **Detail** — Full movie info, cast, trailer and similar movies
- ❤️ **Favorites** — Locally saved movies, available offline
- ⚙️ **Settings** — Light/dark theme and content region

---

## Architecture

The project follows **Clean Architecture** principles and is split into **feature modules**, each self-contained and independent from one another.

```
velvet/
├── app/                  # Entry point, NavGraph, BottomNav
├── core/
│   ├── core-common/      # Result<T>, DispatcherProvider, extensions
│   ├── core-domain/      # Models, Repository interfaces, UseCases (pure Kotlin)
│   ├── core-network/     # Retrofit, DTOs, mappers, RemoteMediator
│   ├── core-database/    # Room, entities, DAOs
│   └── core-ui/          # Theme, typography, shared Compose components
├── feature/
│   ├── feature-home/
│   ├── feature-search/
│   ├── feature-detail/
│   ├── feature-favorites/
│   └── feature-settings/
└── build-logic/          # Convention plugins for Gradle
```

### Key principles

- **Room as single source of truth** — the UI never observes the network directly
- **RemoteMediator** for paginated collections (Paging 3)
- **Fetch-and-cache** with `lastUpdated` for non-paginated data
- **MVI pattern** — all ViewModels extend a shared `BaseViewModel<UiState, UiIntent, UiEvent>`
- **Navigation 3** with type-safe routes via `@Serializable`
- **No `Dispatchers.IO` hardcoded** — `DispatcherProvider` injected everywhere for testability

---

## Tech Stack

| Area | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 + Dynamic Color |
| Architecture | Clean Architecture + MVI |
| Navigation | AndroidX Navigation 3 |
| DI | Hilt |
| Network | Retrofit 2 + OkHttp + Kotlinx Serialization |
| Database | Room |
| Paging | Paging 3 + RemoteMediator |
| Images | Coil 3 |
| Async | Coroutines + Flow |
| Build | Gradle Version Catalogs + Convention Plugins |

---

## Getting Started

### Prerequisites

- Android Studio Ladybug or later
- JDK 17
- A free [TMDB API key](https://www.themoviedb.org/settings/api)

### Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/subenoeva/velvet.git
   cd velvet
   ```

2. Add your TMDB API key to `local.properties`:
   ```properties
   TMDB_API_KEY=your_api_key_here
   ```

3. Build and run the project from Android Studio or:
   ```bash
   ./gradlew assembleDebug
   ```

---

## API

This app uses the [TMDB API](https://developer.themoviedb.org/docs). Content and images are provided by TMDB but the app is not endorsed or certified by TMDB.

---

## License

```
MIT License

Copyright (c) 2025 subenoeva

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
