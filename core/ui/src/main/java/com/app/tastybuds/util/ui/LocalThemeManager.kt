package com.app.tastybuds.util.ui

import androidx.compose.runtime.compositionLocalOf

// Provides the app-wide ThemeManager to composables (e.g. dark-mode toggle in settings).
// Hosted in :core:ui so any feature can read it without depending on :app.
val LocalThemeManager = compositionLocalOf<ThemeManager> {
    error("ThemeManager not provided")
}
