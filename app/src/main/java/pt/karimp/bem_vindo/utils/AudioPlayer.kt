package pt.karimp.bem_vindo.utils

import android.media.MediaPlayer
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.karimp.bem_vindo.R

@Composable
fun AudioPlayer(audioUrl: String) {
    val mediaPlayer = remember { MediaPlayer() }
    var isPlaying by remember { mutableStateOf(false) }
    var duration by remember { mutableStateOf(0) }
    val context = LocalContext.current

    // Adicionando o listener para o fim do áudio e duração
    DisposableEffect(context) {
        mediaPlayer.setOnCompletionListener {
            isPlaying = false  // O áudio acabou, muda para "Play"
        }

        mediaPlayer.setOnPreparedListener {
            duration = mediaPlayer.duration // Captura a duração do áudio
        }

        onDispose {
            mediaPlayer.release() // Libera recursos ao sair da composição
        }
    }

    // Função para formatar a duração (em milissegundos) em minutos e segundos
    fun formatDuration(duration: Int): String {
        val minutes = (duration / 1000) / 60
        val seconds = (duration / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
            onClick = {
                try {
                    if (isPlaying) {
                        mediaPlayer.stop()
                        mediaPlayer.reset()
                        isPlaying = false
                        Toast.makeText(context, "Áudio pausado", Toast.LENGTH_SHORT).show()
                    } else {
                        mediaPlayer.reset()
                        mediaPlayer.setDataSource(audioUrl)
                        mediaPlayer.prepare()
                        mediaPlayer.start()
                        isPlaying = true
                        Toast.makeText(context, "Reproduzindo áudio", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Erro ao reproduzir áudio: ${e.message}", Toast.LENGTH_LONG).show()
                    e.printStackTrace()
                }
            }
        ) {
            Icon(
                painter = painterResource(id = if (isPlaying) R.mipmap.ic_pausa else R.mipmap.ic_play),
                contentDescription = if (isPlaying) "Pausar Áudio" else "Reproduzir Áudio",
                tint = Color.Unspecified
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Exibe a duração do áudio
        Text(
            text = formatDuration(duration),
            style = TextStyle(fontSize = 16.sp, color = Color.Black)
        )
    }
}
