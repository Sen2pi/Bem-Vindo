package pt.karimp.bem_vindo.utils

import com.google.firebase.Timestamp

fun formatTimestamp(timestamp: Timestamp?): String {
    val sdf = java.text.SimpleDateFormat(
        "d 'de' MMMM 'de' yyyy 'às' HH:mm:ss",
        java.util.Locale("pt", "pt")
    )
    return sdf.format(timestamp?.toDate())
}
