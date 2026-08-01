package com.example.loudnessbooster.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.loudnessbooster.BoosterViewModel

private val BgDark = Color(0xFF0F0F1A)
private val Accent = Color(0xFF7B61FF)
private val AccentOff = Color(0xFF3A3A5C)
private val Surface = Color(0xFF1A1A2E)

@Composable
fun BoosterScreen(viewModel: BoosterViewModel) {
    val percent by viewModel.boostPercent.collectAsState()
    val enabled by viewModel.isEnabled.collectAsState()
    val gainMb = (percent * 15f).toInt()

    val knobColor by animateColorAsState(
        if (enabled) Accent else AccentOff,
        animationSpec = tween(400), label = "knob"
    )
    val buttonBg by animateColorAsState(
        if (enabled) Accent else AccentOff,
        animationSpec = tween(400), label = "btn"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(BgDark, Color(0xFF16162A)))
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "Loudness Booster",
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Text(
                    "Volume Enhancement",
                    color = Color.White.copy(alpha = 0.4f),
                    fontSize = 13.sp
                )
            }

            Box(contentAlignment = Alignment.Center) {
                CircularKnob(
                    value = percent / 100f,
                    onValueChange = { viewModel.setBoost(it * 100f) },
                    modifier = Modifier.size(260.dp),
                    progressColor = knobColor,
                    trackColor = Surface
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${percent.toInt()}%",
                        color = Color.White,
                        fontSize = 52.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Boost",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 14.sp
                    )
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = Surface),
                shape = MaterialTheme.shapes.medium
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 40.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "+$gainMb mB",
                        color = knobColor,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Ganancia actual",
                        color = Color.White.copy(alpha = 0.45f),
                        fontSize = 12.sp
                    )
                    Spacer(Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { percent / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(CircleShape),
                        color = knobColor,
                        trackColor = BgDark
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("0 mB", color = Color.White.copy(0.3f), fontSize = 10.sp)
                        Text("Limite seguro: 1500 mB", color = Color.White.copy(0.3f), fontSize = 10.sp)
                    }
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Button(
                    onClick = { viewModel.toggleEnabled() },
                    modifier = Modifier.size(80.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = buttonBg),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        if (enabled) "ON" else "OFF",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    if (enabled) "Amplificacion activa" else "Amplificacion desactivada",
                    color = if (enabled) knobColor else Color.White.copy(0.35f),
                    fontSize = 13.sp
                )
            }
        }
    }
}
