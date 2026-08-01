package com.example.loudnessbooster.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlin.math.*

@Composable
fun CircularKnob(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    trackColor: Color = Color(0xFF2A2A3E),
    progressColor: Color = Color(0xFF7B61FF),
    thumbColor: Color = Color.White,
) {
    val startAngle = 135f
    val sweepRange = 270f

    Canvas(
        modifier = modifier
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val pos = change.position
                    val dx = pos.x - center.x
                    val dy = pos.y - center.y
                    var angle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
                    if (angle < 0) angle += 360f
                    var normalized = angle - startAngle
                    if (normalized < 0) normalized += 360f
                    if (normalized > sweepRange) normalized = normalized.coerceIn(0f, sweepRange)
                    val newValue = (normalized / sweepRange).coerceIn(0f, 1f)
                    onValueChange(newValue)
                }
            }
    ) {
        val stroke = 18.dp.toPx()
        val padding = stroke / 2 + 4.dp.toPx()
        val diameter = minOf(size.width, size.height) - padding * 2
        val topLeft = Offset((size.width - diameter) / 2, (size.height - diameter) / 2)
        val arcSize = Size(diameter, diameter)

        drawArc(
            color = trackColor,
            startAngle = startAngle,
            sweepAngle = sweepRange,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = stroke, cap = StrokeCap.Round)
        )

        drawArc(
            color = progressColor,
            startAngle = startAngle,
            sweepAngle = value * sweepRange,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = stroke, cap = StrokeCap.Round)
        )

        val thumbAngleRad = Math.toRadians((startAngle + value * sweepRange).toDouble())
        val radius = diameter / 2
        val center = Offset(size.width / 2f, size.height / 2f)
        val thumbPos = Offset(
            center.x + (radius * cos(thumbAngleRad)).toFloat(),
            center.y + (radius * sin(thumbAngleRad)).toFloat()
        )
        drawCircle(color = progressColor, radius = stroke * 0.7f, center = thumbPos)
        drawCircle(color = thumbColor, radius = stroke * 0.4f, center = thumbPos)
    }
}
