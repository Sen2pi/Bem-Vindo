package pt.karimp.bem_vindo.utils

import android.media.MediaPlayer
import android.media.MediaRecorder
import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.appwrite.Client
import io.appwrite.models.InputFile
import io.appwrite.services.Storage
import kotlinx.coroutines.launch
import pt.karimp.bem_vindo.API.checkAndRequestPermissions
import pt.karimp.bem_vindo.R
import java.io.File
import java.util.UUID

@Composable
fun AudioRecorder(
    onRecordComplete: (filePath: String, audioUrl: String) -> Unit,
    bucketId: String
) {
    var isRecording by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val outputFilePath = "${context.filesDir}/audio_${System.currentTimeMillis()}.3gp"
    val appwrite = Client(context).setProject("674fb276000fbf815e06")
    val coroutineScope = rememberCoroutineScope()
    var mediaRecorder by remember { mutableStateOf<MediaRecorder?>(null) }
    checkAndRequestPermissions(context)
    val uuid = UUID.randomUUID().toString()

    // Função para reproduzir som de alerta
    fun playAlertSound(resourceId: Int) {
        val mediaPlayer = MediaPlayer.create(context, resourceId)
        mediaPlayer.start()
        mediaPlayer.setOnCompletionListener { it.release() }
    }

    // Upload de arquivo para Appwrite
    suspend fun uploadFile(bucketId: String, filePath: String) {
        val storage = Storage(appwrite)
        val file = InputFile.fromFile(File(filePath))
        try {
            val result = storage.createFile(
                bucketId = bucketId,
                fileId = uuid,
                file = file
            )
            val audioUrl = "https://cloud.appwrite.io/v1/storage/buckets/${bucketId}/files/${uuid}/view?project=674fb276000fbf815e06&project=674fb276000fbf815e06&mode=admin"
            Toast.makeText(context, "Áudio enviado com sucesso!", Toast.LENGTH_SHORT).show()
            onRecordComplete(filePath, audioUrl)
        } catch (e: Exception) {
            Toast.makeText(context, "Erro no upload do áudio: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // Iniciar gravação
    fun startRecording() {
        try {
            mediaRecorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                setOutputFile(outputFilePath)
                prepare()
                start()
            }
            isRecording = true
            playAlertSound(R.raw.start_sound) // Reproduz som de início
        } catch (e: Exception) {
            Toast.makeText(context, "Erro ao iniciar gravação: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // Parar gravação
    fun stopRecording() {
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            isRecording = false
            playAlertSound(R.raw.stop_sound) // Reproduz som de parada
            coroutineScope.launch { uploadFile(bucketId, outputFilePath) }
        } catch (e: Exception) {
            Toast.makeText(context, "Erro ao parar gravação: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // Botão de microfone
    IconButton(
        onClick = {
            if (isRecording) stopRecording() else startRecording()
        },
    ) {
        Icon(
            painter = painterResource(id = if (isRecording) R.mipmap.ic_stop else R.mipmap.ic_mic),
            contentDescription = if (isRecording) "Parar Gravação" else "Gravar Áudio",
            tint = Color.Unspecified,
            modifier = Modifier.size(43.dp)
        )
    }
}
