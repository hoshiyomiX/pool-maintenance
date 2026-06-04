# ============================================================
# Pool Maintenance — ProGuard / R8 Rules
# ============================================================

# ── Kotlin ──────────────────────────────────────────────────
-dontwarn kotlin.**
-keep class kotlin.Metadata { *; }
-keepclassmembers class **$WhenMappings {
    <fields>;
}

# ── Room ────────────────────────────────────────────────────
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep class * extends androidx.room.Dao { <methods>; }
-dontwarn androidx.room.paging.**

# ── Hilt / Dagger ──────────────────────────────────────────
-dontwarn dagger.hilt.**
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }

# ── Jetpack Compose ────────────────────────────────────────
-dontwarn androidx.compose.**
-keep class androidx.compose.** { *; }

# ── AndroidX ───────────────────────────────────────────────
-keep class androidx.lifecycle.** { *; }
-keep class androidx.navigation.** { *; }
-dontwarn androidx.navigation.**

# ── App-specific ───────────────────────────────────────────
-keep class com.poolmaintenance.app.data.** { *; }
-keep class com.poolmaintenance.app.di.** { *; }
-keep class com.poolmaintenance.app.PoolMaintenanceApp { *; }
-keep class com.poolmaintenance.app.MainActivity { *; }

# ── General optimizations ─────────────────────────────────
-optimizationpasses 5
-allowaccessmodification
-dontpreverify
-verbose
