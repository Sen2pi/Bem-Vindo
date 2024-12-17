package pt.karimp.bem_vindo.utils

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import kotlin.random.Random
import pt.karimp.bem_vindo.R
import androidx.compose.runtime.remember
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.zIndex

@Composable
fun ConfettiRain() {
    val infiniteTransition = rememberInfiniteTransition()

    // Lista de partículas com posições e tamanhos aleatórios
    val confettiParticles = remember {
        List(1000) { // Aumentamos o número de partículas para 500 para criar o efeito de cascata
            ConfettiParticle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                color = Color(
                    Random.nextInt(256),
                    Random.nextInt(256),
                    Random.nextInt(256)
                ),
                size = Random.nextFloat() * 8.dp.value + 2.dp.value // Partículas menores e mais variadas
            )
        }
    }
    confettiParticles.forEach { particle ->
        val animatedY = infiniteTransition.animateFloat(
            initialValue = particle.y,
            targetValue = 1.5f, // Sai da tela
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 3000,
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Restart
            )
        ).value

        val animatedX = infiniteTransition.animateFloat(
            initialValue = particle.x,
            targetValue = Random.nextFloat() * 2f, // Movimento lateral aleatório
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 2500,
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Restart
            )
        ).value
    // Animação para mover as partículas para baixo e dar um efeito de rotação/variação
    Canvas(modifier = Modifier.fillMaxSize()) {
            // Desenho de cada partícula com variação de tamanho e rotação
            drawCircle(
                color = particle.color,
                radius = particle.size,
                center = Offset(animatedX * size.width, animatedY * size.height)
            )
        }
    }
}

data class ConfettiParticle(
    val x: Float,
    val y: Float,
    val color: Color,
    val size: Float
)



