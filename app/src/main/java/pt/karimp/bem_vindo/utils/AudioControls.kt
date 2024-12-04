package pt.karimp.bem_vindo.utils
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import pt.karimp.bem_vindo.R

@Composable
fun AudioControls(audioUrls: String?, onPlay: (String) -> Unit, onDelete: (String) -> Unit) {
    if (audioUrls != null) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Botão Play
            IconButton(onClick = { onPlay(audioUrls) }) {
                Icon(
                    painter = painterResource(id = R.mipmap.ic_play),
                    contentDescription = "Reproduzir Áudio",
                    tint = Color.Unspecified
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Botão Eliminar
            IconButton(onClick = { onDelete(audioUrls) }) {
                Icon(
                    painter = painterResource(id = R.mipmap.ic_trash),
                    contentDescription = "Eliminar Áudio",
                    tint = Color.Unspecified
                )
            }
        }
    }
}
