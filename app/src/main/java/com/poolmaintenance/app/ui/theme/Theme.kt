package com.poolmaintenance.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PoolBlueLight,
    secondary = PoolTealLight,
    tertiary = SandDark,
    background = WaterDeep,
    surface = WaterDeep,
    onPrimary = WaterDeep,
    onSecondary = WaterDeep,
    onTertiary = WaterDeep,
    onBackground = WaterSurface,
    onSurface = WaterSurface
)

private val LightColorScheme = lightColorScheme(
    primary = PoolBlue,
    secondary = PoolTeal,
    tertiary = SandDark,
    background = WaterSurface,
    surface = WaterSurface,
    onPrimary = WaterSurface,
    onSecondary = WaterSurface,
    onTertiary = WaterDeep,
    onBackground = WaterDeep,
    onSurface = WaterDeep
)

@Composable
fun PoolMaintenanceTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
