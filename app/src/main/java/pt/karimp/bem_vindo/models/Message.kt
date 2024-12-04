package pt.karimp.bem_vindo.models

import com.google.firebase.Timestamp

data class Message(
    val fromUserId: String = "",
    val toUserId: String = "",
    val message: String = "",
    val type: String = "text",
    val timestamp: Timestamp = Timestamp.now(),
    val read: Boolean = false,
    val audioUrl: String = "",
)
