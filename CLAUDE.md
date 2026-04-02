# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**Velvet** is an offline-first Android movie discovery app powered by the TMDB API. It uses Clean Architecture + MVI pattern across independent feature modules.

- **Package:** `com.subenoeva.velvet`
- **Min SDK:** 26 | **Target SDK:** 35
- **TMDB API key:** defined in `local.properties` as `TMDB_API_KEY=...` — exposed via `BuildConfig.TMDB_API_KEY`. Never hardcode it.

## Build Commands

```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Run all unit tests across all modules
./gradlew testAll

# Run tests for a specific module
./gradlew :core:core-domain:test
./gradlew :core:core-common:test

# Run a single test class
./gradlew :core:core-common:test --tests "com.subenoeva.velvet.core.common.presentation.BaseViewModelTest"
```

## Module Architecture

```
velvet/
├── app/                   # Entry point, NavGraph, BottomNav, app-level Hilt
├── core/
│   ├── core-common/       # Result<T>, DispatcherProvider, BaseViewModel
│   ├── core-domain/       # Domain models, MovieRepository interface, UseCases (pure Kotlin/Android library)
│   ├── core-network/      # Retrofit, DTOs, mappers, RemoteMediator
│   ├── core-database/     # Room, entities, DAOs
│   └── core-ui/           # VelvetTheme, shared Compose components
├── feature/               # feature-home, feature-search, feature-detail, feature-favorites, feature-settings
└── build-logic/           # Convention plugins (Gradle)
```

### Dependency Rules

| Module | Can depend on |
|--------|---------------|
| `app` | all `feature-*`, all `core-*` |
| `feature-*` | `core-domain`, `core-ui`, `core-common` |
| `feature-detail`, `feature-home` | + `core-network` |
| `feature-favorites` | + `core-database` |
| `core-domain` | `core-common` only |
| `core-network`, `core-database` | `core-common` only |
| `core-ui` | `core-common` only |

**No `feature-*` module knows about another `feature-*`.**

## Convention Plugins (build-logic)

Each module uses one of four convention plugins instead of configuring Gradle directly:

| Plugin alias | Used by |
|---|---|
| `velvet.android.application` | `app` |
| `velvet.android.library` | `core-*` modules |
| `velvet.android.feature` | `feature-*` modules — automatically adds Compose, Hilt, Navigation 3, `core-ui`, `core-domain`, `core-common` |
| `velvet.kotlin.library` | Pure Kotlin modules (JVM 21 toolchain) |

## MVI Pattern

All ViewModels extend `BaseViewModel<UiState, UiIntent, UiEvent>` from `core-common`.

- `sendIntent(intent)` — UI sends user actions into the ViewModel
- `handleIntent(intent)` — abstract suspend fun that each ViewModel implements
- `updateState { ... }` / `setState(newState)` — updates state
- `sendEvent(event)` — one-shot side effects (navigation, snackbars)

Each feature defines three sealed types: `*UiState` (data class), `*UiIntent` (sealed interface), `*UiEvent` (sealed interface).

## Use Cases

Two base classes in `core-domain`:

- `UseCase<P, R>` — suspend, single result
- `FlowUseCase<P, R>` — returns `Flow<R>`
- Use `NoParams` as the type parameter when no input is needed

## Offline-First Data Flow

```
UI  ←── Room (Flow) ←── Repository ←── RemoteMediator ──→ TMDB API
```

- UI **only** observes Room — never the network directly.
- Paginated lists (popular, top-rated, upcoming, search): Paging 3 + `RemoteMediator`.
- Non-paginated data (trending, detail, cast): `Flow` from Room + background refresh if `lastUpdated` exceeds TTL.
- `DispatcherProvider` is always injected for coroutine dispatchers — never use `Dispatchers.IO` directly.

## Navigation

Uses **AndroidX Navigation 3** (`androidx.navigation3`). Routes are `@Serializable` data objects/classes defined per feature in `*Routes.kt` files. The `NavDisplay` + `rememberNavBackStack` live in `app`.

## Result Type

`core-common` defines `sealed interface Result<out T>` with three states: `Success(data)`, `Error(exception, message?)`, `Loading`. Helpers: `getOrNull()`, `map { }`.

## TMDB API

- Base URL: `https://api.themoviedb.org/3/`
- Auth: header `Authorization: Bearer {TMDB_API_KEY}`
- Image base URL: `https://image.tmdb.org/t/p/` — sizes: `w500` (posters), `w780` (backdrops), `w185` (cast)

## Testing

- **Unit tests:** JUnit 4, MockK, Turbine (Flow testing), `coroutines-test`
- Tests live in `src/test/` inside each module
- `DispatcherProvider` makes coroutine dispatchers injectable — use `TestCoroutineDispatcher` / `UnconfinedTestDispatcher` in tests
