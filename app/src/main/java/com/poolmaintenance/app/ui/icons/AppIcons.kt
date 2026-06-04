package com.poolmaintenance.app.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * Custom icon definitions to replace material-icons-extended dependency.
 * Only includes the icons actually used by the app, saving ~3-4 MB of APK size.
 * Path data sourced from Material Design Icons (Apache 2.0 license).
 */
object AppIcons {

    val Pool: ImageVector by lazy {
        ImageVector.Builder(
            name = "Pool",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                pathFillType = PathFillType.NonZero
            ) {
                // Water waves at bottom
                moveTo(22f, 21f)
                curveTo(20.89f, 21f, 20.27f, 20.63f, 19.82f, 20.36f)
                curveTo(19.45f, 20.14f, 19.22f, 20f, 18.67f, 20f)
                curveTo(18.11f, 20f, 17.89f, 20.13f, 17.52f, 20.36f)
                curveTo(17.06f, 20.63f, 16.45f, 21f, 15.34f, 21f)
                curveTo(14.23f, 21f, 13.61f, 20.63f, 13.16f, 20.36f)
                curveTo(12.79f, 20.14f, 12.56f, 20f, 12.01f, 20f)
                curveTo(11.45f, 20f, 11.23f, 20.13f, 10.86f, 20.36f)
                curveTo(10.4f, 20.63f, 9.78f, 21f, 8.67f, 21f)
                curveTo(7.56f, 21f, 6.94f, 20.63f, 6.49f, 20.36f)
                curveTo(6.12f, 20.14f, 5.89f, 20f, 5.34f, 20f)
                curveTo(4.78f, 20f, 4.56f, 20.13f, 4.19f, 20.36f)
                curveTo(3.73f, 20.63f, 3.11f, 21f, 2f, 21f)
                verticalLineTo(19f)
                curveTo(2.56f, 19f, 2.78f, 18.87f, 3.15f, 18.64f)
                curveTo(3.61f, 18.37f, 4.23f, 18f, 5.34f, 18f)
                curveTo(6.45f, 18f, 7.07f, 18.37f, 7.52f, 18.64f)
                curveTo(7.89f, 18.86f, 8.12f, 19f, 8.67f, 19f)
                curveTo(9.23f, 19f, 9.45f, 18.87f, 9.82f, 18.64f)
                curveTo(10.28f, 18.37f, 10.9f, 18f, 12.01f, 18f)
                curveTo(13.12f, 18f, 13.74f, 18.37f, 14.19f, 18.64f)
                curveTo(14.56f, 18.86f, 14.79f, 19f, 15.34f, 19f)
                curveTo(15.9f, 19f, 16.12f, 18.87f, 16.49f, 18.64f)
                curveTo(16.95f, 18.37f, 17.57f, 18f, 18.68f, 18f)
                curveTo(19.79f, 18f, 20.41f, 18.37f, 20.86f, 18.64f)
                curveTo(21.23f, 18.86f, 21.46f, 19f, 22f, 19f)
                close()
                // Swimmer figure
                moveTo(7.5f, 14f)
                curveTo(8.33f, 14f, 9f, 13.33f, 9f, 12.5f)
                curveTo(9f, 11.67f, 8.33f, 11f, 7.5f, 11f)
                curveTo(6.67f, 11f, 6f, 11.67f, 6f, 12.5f)
                curveTo(6f, 13.33f, 6.67f, 14f, 7.5f, 14f)
                close()
                moveTo(19.5f, 12.5f)
                lineTo(14.5f, 9.5f)
                lineTo(19.5f, 6.5f)
                lineTo(14f, 3.5f)
                lineTo(11f, 5.17f)
                verticalLineTo(8.83f)
                lineTo(14f, 11.5f)
                lineTo(11f, 13.5f)
                verticalLineTo(16f)
                lineTo(14.5f, 14f)
                close()
            }
        }.build()
    }

    val Map: ImageVector by lazy {
        ImageVector.Builder(
            name = "Map",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(20.5f, 3f)
                lineTo(20.34f, 3.03f)
                lineTo(15f, 5.1f)
                lineTo(9f, 3f)
                lineTo(3.36f, 4.9f)
                curveTo(3.15f, 4.97f, 3f, 5.15f, 3f, 5.38f)
                verticalLineTo(20.5f)
                curveTo(3f, 20.78f, 3.22f, 21f, 3.5f, 21f)
                lineTo(3.66f, 20.97f)
                lineTo(9f, 18.9f)
                lineTo(15f, 21f)
                lineTo(20.64f, 19.1f)
                curveTo(20.85f, 19.03f, 21f, 18.85f, 21f, 18.62f)
                verticalLineTo(3.5f)
                curveTo(21f, 3.22f, 20.78f, 3f, 20.5f, 3f)
                close()
                moveTo(15f, 19f)
                lineTo(9f, 16.89f)
                verticalLineTo(5f)
                lineTo(15f, 7.11f)
                close()
            }
        }.build()
    }

    val BarChart: ImageVector by lazy {
        ImageVector.Builder(
            name = "BarChart",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(5f, 9.2f)
                horizontalLineTo(8f)
                verticalLineTo(19f)
                horizontalLineTo(5f)
                close()
                moveTo(10.6f, 5f)
                horizontalLineTo(13.4f)
                verticalLineTo(19f)
                horizontalLineTo(10.6f)
                close()
                moveTo(16.2f, 13f)
                horizontalLineTo(19f)
                verticalLineTo(19f)
                horizontalLineTo(16.2f)
                close()
            }
        }.build()
    }

    val CheckCircle: ImageVector by lazy {
        ImageVector.Builder(
            name = "CheckCircle",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(12f, 2f)
                curveTo(6.48f, 2f, 2f, 6.48f, 2f, 12f)
                reflectiveCurveToRelative(4.48f, 10f, 10f, 10f)
                reflectiveCurveToRelative(10f, -4.48f, 10f, -10f)
                reflectiveCurveTo(17.52f, 2f, 12f, 2f)
                close()
                moveTo(10f, 17f)
                lineToRelative(-5f, -5f)
                lineToRelative(1.41f, -1.41f)
                lineTo(10f, 14.17f)
                lineToRelative(7.59f, -7.59f)
                lineTo(19f, 8f)
                lineToRelative(-9f, 9f)
                close()
            }
        }.build()
    }

    val CircleOutlined: ImageVector by lazy {
        ImageVector.Builder(
            name = "CircleOutlined",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(12f, 2f)
                curveTo(6.47f, 2f, 2f, 6.47f, 2f, 12f)
                reflectiveCurveToRelative(4.47f, 10f, 10f, 10f)
                reflectiveCurveToRelative(10f, -4.47f, 10f, -10f)
                reflectiveCurveTo(17.53f, 2f, 12f, 2f)
                close()
                moveTo(12f, 20f)
                curveToRelative(-4.41f, 0f, -8f, -3.59f, -8f, -8f)
                reflectiveCurveToRelative(3.59f, -8f, 8f, -8f)
                reflectiveCurveToRelative(8f, 3.59f, 8f, 8f)
                reflectiveCurveToRelative(-3.59f, 8f, -8f, 8f)
                close()
            }
        }.build()
    }

    val Delete: ImageVector by lazy {
        ImageVector.Builder(
            name = "Delete",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(6f, 19f)
                curveToRelative(0f, 1.1f, 0.9f, 2f, 2f, 2f)
                horizontalLineToRelative(8f)
                curveToRelative(1.1f, 0f, 2f, -0.9f, 2f, -2f)
                verticalLineTo(7f)
                horizontalLineTo(6f)
                verticalLineTo(19f)
                close()
                moveTo(19f, 4f)
                horizontalLineToRelative(-3.5f)
                lineToRelative(-1f, -1f)
                horizontalLineToRelative(-5f)
                lineToRelative(-1f, 1f)
                horizontalLineTo(5f)
                verticalLineToRelative(2f)
                horizontalLineToRelative(14f)
                verticalLineTo(4f)
                close()
            }
        }.build()
    }

    val Science: ImageVector by lazy {
        ImageVector.Builder(
            name = "Science",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                pathFillType = PathFillType.NonZero
            ) {
                // Flask/beaker icon
                moveTo(13f, 2f)
                verticalLineToRelative(5.17f)
                lineToRelative(6f, 6f)
                verticalLineTo(19f)
                curveToRelative(0f, 1.1f, -0.9f, 2f, -2f, 2f)
                horizontalLineTo(7f)
                curveToRelative(-1.1f, 0f, -2f, -0.9f, -2f, -2f)
                verticalLineToRelative(-2.83f)
                lineToRelative(6f, -6f)
                verticalLineTo(2f)
                horizontalLineTo(13f)
                close()
                moveTo(15f, 2f)
                horizontalLineTo(9f)
                verticalLineToRelative(6f)
                lineToRelative(-5f, 5f)
                verticalLineTo(19f)
                curveToRelative(0f, 0.55f, 0.45f, 1f, 1f, 1f)
                horizontalLineToRelative(14f)
                curveToRelative(0.55f, 0f, 1f, -0.45f, 1f, -1f)
                verticalLineToRelative(-6f)
                lineToRelative(-5f, -5f)
                verticalLineTo(2f)
                close()
            }
        }.build()
    }

    val WaterDrop: ImageVector by lazy {
        ImageVector.Builder(
            name = "WaterDrop",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(12f, 2f)
                curveToRelative(0f, 0f, -7f, 7.69f, -7f, 12f)
                curveToRelative(0f, 3.87f, 3.13f, 7f, 7f, 7f)
                reflectiveCurveToRelative(7f, -3.13f, 7f, -7f)
                curveTo(19f, 9.69f, 12f, 2f, 12f, 2f)
                close()
                moveTo(12f, 19f)
                curveToRelative(-2.76f, 0f, -5f, -2.24f, -5f, -5f)
                curveToRelative(0f, -0.87f, 0.22f, -1.76f, 0.64f, -2.67f)
                curveToRelative(0.44f, -0.95f, 1.07f, -1.93f, 1.83f, -2.91f)
                curveTo(10.43f, 7.13f, 11.53f, 5.87f, 12f, 5.3f)
                curveToRelative(0.47f, 0.57f, 1.57f, 1.83f, 2.53f, 3.12f)
                curveToRelative(0.76f, 0.98f, 1.39f, 1.96f, 1.83f, 2.91f)
                curveTo(16.78f, 12.24f, 17f, 13.13f, 17f, 14f)
                curveTo(17f, 16.76f, 14.76f, 19f, 12f, 19f)
                close()
            }
        }.build()
    }
}
