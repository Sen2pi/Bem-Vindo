package pt.karimp.bem_vindo.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.karimp.bem_vindo.R
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.launch

@Composable
fun ChatInputSection(
    audioUrls: String?,
    messageText: TextFieldValue,
    onMessageTextChange: (TextFieldValue) -> Unit,
    deleteAudioFile: suspend (fileId: String) -> Unit, // The delete function is passed in
    onPlayAudio: @Composable (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    if (audioUrls != null) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Audio Player
            AudioPlayer(audioUrl = audioUrls)

            Spacer(modifier = Modifier.width(5.dp)) // Espaçamento entre os ícones

            // Delete Audio Button
            IconButton(onClick = {
                val fileId = extractFileId(audioUrls)
                if (fileId != null) {
                    coroutineScope.launch {
                        deleteAudioFile(fileId) // Delete the audio file asynchronously
                    }
                }
            }) {
                Icon(
                    painter = painterResource(id = R.mipmap.ic_trash),
                    contentDescription = "Delete Audio",
                    modifier = Modifier.size(50.dp),
                    tint = Color.Unspecified
                )
            }
        }
    } else {
        if (messageText.text.contains("...")) {
                Text(
                    text = messageText.text,
                    style = LocalTextStyle.current.copy(
                        color = Color.White,
                    ),
                )
        } else {
            BasicTextField(
                value = messageText,
                onValueChange = { onMessageTextChange(it) },
                modifier = Modifier
                    .height(50.dp)
                    .background(Color(0xFF81C784), RoundedCornerShape(32.dp)) // Verde claro
                    .padding(16.dp)
                    .width(240.dp),
                textStyle = LocalTextStyle.current.copy(
                    color = Color.White,
                    fontSize = 12.sp
                ),
                cursorBrush = SolidColor(Color.White),
                maxLines = 1
            )
        }
    }
}
