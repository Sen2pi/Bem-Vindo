package pt.karimp.bem_vindo.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.*
import pt.karimp.bem_vindo.R
import pt.karimp.bem_vindo.ui.theme.getUnmarkedClasses
import pt.karimp.bem_vindo.ui.theme.getUnreadMessages

fun sendNewMessageNotification(context: Context) {
    val channelId = "new_message_channel"
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // Criação do canal de notificação (necessário no Android 8+)
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId,
            "New Messages",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)
    }

    // Criação da notificação
    val notification: Notification = NotificationCompat.Builder(context, channelId)
        .setContentTitle("Nova mensagem recebida")
        .setContentText("Você tem uma nova mensagem no chat.")
        .setSmallIcon(R.mipmap.ic_notification) // Defina o ícone apropriado
        .build()

    // Enviar a notificação
    notificationManager.notify(0, notification)
}
fun sendmarkedClassNotification(context: Context) {
    val channelId = "marked_class_channel"
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // Criação do canal de notificação (necessário no Android 8+)
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId,
            "Marked Class",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)
    }

    // Criação da notificação
    val notification: Notification = NotificationCompat.Builder(context, channelId)
        .setContentTitle("Aluno Presente")
        .setContentText("Um Aluno marcou presença na sua aula")
        .setSmallIcon(R.mipmap.ic_notification) // Defina o ícone apropriado
        .build()
    // Enviar a notificação
    notificationManager.notify(0, notification)
}
fun sendAlunosSemProfessorNotification(context: Context) {
    val channelId = "aluno_sem_professor_channel"
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // Criação do canal de notificação (necessário no Android 8+)
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId,
            "Aluno sem Professor",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)
    }

    // Criação da notificação
    val notification: Notification = NotificationCompat.Builder(context, channelId)
        .setContentTitle("Aluno sem Professor")
        .setContentText("Tem alunos sem professores na lista")
        .setSmallIcon(R.mipmap.ic_notification) // Defina o ícone apropriado
        .build()
    // Enviar a notificação
    notificationManager.notify(0, notification)
}

fun sendUnmarkedClassNotification(context: Context) {
    val channelId = "unmarked_class_channel"
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // Criação do canal de notificação (necessário no Android 8+)
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId,
            "Unmarked Class",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)
    }

    // Criação da notificação
    val notification: Notification = NotificationCompat.Builder(context, channelId)
        .setContentTitle("Aula com presença por marcar")
        .setContentText("Você tem uma aula por marcar presença")
        .setSmallIcon(R.mipmap.ic_notification) // Defina o ícone apropriado
        .build()

    // Enviar a notificação
    notificationManager.notify(0, notification)
}

@Composable
fun MessageNotification(userId: String) {
    val context = LocalContext.current

    LaunchedEffect(userId) {
        // Ao encontrar uma nova mensagem não lida, envie uma notificação
        getUnreadMessages(userId) { count ->
            if (count > 0) {
                sendNewMessageNotification(context)
            }
        }
    }
}
@Composable
fun ClassNotification(userId: String) {
    val context = LocalContext.current

    LaunchedEffect(userId) {
        // Ao encontrar uma nova mensagem não lida, envie uma notificação
        getUnmarkedClasses(userId) { count ->
            if (count > 0) {
                sendUnmarkedClassNotification(context)
            }
        }
    }
}